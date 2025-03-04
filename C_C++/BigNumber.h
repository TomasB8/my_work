#pragma once
// here you can include whatever you want :)
#include <stdint.h>
#include <exception>
#include <cmath>

#include <string>
#include <vector>
#include <stdexcept>

// if you do not plan to implement bonus, you can delete those lines
// or just keep them as is and do not define the macro to 1
#define SUPPORT_IFSTREAM 0
#define SUPPORT_ISQRT 0
#define SUPPORT_EVAL 0  // special bonus

class BigInteger {
public:
	// constructors
	BigInteger() : isNegative(false) {};

	BigInteger(int64_t n) : isNegative(n < 0) {
		
		n = std::abs(n);
		while (n > 0) {
			digits.push_back(n % 10);
			n /= 10;
		}
		remove_leading_zeros();
	};

	explicit BigInteger(const std::string& str) {
		if (str.empty()) {
			throw std::invalid_argument("Empty string");
		}
		size_t i = 0;

		isNegative = false;

		if (str[0] == '-') {
			isNegative = true;
			i++;
		}
		else if (str[0] == '+') {
			i++;
		}
		for (; i < str.size(); i++) {
			if (str[i] < '0' || str[i] > '9') {
				throw std::invalid_argument("Invalid string");
			}
			// from standart number representation to little endian
			digits.insert(digits.begin(), str[i] - '0');
		}
		remove_leading_zeros();
	};

	// copy (default)
	BigInteger(const BigInteger& other) = default;
	BigInteger& operator=(const BigInteger& rhs) = default;
	// unary operators
	const BigInteger& operator+() const { return *this; };
	BigInteger operator-() const {
		BigInteger result = *this;
		if (*this != 0)
			result.isNegative = !result.isNegative;
		return result;
	};

	// binary arithmetics operators
	BigInteger& operator+=(const BigInteger& rhs) {
		if (isNegative == rhs.isNegative) {
			// Same sign
			uint8_t carry = 0;
			for (size_t i = 0; i < std::max(digits.size(), rhs.digits.size()) || carry; i++) {
				if (i == digits.size()) {
					digits.push_back(0);
				}
				digits[i] += carry + (i < rhs.digits.size() ? rhs.digits[i] : 0);

				// Carry handling
				carry = digits[i] >= 10;
				if (carry) {
					digits[i] -= 10;
				}
			}
		}
		else {
			// Different sign
			if (this->absolute_greater_than(rhs)) {
				// this this > rhs
				subtract_abs(rhs);
			}
			else {
				BigInteger temp = rhs;
				temp.subtract_abs(*this);
				*this = std::move(temp);
			}
		}

		remove_leading_zeros();
		return *this;
	};

	BigInteger& operator-=(const BigInteger& rhs) {
		*this += -rhs;
		remove_leading_zeros();
		return *this;
	};

	BigInteger& operator*=(const BigInteger& rhs) {
		if (digits.empty() || rhs.digits.empty()) {
			*this = 0;
			remove_leading_zeros(); // just in case
			return *this;
		}

		if (rhs == BigInteger(10)) {
			// Special case for multiplying by 10, (faster division)
			digits.insert(digits.begin(), 0);
			remove_leading_zeros();
			return *this;
		}

		std::vector<uint8_t> result(digits.size() + rhs.digits.size());
		for (size_t i = 0; i < digits.size(); ++i) {
			uint8_t carry = 0;
			for (size_t j = 0; j < rhs.digits.size(); ++j) {
				uint16_t cur = result[i + j] + static_cast<uint16_t>(digits[i]) * rhs.digits[j] + carry;
				result[i + j] = static_cast<uint8_t>(cur % 10);
				carry = static_cast<uint8_t>(cur / 10);
			}
			result[i + rhs.digits.size()] += carry;
		}

		digits = std::move(result);
		isNegative = isNegative != rhs.isNegative;
		remove_leading_zeros();
		return *this;
	};

	BigInteger& operator/=(const BigInteger& rhs) {
		if (rhs.digits.empty()) {
			throw std::runtime_error("Division by zero");
		}
		if (this->digits.empty() || rhs.absolute_greater_than(*this)) {
			*this = 0;
			remove_leading_zeros(); // desperation
			return *this;
		}

		bool fin_sign = isNegative != rhs.isNegative;
		BigInteger dividend = this->absolute();
		BigInteger divisor = rhs.absolute();
		BigInteger quotient = 0;
		BigInteger remainder = 0;

		// Loooong division
		for (auto it = dividend.digits.rbegin(); it != dividend.digits.rend(); ++it) { // iteration from MSB to LSB
			remainder *= 10;
			remainder += *it;

			uint8_t factor = 0;
			while (remainder >= divisor) {
				remainder -= divisor;
				++factor;
			}
			quotient = quotient * 10 + BigInteger(factor);
		}

		quotient.isNegative = fin_sign;
		*this = quotient;

		remove_leading_zeros();
		return *this;
	};

	BigInteger& operator%=(const BigInteger& rhs) {
		if (rhs.digits.empty()) {
			throw std::invalid_argument("Division by zero");
		}
		if (rhs.absolute_greater_than(*this)) {
			return *this;
		}

		BigInteger remainder = this->absolute(); // lazy copy
		BigInteger div = rhs.absolute();		// this is 100% gona bite me in the ass in rationals
		BigInteger scale = 1;
		while (remainder >= div * 10) {
			div *= 10;
			scale *= 10;
		}

		while (remainder.absolute_greater_than(rhs) || remainder == rhs.absolute()) {
			if (remainder >= div) {
				remainder -= div;
			}
			else {
				div /= 10;  // Scale down the divisor and the scale
				scale /= 10;
			}
		}

		*this = std::move(remainder);
		this->isNegative = false;

		remove_leading_zeros();
		return *this;
	};

	double sqrt() const {
		if (isNegative) {
			throw std::invalid_argument("Cannot sqrt negative number");
		}
		if (digits.empty()) {
			return 0;
		}
		double result = 0;
		for (size_t i = digits.size(); i > 0; i--) {
			result *= 10;
			result += digits[i - 1];
			if (std::isinf(result)) {
				throw std::runtime_error("Double overflow");
			}
		}
		return std::sqrt(result);
	};
#if SUPPORT_ISQRT == 1
	BigInteger isqrt() const;
#endif
private:

	std::vector<uint8_t> digits = {};
	bool isNegative = false;

	// Helper functions
	// Removes leading zeros from the number and makes 0 always positive
	void remove_leading_zeros() {
		while (!digits.empty() && digits.back() == 0) {
			digits.pop_back();
		}
		// Handle the case of the number being 0
		if (digits.empty()) {
			isNegative = false;  // A zero number should not be negative
		}
	}
	bool absolute_greater_than(const BigInteger& rhs) const {
		if (digits.size() != rhs.digits.size()) {
			return digits.size() > rhs.digits.size();
		}
		for (size_t i = digits.size(); i > 0; i--) {
			if (digits[i - 1] != rhs.digits[i - 1]) {
				return digits[i - 1] > rhs.digits[i - 1];
			}
		}
		return false;
	}
	void subtract_abs(const BigInteger& rhs) {
		// this function doesnt care about the sign
		// *this NEEDS TO BE GREATER than rhs
		uint8_t borrow = 0;
		for (size_t i = 0; i < std::max(digits.size(), rhs.digits.size()) || borrow; i++) {
			if (i == digits.size()) {
				digits.push_back(0);
			}
			digits[i] -= borrow + (i < rhs.digits.size() ? rhs.digits[i] : 0);

			// Carry handling
			borrow = digits[i] >= 10;
			if (borrow) {
				digits[i] += 10;
			}
		}
		remove_leading_zeros();
	};
	BigInteger absolute() const {
		// returns copy of the value , always positive, usefull in division, 
		// bc we need to work with the number later (that's why we use absolute_greater_than more)
		BigInteger abs_value = *this;
		abs_value.isNegative = false;
		return abs_value;
	};

	friend std::ostream& operator<<(std::ostream& lhs, const BigInteger& rhs);
	friend bool operator<(const BigInteger& lhs, const BigInteger& rhs);
	friend bool operator==(const BigInteger& lhs, const BigInteger& rhs);
	// functions used in division and multiplication for clarity
	friend BigInteger operator*(BigInteger lhs, const BigInteger& rhs);
	friend bool operator>=(const BigInteger& lhs, const BigInteger& rhs);
	friend BigInteger operator+(BigInteger lhs, const BigInteger& rhs);
	friend BigInteger operator-(BigInteger lhs, const BigInteger& rhs);
};

inline BigInteger operator+(BigInteger lhs, const BigInteger& rhs) {
	lhs += rhs;
	return lhs;
};

inline BigInteger operator-(BigInteger lhs, const BigInteger& rhs) {
	lhs -= rhs;
	return lhs;
};

inline BigInteger operator*(BigInteger lhs, const BigInteger& rhs) {
	lhs *= rhs;
	return lhs;
};
inline BigInteger operator/(BigInteger lhs, const BigInteger& rhs) {
	lhs /= rhs;
	return lhs;
};
inline BigInteger operator%(BigInteger lhs, const BigInteger& rhs) {
	lhs %= rhs;
	return lhs;
};


inline bool operator==(const BigInteger& lhs, const BigInteger& rhs) {
	if (lhs.isNegative != rhs.isNegative) {
		return false;
	}
	if (lhs.digits.size() != rhs.digits.size()) {
		return false;
	}
	for (size_t i = 0; i < lhs.digits.size(); i++) {
		if (lhs.digits[i] != rhs.digits[i]) {
			return false;
		}
	}
	return true;
};
inline bool operator!=(const BigInteger& lhs, const BigInteger& rhs) {
	return !(lhs == rhs);
};
inline bool operator<(const BigInteger& lhs, const BigInteger& rhs) {
	if (lhs.isNegative != rhs.isNegative) {
		return lhs.isNegative;
	}
	if (lhs.digits.size() != rhs.digits.size()) {
		if (lhs.isNegative) // Both numbers are negative
		{
			return lhs.digits.size() > rhs.digits.size();
		}
		else {
			return lhs.digits.size() < rhs.digits.size();
		}
	}
	for (size_t i = lhs.digits.size(); i > 0; i--) {
		if (lhs.digits[i - 1] != rhs.digits[i - 1]) {
			if (lhs.isNegative) // Both numbers are negative
			{
				return lhs.digits[i - 1] > rhs.digits[i - 1];
			}
			else {
				return lhs.digits[i - 1] < rhs.digits[i - 1];
			}
		}
	}
	return false;
};

inline bool operator>(const BigInteger& lhs, const BigInteger& rhs) {
	return rhs < lhs;
};

inline bool operator<=(const BigInteger& lhs, const BigInteger& rhs) {
	return !(lhs > rhs);
};
inline bool operator>=(const BigInteger& lhs, const BigInteger& rhs) {
	return !(lhs < rhs);
};

inline std::ostream& operator<<(std::ostream& lhs, const BigInteger& rhs) {
	if (rhs.isNegative) lhs << '-';
	if (rhs.digits.empty())
		lhs << 0;
	else {
		for (auto it = rhs.digits.rbegin(); it != rhs.digits.rend(); ++it) {
			lhs << static_cast<char>(*it + '0');
		}
	}
	return lhs;
};

#if SUPPORT_IFSTREAM == 1
// this should behave exactly the same as reading int with respect to
// whitespace, consumed characters etc...
inline std::istream& operator>>(std::istream& lhs, BigInteger& rhs);  // bonus
#endif




class BigRational {
public:
	// constructors
	BigRational() {
		numerator = 0;
		denominator = 1;
	};
	BigRational(int64_t a, int64_t b) {
		if (b == 0) {
			throw std::invalid_argument("Division by zero");
		}
		numerator = a;
		denominator = b;
		normalize();
	};
	BigRational(const std::string& a, const std::string& b) {
		if (b == "0") {
			throw std::invalid_argument("Division by zero");
		}
		numerator = BigInteger(a);
		denominator = BigInteger(b);
		normalize();
	};
	// copy
	BigRational(const BigRational& other) = default;
	BigRational& operator=(const BigRational& rhs) = default;
	// unary operators
	const BigRational& operator+() const { return *this; }
	BigRational operator-() const {
		BigRational result = *this; // first copy bc const
		result.numerator = -result.numerator;
		return result;
	}
	// binary arithmetics operators
	BigRational& operator*=(const BigRational& rhs) {
		numerator *= rhs.numerator;
		denominator *= rhs.denominator;
		normalize();
		return *this;
	};

	BigRational& operator/=(const BigRational& rhs) {
		if (rhs.numerator == 0) {
			throw std::invalid_argument("Division by zero");
		}
		numerator *= rhs.denominator;
		denominator *= rhs.numerator;
		normalize();
		return *this;
	};

	BigRational& operator+=(const BigRational& rhs) {
		// no need to multiply (not cheap) if denominators are equal
		if (this->denominator == rhs.denominator) {
			this->numerator += rhs.numerator;
		}
		else {
			// Perform the addition with cross-multiplication
			this->numerator = this->numerator * rhs.denominator + this->denominator * rhs.numerator;
			this->denominator *= rhs.denominator;
		}

		normalize();
		return *this;
	};
	BigRational& operator-=(const BigRational& rhs) {
		*this += -rhs;
		normalize();
		return *this;
	};

	double sqrt() const {
		if (numerator < 0) {
			throw std::invalid_argument("Cannot sqrt negative number");
		}
		// conversion from BigInteger to double happens in sqrt()
		return numerator.sqrt() / denominator.sqrt();
	};
#if SUPPORT_ISQRT == 1
	BigInteger isqrt() const;
#endif
private:
	// here you can add private data and members, but do not add stuff to
	// public interface, also you can declare friends here if you want
	BigInteger numerator;
	BigInteger denominator;

	// Friends :)
	friend std::ostream& operator<<(std::ostream& lhs, const BigRational& rhs);
	friend bool operator==(const BigRational& lhs, const BigRational& rhs);
	friend bool operator<(const BigRational& lhs, const BigRational& rhs);

	static BigInteger greatest_common_divisor(BigInteger a, BigInteger b) {
		if (a < 0) a = -a;
		if (b < 0) b = -b;
		while (b != 0) {
			a %= b;
			std::swap(a, b);
		}
		return a;
	}

	void normalize() {
		if (numerator == 0) {
			denominator = 1;
		}

		if (denominator < 0) {
			numerator = -numerator;
			denominator = -denominator;
		}
		
		BigInteger gcd = greatest_common_divisor(numerator, denominator);
		numerator /= gcd;
		denominator /= gcd;
	}
};

inline BigRational operator+(BigRational lhs, const BigRational& rhs) {
	lhs += rhs;
	return lhs;
};

inline BigRational operator-(BigRational lhs, const BigRational& rhs) {
	lhs -= rhs;
	return lhs;
};

inline BigRational operator*(BigRational lhs, const BigRational& rhs) {
	lhs *= rhs;
	return lhs;
};

inline BigRational operator/(BigRational lhs, const BigRational& rhs) {
	lhs /= rhs;
	return lhs;
};

inline bool operator==(const BigRational& lhs, const BigRational& rhs) {
	return lhs.numerator == rhs.numerator && lhs.denominator == rhs.denominator;
};
inline bool operator!=(const BigRational& lhs, const BigRational& rhs) {
	return !(lhs == rhs);
};
inline bool operator<(const BigRational& lhs, const BigRational& rhs) {
	if (lhs.denominator == rhs.denominator) {
		return lhs.numerator < rhs.numerator;
	}
	return lhs.numerator * rhs.denominator < rhs.numerator* lhs.denominator;
};
inline bool operator>(const BigRational& lhs, const BigRational& rhs) {
	return rhs < lhs;
};
inline bool operator<=(const BigRational& lhs, const BigRational& rhs) {
	return !(lhs > rhs);
};
inline bool operator>=(const BigRational& lhs, const BigRational& rhs) {
	return !(lhs < rhs);
};

inline std::ostream& operator<<(std::ostream& lhs, const BigRational& rhs) {
	// all numbers should be already stored in irreducible form (hopefully)
	
	lhs << rhs.numerator;
	if (rhs.denominator != 1) {
		lhs << '/' << rhs.denominator;
	}
	return lhs;
};

#if SUPPORT_IFSTREAM == 1
// this should behave exactly the same as reading int with respect to
// whitespace, consumed characters etc...
inline std::istream& operator>>(std::istream& lhs, BigRational& rhs);  // bonus
#endif

#if SUPPORT_EVAL == 1
inline BigInteger eval(const std::string&);
#endif