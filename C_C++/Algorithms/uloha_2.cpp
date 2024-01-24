#include <iostream>
using namespace std;

const int d = 4;			// najvyssi deadline

typedef int index;
typedef index set_pointer;

/*
Struktura reprezentujuca prvky v mnozine.
*/
struct node_type {
	index parent;
	int depth;
	int smallest;
	int job_number;
};

typedef node_type universe[d+1];
universe U;

// Funkcia, ktora vytvara mnozinu.
void makeset(index i) {
	U[i].parent = i;			// spojenie s inou mnozinou
	U[i].depth = 0;				// hlbka
	U[i].smallest = i;			// najmensi prvok v mnozine
	U[i].job_number = 0;		// cislo prace v danom uzle
}

// Pridanie prace do uzla.
void add_job(index i, int job) {
	U[i].job_number = job;
}

/*
Funkcia, ktora spaja dve disjunktne mnoziny prvkov.
*/
void merge(set_pointer p, set_pointer q) {
	if (U[p].depth == U[q].depth) {
		U[q].parent = p;
		if (U[q].smallest < U[p].smallest) {
			U[p].smallest = U[q].smallest;
		}
		while (U[p].parent != p) {
			U[p].smallest = U[q].smallest;
			U[p].depth = U[p].depth + 1;
			p = U[p].parent;
		} 
		U[p].smallest = U[q].smallest;
		U[p].depth = U[p].depth + 1;
	}
	else if (U[p].depth < U[q].depth) {
		U[q].parent = p;
		U[p].depth = U[q].depth + 1;
		if (U[p].smallest > U[q].smallest) {
			U[p].smallest = U[q].smallest;
		}
		while (U[p].parent != p) {
			U[p].smallest = U[q].smallest;
			U[p].depth = U[p].depth + 1;
			p = U[p].parent;
		}
		U[p].smallest = U[q].smallest;
		U[p].depth = U[p].depth + 1;
	}
}

// Funkcia, ktora vracia najmeni prvok z mnoziny.
int small(set_pointer p) {
	return U[p].smallest;
}

/*
Funkcia, ktora rozdeli pole a vrati pivotny prvok.
*/
int partition(int deadline[], int profits[], int jobs[], int low, int high) {
    int pivot = profits[high];
    int i = low - 1;

    for (int j = low; j < high; j++) {
        if (profits[j] >= pivot) {
            i++;
            swap(profits[i], profits[j]);
            swap(deadline[i], deadline[j]);
            swap(jobs[i], jobs[j]);
        }
    }

    swap(profits[i + 1], profits[high]);
    swap(deadline[i + 1], deadline[high]);
    swap(jobs[i + 1], jobs[high]);

    return i + 1;
}

/*
Funkcia, ktora implementuje QuickSort s časovou zložitosťou O(n log(n))
*/
void quickSort(int deadline[], int profits[], int jobs[], int low, int high) {
    if (low < high) {
        int pivotIndex = partition(deadline, profits, jobs, low, high);

        quickSort(deadline, profits, jobs, low, pivotIndex - 1);
        quickSort(deadline, profits, jobs, pivotIndex + 1, high);
    }
}

/*
Funkcia, ktora zoradi polia deadline, profits a jobs zostupne na zaklade pola profits
*/
void ordering(int n, int deadline[], int profits[], int jobs[]) {
    quickSort(deadline, profits, jobs, 0, n - 1);
}

/*
Funkcia, ktora najde optimalne vykonavanie.
*/
void schedule(int n, int *total_profit, int deadline[], int profit[]){
	for (int i = 0; i < n; i++) {
		if(small(deadline[i]) != 0){
			int j = deadline[i];
			while (j >= 0){
				if(U[j].job_number == 0){
					U[j].job_number = i + 1;
					break;
				}
				j--;
			}
			*total_profit += profit[i];
			merge(small(deadline[i]), small(deadline[i]) - 1);
		}
	}
}

int main() {
	int max_profit;
	int n = 7;
	int total_profit = 0;								// profit z vykonavania prac
	int jobs[] = {1, 2, 3, 4, 5, 6, 7};					// poradie jednotlivych prac
	int deadline[] = { 2, 4, 3, 2, 3, 1, 1 };			// deadliny jednotlivych prac
	int profit[] = { 40, 15, 60, 20, 10, 45, 55 };		// profity jednotlivych prac

	// vytvorenie d+1 disjunktnych mnozin
	for (int i = 0; i < d+1; i++) {
		makeset(i);
	}

	ordering(n, deadline, profit, jobs);				// zoradenie podla profit
	schedule(n, &total_profit, deadline, profit);		// planovanie prac

	for (int i = 1; i <= d; i++) {
		cout << jobs[U[i].job_number-1] << " ";
	}
	cout << endl;
	cout << "Max profit: " << total_profit << endl;

	
	return 0;
}
