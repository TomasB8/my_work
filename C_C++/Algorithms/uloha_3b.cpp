#include <iostream>
#include <climits>
using namespace std;

#define N 3

/*
Funkcia, ktora najde najmensi prvok v riadku i.
*/
int findMinInRow(int matrix[][N], int i){
    int minRow = INT_MAX;
    for(int j=0; j<N; j++){
        if (minRow > matrix[i][j]){
            minRow = matrix[i][j];
        }
    }
    return minRow;
}

/*
Funkcia, ktora najde najmensi riadok v stlpci j.
*/
int findMinInCol(int matrix[][N], int j){
    int minCol = INT_MAX;
    for(int i=0; i<N; i++){
        if(minCol > matrix[i][j]){
            minCol = matrix[i][j];
        }
    }
    return minCol;
}

/*
Funcia, ktora odcita min od kazdeho prvku v riadku i.
*/
void subtractRow(int matrix[][N], int min, int i, int rowsZeros[]){
    for(int j=0; j<N; j++){
        matrix[i][j] -= min;
        if(matrix[i][j] == 0){
            rowsZeros[i] += 1;      //ukladanie poctu 0 v jednotlivych riadkoch
        }
    }
}

/*
Funkcia, ktora odcita min od kazdeho prvku v stlpci j.
*/
void subtractCol(int matrix[][N], int min, int j, int rowsZeros[]){
    int previous = -1;
    for(int i=0; i<N; i++){
        previous = matrix[i][j];
        matrix[i][j] -= min;
        if(matrix[i][j] == 0){
            if (previous != 0)
                rowsZeros[i] += 1;
        }
    }
}

/*
Funkcia, ktora kontroluje, ci stlpec j obsahuje nejake nuly.
*/
bool hasZero(int mask[][N], int j){
    for(int i=0; i<N; i++){
        if(mask[i][j] == -1)
            return true;
    }
    return false;
}

/*
Funkcia, ktora zamaskuje 0, co najmensim poctom ciar.
*/
bool mask_zeros(int rowsZeros[], int matrix[][N], int mask[][N]){
    int lines = 0;
    //najskor zamaskuje riadky, ale len take, v ktorych je viac ako 1 nula
    for(int i=0; i<N; i++){
        if(rowsZeros[i] > 1){
            lines += 1;
            for(int j=0; j<N; j++){
                if(mask[i][j] == -1){
                    mask[i][j] = 1;
                }else{
                    mask[i][j] += 1;
                }
            }
        }
    }
    //potom zamaskuje stlpce, v ktorych este ostali nejake nuly
    for(int i=0; i<N; i++){
        if(hasZero(mask, i)){
            lines += 1;
            for(int j=0; j<N; j++){
                if(mask[j][i] == -1){
                    mask[j][i] = 1;
                }else{
                    mask[j][i] += 1;
                }
            }
        }
        
    }
    if(lines == N){
        return true;    //ak sa pocet ciar rovna n, vrati true
    }else{
        return false;   //inak false
    }
}

/*
Funkcia, ktora priraduje prace jednotlivym ludom.
*/
void assignJobs(int assignment[], int matrix[][N], int rowsZeros[]){
    for(int i=0; i<N; i++){
        for(int j=0; j<N; j++){
            if(matrix[i][j] == 0){
                if(rowsZeros[i] == 1)
                    assignment[j] = i+1;
                else{
                    if (assignment[j] == 0){
                        assignment[j] = i+1;
                    }
                }
            }
        }
    }
}

int main(){
    int matrix[][N] = {{10,5,5}, {2,4,10}, {5,1,7}};
    int assignment[N] = {0};
    int* rowsZeros = new int[N]();      //pocet 0 v jednotlivych riadkoch

    //for-cyklus, v ktorom sa od kazdeho prvku v kazdom riadku odcita najmensia hodnota z riadku
    for(int i=0; i<N; i++){
        int min = findMinInRow(matrix, i);
        subtractRow(matrix, min, i, rowsZeros);
    }

    //for-cyklus, v ktorom sa od kazdeho prvku v kazdom stlpci odcita najmensia hodnota zo stlpca
    for(int j=0; j<N; j++){
        int min = findMinInCol(matrix, j);
        subtractCol(matrix, min, j, rowsZeros);
    }
    
    //bude sa vykonavat az kym nebude minimalny pocet ciar na pokrytie 0 rovny n 
    while(true){
        int minimum = INT_MAX;
        int mask[][N] = { {0, 0, 0} ,{0, 0, 0}, {0, 0, 0} };
        //okopirovanie 0 do masky, miesta na ktorch je 0, sa nahradia -1 v maske
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                if(matrix[i][j] == 0)
                    mask[i][j] = -1;
                else
                    mask[i][j] = 0;
            }
        }
        if (mask_zeros(rowsZeros, matrix, mask))
            break;
        
        //najde sa najmensi prvok z nepokrytych elementov
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                if(mask[i][j] == 0 && matrix[i][j]<minimum)
                    minimum = matrix[i][j];
            }
        }
        
        //najmensi prvok sa odcita od vsetkych nepokrytych prvkov a pripocita sa ku vsetkym, ktore su pokryte dvakrat
        for(int i=0; i<N; i++){
            for(int j=0; j<N; j++){
                if(mask[i][j] == 0)
                    matrix[i][j] -= minimum;
                else if(mask[i][j] == 2)
                    matrix[i][j] += minimum;
            }
        }
    }
    
    assignJobs(assignment, matrix, rowsZeros);      //pridelenie prace ludom
    for(int i=1; i<=N; i++){
        cout << "P" << assignment[i-1] << ": J" << i << endl;
    }

    return 0;
}