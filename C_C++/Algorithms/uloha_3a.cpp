#include <iostream>
using namespace std;

#define N 3

/*
Funkcia, ktora najde v matici najvacsi prvok.
*/
int findMax(int table[][N]){
	int max = 0;
	for(int i=0; i<N; i++){
		for(int j=0; j<N; j++){
			if(table[i][j] > max){
				max = table[i][j];
			}
		}
	}
	return max;
}

/*
Funkcia, ktora najde v matici dalsi najmensi prvok.
*/
void findMinimum(int table[][N], int used[][N], int* x, int* y, int previous, int max) {
	int min = max;			// ulozenie najvacsieho cisla z matice
	int pos_x = 0;
	int pos_y = 0;
	for (int i = 0; i < N; i++) {
		for (int j = 0; j < N; j++) {
			if (table[i][j] <= min && table[i][j] >= previous && used[i][j] == 0) {
				min = table[i][j];
				pos_x = i;
				pos_y = j;
			}
		}
	}
	// ulozenie suradnic najmensieho prvku v matici
	*x = pos_x;
	*y = pos_y;
}

/*
Funkcia, ktora zisti, ci je pole K feasible
*/
bool isFeasible(int n, int *K) {
	for (int i = 0; i < n; i++) {
		for (int j = i + 2; j < n; j += 2) {
			if (K[i] == K[j]) {
				return false;
			}
		}
	}
	return true;
}

/*
Funkcia, ktora optimalne priradi prace ludom.
*/
void assign(int table[][N], int* J[], int max) {
	int i;
	int x, y;
	int last = 0;
	int poradie = 2;
	int* K = new int[N * 2];			// kazda praca zabera 2 miesta v K, pre riadok aj stlpec
	int used[N][N] = {{0}};

	findMinimum(table, used, &x, &y, last, max);		// najdenie najmensieho prvku
	last = table[x][y];
	// ulozenie prveho prvku do J
	*(*J + 0) = x;
	*(*J + 1) = y;
	// prechazda nasledujuce prvky
	for (int i = 0; i < N * N; i++) {
		copy(*J + 0, *J + poradie, K);				// K = J
		findMinimum(table, used, &x, &y, last, max);			// najdenie nasledujuceho najmensieho prvku
		last = table[x][y];
		used[x][y] = 1;
		// vlozenie najdeneho prvku do K
		K[poradie] = x;
		K[poradie+1] = y;
		if (isFeasible(poradie+2, K)) {
			poradie += 2;
			copy(K + 0, K + poradie, *J);		// ak K je feasible, tak J = K
		}
		// ukoncenie po naplneni pola
		if (poradie == 2*N) {
			break;
		}
	}
}

int main() {
	int table[][N] = { {10, 5, 5} ,{2, 4, 10}, {5, 1, 7} };		// matica, riadky - ludia, stlpce - prace
	int* J = new int[N*2];
	int max = findMax(table);

	assign(table, &J, max);			// pridelenie prace ludom

	for (int i = 0; i < 2 * N; i+=2) {
		cout << "P" << J[i] + 1 << ": J" << J[i + 1]+1 << endl;
	}

	return 0;
}