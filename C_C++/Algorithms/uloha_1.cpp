#include<iostream>
using namespace std;

/*
Funkcia, ktora zoradi polia deadline, profits a jobs zostupne na zaklade pola profits
*/
void ordering(int n, int deadline[], int profits[], int jobs[]) {
	for (int i = 1; i < n; ++i) {
		int profitKey = profits[i];
		int deadlineKey = deadline[i];
		int jobKey = jobs[i];
		int j = i - 1;

		// Posunie elementy z profits[0...i-1], deadlines[0...i-1] a jobs[0...i-1] na zaklade profitu, ktory je vacsi ako profitKey
		while (j >= 0 && profits[j] < profitKey) {
			profits[j + 1] = profits[j];
			deadline[j + 1] = deadline[j];
			jobs[j + 1] = jobs[j];
			j--;
		}

		profits[j + 1] = profitKey;
		deadline[j + 1] = deadlineKey;
		jobs[j + 1] = jobKey;
	}
}

/*
Funkcia, ktora zoradi pole K na zaklade deadline.
Je to potrebne pre zistovanie, ci je K feasible.
*/
void orderByDeadlines(int n, int* K, const int deadline[]) {
	int* ds = new int[n];
	// Vyfiltrovanie len deadlinov, ktore potrebujeme.
	for (int i = 0; i < n; i++) {
		ds[i] = deadline[K[i]-1];
	}

	for (int i = 1; i < n; ++i) {
		int dsKey = ds[i];
		int jobKey = K[i];
		int j = i - 1;

		// Posunie elementy z ds[0...i-1] a K[0...i-1] na zaklade deadline, ktory je vacsi ako dsKey
		while (j >= 0 && ds[j] > dsKey) {
			ds[j + 1] = ds[j];
			K[j + 1] = K[j];
			j--;
		}

		ds[j + 1] = dsKey;
		K[j + 1] = jobKey;
	}
	delete[] ds;
}

/*
Funkcia, ktora zisti, ci je pole K feasible
*/
bool isFeasible(int n, int *K, const int deadline[]) {
	int d = 1;
	for (int i = 0; i < n; i++) {
		if (deadline[K[i] - 1] < d) {
			return false;
		}
		d++;
	}
	return true;
}


/*
Funkcia, ktora najde optimalne vykonavanie.
*/
void schedule(int n, const int deadline[], int* J[]) {
	int i, poradie;
	int* K = new int[n];

	*J[0] = 1;											// J[0] = 1
	poradie = 1;
	for (i = 2; i <= n; i++) {
		copy(*J + 0, *J + poradie, K);					// K = J
		K[poradie] = i;
		orderByDeadlines(poradie + 1, K, deadline);		// zoradenie pola K na zaklade deadlinov, aby sme vedeli lahsie zistit, ci je feasible
		if (isFeasible(poradie+1, K, deadline)) {
			poradie++;
			copy(K + 0, K + poradie, *J);				// ak je K feasible, tak J = K
		}
	}	
	delete[] K;
}

int main() {
	int max_profit;										// profit
	int n = 7;											// pocet prac
	int jobs[] = {1, 2, 3, 4, 5, 6, 7};					// poradie jednotlivych prac
	int deadline[] = {2, 4, 3, 2, 3, 1, 1};				// deadline jednotlivych prac
	int profit[] = { 40, 15, 60, 20, 10, 45, 55 };		// profity jednotlivych prac
	int* J = new int[n]();

	ordering(n, deadline, profit, jobs);		// usporiadanie podľa profitu zostupne

	schedule(n, deadline, &J);			// planovanie
	max_profit = 0;
	for (int i = 0; i < n; i++) {
		if(J[i] != 0){
			cout << jobs[J[i]-1] << " ";
			max_profit += profit[J[i] - 1];
		}else{
			break;
		}
	}
	cout << endl << "Max profit: " << max_profit << endl;

	delete[] J;
	return 0;
}
