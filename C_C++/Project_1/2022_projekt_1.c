#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// funkcia, ktora vymiena cele cisla pre bubblesort
void vymen_int(long int *x1, long int *x2){
    long int tmp = *x1;
    *x1 = *x2;
    *x2 = tmp;
}

// funkcia, ktora vymiena retazce pre bubblesort
void vymen_string(char *x1, char *x2){
    char *tmp = x1;
    x1 = x2;
    x2 = tmp;
}

// funkcia, ktora vymiena realne cisla pre bubblesort
void vymen_double(double *x1, double *x2){
    double tmp = *x1;
    *x1 = *x2;
    *x2 = tmp;
}

// funkcia, ktora zoraduje prvky v poliach podla datumu a casu vzostupne
void bubbleSort(int pocet_prvkov, char **modul, char **velicina, long int *datum, long int *cas, double *hodnota){
    // prechadzame vsetky merania
    for (int i = 0; i < pocet_prvkov - 1; i++)
        for (int j = 0; j < pocet_prvkov - i - 1; j++){
            // ak je nasledujuci datum vacsi, vymeni ich
            if (datum[j] > datum[j + 1]){
                vymen_string(&(*modul)[j], &(*modul)[j + 1]);
                vymen_string(&(*velicina)[j], &(*velicina)[j + 1]);
                vymen_double(&hodnota[j], &hodnota[j + 1]);
                vymen_int(&cas[j], &cas[j + 1]);
                vymen_int(&datum[j], &datum[j + 1]);
            // ak sa datumy rovnaju, porovnava sa cas
            }else if(datum[j] == datum[j + 1]){
                if(&cas[j] > &cas[j+1]){
                    vymen_string(&(*modul)[j], &(*modul)[j + 1]);
                    vymen_string(&(*velicina)[j], &(*velicina)[j + 1]);
                    vymen_double(&hodnota[j], &hodnota[j + 1]);
                    vymen_int(&cas[j], &cas[j + 1]);
                    vymen_int(&datum[j], &datum[j + 1]);
                }
            }
        }
                
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora vypise zaznamy merania:
    1. ak boli vytvorene dynamicke polia, vypise ich z poli
    2 ak neboli vytvorene dynamicke polia, vypise zaznamy z poli
*/
int funkcia_v(FILE** fr, int pocet_merani, long long int *id_cislo, char **mer_modul, char **typ_mer_veliciny, double *hodnota, char **cas_merania, long int *datum){
    if(*fr == NULL){
        if ((*fr = fopen("dataloger.txt", "r")) == NULL) {
            printf("Neotvoreny subor.\n");
            return 0;
        }
    }

    // zistovanie ci boli dynamicke polia vytvorene
    if(id_cislo!=NULL && mer_modul!=NULL && typ_mer_veliciny!=NULL && hodnota!=NULL && cas_merania!=NULL && datum!=NULL){
        // vypis zaznamov z poli
        for(int i=0; i<pocet_merani; i++){
            printf("ID cislo mer. osoby: %lld\n", id_cislo[i]);
            printf("Mer.modul: %s\n", mer_modul[i]);
            printf("Typ mer. veliciny: %s\n", typ_mer_veliciny[i]);
            printf("Hodnota: %f\n", hodnota[i]);
            printf("Cas merania: %s\n", cas_merania[i]);
            printf("Datum: %ld\n", datum[i]);
            printf("\n");
        }
    }else{
        char pole[10];

        // nacitanie a vypisanie zaznamov zo suboru
        while(1){
            if(fscanf(*fr, "%s", pole)!=1) break;
            printf("ID cislo mer. osoby: %s\n", pole);
            fscanf(*fr, "%s", pole);
            printf("Mer.modul: %s\n", pole);
            fscanf(*fr, "%s", pole);
            printf("Typ mer. veliciny: %s\n", pole);
            fscanf(*fr, "%s", pole);
            printf("Hodnota: %s\n", pole);
            fscanf(*fr, "%s", pole);
            printf("Cas merania: %s\n", pole);
            fscanf(*fr, "%s", pole);
            printf("Datum: %s\n", pole);
            printf("\n");
        }    
    }
    // obnovenie pointera na zaciatok suboru
    rewind(*fr);
    return 1;
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora po aktivovani nacita mer. modul a typ mer.veliciny
a vypise zoznam hodnot zoradenych podla casu a datumu merania
*/
int funkcia_o(FILE** fr, int pocet_merani, long long int *id_cislo, char **mer_modul, char **typ_mer_veliciny, double *hodnota, char **cas_merania, long int *datum){
    // zistovanie, ci bol subor otvoreny
    if(*fr == NULL){
        printf("Neotvoreny subor.\n");
        return 0;
    }

    char modul[4];
    char typ_veliciny[3];
    int poradie = 0;
    char c;

    // v pripade, ze pocet merani este nebol zisteny, nacita ho
    if(pocet_merani == 0){
        while((c=getc(*fr)) != EOF){
            if(c == '\n'){
                pocet_merani++;
            }
        }
        pocet_merani = (pocet_merani+1)/7;
        // obnovenie pointera na zaciatok suboru
        rewind(*fr);
    }

    // vytvorenie dynamickych poli pre zaznamy, ktore sa budu zoradovat
    char** modul_sort = malloc(pocet_merani * sizeof(char*));
    for(int i=0; i<pocet_merani; i++) modul_sort[i] = malloc(4*sizeof(char)); 
    char** velicina_sort = malloc(pocet_merani * sizeof(char*));
    for(int i=0; i<pocet_merani; i++) velicina_sort[i] = malloc(3*sizeof(char));
    long int *datum_sort = malloc(pocet_merani * sizeof(long int));
    long int *cas_sort = malloc(pocet_merani * sizeof(long int));
    double *hodnota_sort = malloc(pocet_merani * sizeof(double));

    // nacitanie modulu a typu veliciny zo vstupu
    scanf("%s %s", modul, typ_veliciny);

    // blok, ktory sa vykona, ak uz boli dynamicke polia vytvorene
    if(id_cislo!=NULL && mer_modul!=NULL && typ_mer_veliciny!=NULL && hodnota!=NULL && cas_merania!=NULL && datum!=NULL){
        // for-cyklus, ktory prechadza vsetky merania
        for(int i=0; i<pocet_merani; i++){
            int podmienka1=1, podmienka2=1;     //nastavenie podmienok defaultne na 1

            // for cyklus, ktory porovnava retazce a zistuje, ci plati prva podmienka
            for(int j=0; j<3; j++){
                if(modul[j] != mer_modul[i][j]){
                    podmienka1 = 0;
                    break;
                }
            }

            // v pripade, ze prva podmienka stale plati, kontroluje sa aj druha podmienka
            if(podmienka1==1){
                for(int k=0; k<2; k++){
                    if(typ_veliciny[k] != typ_mer_veliciny[i][k]){
                        podmienka2 = 0;
                        break;
                    }
                }
            }

            // ak platia obidve podmienky, zaznam sa vlozi do pola na zoradenie
            if(podmienka1==1 && podmienka2==1){
                modul_sort[poradie] = mer_modul[i];
                velicina_sort[poradie] = typ_mer_veliciny[i];
                datum_sort[poradie] = datum[i];
                cas_sort[poradie] = atoi(cas_merania[i]);
                hodnota_sort[poradie] = hodnota[i];
                poradie++;
            }
        }

        // zavolanie funkcie bubblesort, ktora zoradi polia, ktore sa nasledne for-cyklom vypisu
        bubbleSort(poradie, modul_sort, velicina_sort, datum_sort, cas_sort, hodnota_sort);
        for(int i=0; i<poradie; i++){
            printf("%s %s %ld %04ld %lf\n", modul_sort[i], velicina_sort[i], datum_sort[i], cas_sort[i], hodnota_sort[i]);
        }
    // blok, ktory sa vykona, ak dynamicke polia este neboli vytvorene
    }else{
        char c1;
        int pozicia = 0;
        poradie = 0;

        // while cyklus, ktory prechadza subor a sucasne kontroluje, ci su retazce rovnake
        while(1){
            // deklaracia premennych
            char id[11]; char m_modul[4]; char velicina[3];
            double hodnota;
            long int datum; long int cas;
            int podmienka1=1, podmienka2=1;

            // nacitanie hodnot zo suboru do premennych
            if(fscanf(*fr, "%s", id)!=1) break;
            fscanf(*fr, "%s", m_modul);
            fscanf(*fr, "%s", velicina);
            fscanf(*fr, "%lf", &hodnota);
            fscanf(*fr, "%ld", &cas);
            fscanf(*fr, "%ld", &datum);

            // for cyklus, ktory porovnava retazce a zistuje, ci plati prva podmienka
            for(int i=0; i<3; i++){
                if(modul[i] != m_modul[i]){
                    podmienka1 = 0;
                    break;
                }
            }
            
            // v pripade, ze prva podmienka stale plati, kontroluje sa aj druha podmienka
            if(podmienka1==1){
                for(int i=0; i<2; i++){
                    if(typ_veliciny[i] != velicina[i]){
                        podmienka2 = 0;
                        break;
                    }
                }
            }

            // ak platia obidve podmienky, zaznam sa vlozi do pola na zoradenie
            if(podmienka1==1 && podmienka2==1){
                modul_sort[poradie] = modul;
                velicina_sort[poradie] = typ_veliciny;
                datum_sort[poradie] = datum;
                cas_sort[poradie] = cas;
                hodnota_sort[poradie] = hodnota;

                poradie++;
            }
        }

        // zavolanie funkcie bubblesort, ktora zoradi polia, ktore sa nasledne for-cyklom vypisu
        bubbleSort(poradie, modul_sort, velicina_sort, datum_sort, cas_sort, hodnota_sort);
        for(int i=0; i<poradie; i++){
            printf("%s %s %ld %04ld %lf\n", modul_sort[i], velicina_sort[i], datum_sort[i], cas_sort[i], hodnota_sort[i]);
        }
    }

    // dealokovanie poli na zoradovanie
    free(modul_sort);
    modul_sort = NULL;
    free(velicina_sort);
    velicina_sort = NULL;
    free(hodnota_sort);
    hodnota_sort = NULL;
    free(cas_sort);
    cas_sort = NULL;
    free(datum_sort);
    datum_sort = NULL;

    // obnovenie pointeru na zaciatok suboru
    rewind(*fr);
    return 1;
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora spocita pocet zaznamov v subore, dynamicky vytvori 
polia pre jednotlive polozky zo vstupu
*/
int funkcia_n(FILE** fr, int *p_pocet_merani, long long int **u_id_cislo, char ***u_mer_modul, char ***u_typ_mer_veliciny, double **u_hodnota, char ***u_cas_merania, long int **u_datum){
    // zistovanie, ci bol subor otvoreny
    if(*fr == NULL){
        printf("Neotvoreny subor.\n");
        return 0;
    }

    // v pripade, ze uz boli polia vytvorene, najskor ich dealokuje a potom vytvori nove
    if(*u_id_cislo!=NULL && *u_mer_modul!=NULL && *u_typ_mer_veliciny!=NULL && *u_hodnota!=NULL && *u_cas_merania!=NULL && *u_datum!=NULL){
        free(*u_id_cislo);
        *u_id_cislo = NULL;
        free(*u_mer_modul);
        *u_mer_modul = NULL;
        free(*u_typ_mer_veliciny);
        *u_typ_mer_veliciny = NULL;
        free(*u_hodnota);
        *u_hodnota = NULL;
        free(*u_cas_merania);
        *u_cas_merania = NULL;
        free(*u_datum);
        *u_datum = NULL;
    }

    char c;
    int pocet_riadkov = 0;
    int j = 0;

    // while cyklus, ktory spocita pocet zaznamov v subore
    while((c=getc(*fr)) != EOF){
        if(c == '\n'){
            pocet_riadkov++;
        }
    }
    *p_pocet_merani = (pocet_riadkov+1)/7;
    // obnovenie pointeru na zaciatok suboru
    rewind(*fr);

    // vytvorenie dynamickych poli pre jednotlive zaznamy
    *u_id_cislo = malloc(*p_pocet_merani * sizeof(long long int));
    *u_mer_modul = malloc(*p_pocet_merani * sizeof(char*));
    for(int i=0; i<*p_pocet_merani; i++) (*u_mer_modul)[i] = malloc(10*sizeof(char)); 
    *u_typ_mer_veliciny = malloc(*p_pocet_merani * sizeof(char*));
    for(int i=0; i<*p_pocet_merani; i++) (*u_typ_mer_veliciny)[i] = (char *)malloc(10*sizeof(char));
    *u_hodnota = malloc(*p_pocet_merani * sizeof(double));
    *u_cas_merania = malloc(*p_pocet_merani * sizeof(char*));
    for(int i=0; i<*p_pocet_merani; i++) (*u_cas_merania)[i] = (char *)malloc(10*sizeof(char));
    *u_datum = malloc(*p_pocet_merani * sizeof(long int));

    // naplnenie dynamickych poli hodnotami zo suboru
    while(1){
        if(fscanf(*fr, "%lld", &(*u_id_cislo)[j])!=1) break;
        fscanf(*fr, "%s", (*u_mer_modul)[j]);
        fscanf(*fr, "%s", (*u_typ_mer_veliciny)[j]);
        fscanf(*fr, "%lf", &(*u_hodnota)[j]);
        fscanf(*fr, "%s", (*u_cas_merania)[j]);
        fscanf(*fr, "%ld", &(*u_datum)[j]);
        
        j++;
    }

    // obnovenie pointeru na zaciatok suboru
    rewind(*fr);
    return 1;
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora po aktivovani skontroluje udaje v textovom subore
*/
int funkcia_c(FILE** fr){
    // zistovanie, ci bol subor otvoreny
    if(*fr == NULL){
        printf("Neotvoreny subor.\n");
        return 0;
    }

    char c1;
    int chyba = 0;      // premenna, ktora hovori, ci je v subore nejaka chyba

    // vytvorenie premennych pre jednotlive zaznamy
    long long int id;
    char modul[4];
    char velicina[3];
    double hodnota;
    char cas[5];
    long int datum;


    while(1){
        //Kontrola ID cisla mer. osoby
        if(fscanf(*fr, "%lld", &id)!=1) break;
        long long int pom = id;
        int pocet_cifier = 1;
        
        // zistovanie poctu cifier
        while(pom/10 != 0){
            pom /= 10;
            pocet_cifier++;
        }

        if(id%11 != 0 || pocet_cifier != 10){
            printf("Nekorektne zadany vstup: %lld\n", id);
            chyba = 1;
        }

        //Kontrola mer. modulu
        fscanf(*fr, "%s", modul);
        int znak = modul[0];
        int cislo1 = modul[1] - '0';
        int cislo2 = modul[2] - '0';
        if((znak<'A' || znak>'Z') || (cislo1<0 || cislo1>9) || (cislo2<0 || cislo2>9) || !cislo1 || !cislo2){
            printf("Nekorektne zadany vstup: %s\n", modul);
            chyba = 1;
        }

        //Kontrola typu mer. veliciny
        fscanf(*fr, "%s", velicina);
        char *x[] = {"R1", "U1", "A1", "R2", "U2", "A2", "R4", "U4", "A4", 0};
        int i = 0;
        int rovnake = 0;
        while(x[i]) {
            if(strcmp(x[i], velicina) == 0) {
                rovnake = 1;
                break;
            }
            i++;
        }
        if(rovnake == 0){
            printf("Nekorektne zadany vstup: %s\n", velicina);
            chyba = 1;
        }    

        //Kontrola hodnoty
        if(fscanf(*fr, "%lf", &hodnota) != 1){
            printf("Nekorektne zadany vstup: %lf\n", hodnota);
            chyba = 1;
        }   
        
        //Kontrola casu
        fscanf(*fr, "%s", cas);
        int hodiny[] = {(int)cas[0]-'0', (int)cas[1]-'0'};
        int minuty[] = {(int)cas[2]-'0', (int)cas[3]-'0'};
        if(hodiny[0]>=0 && hodiny[0]<=2 && hodiny[1]>=0 && hodiny[1]<=9){
            if(hodiny[0] == 2){
                if(hodiny[1]>3){
                    printf("Nekorektne zadany vstup: %s\n", cas);
                    chyba = 1;
                }
            }
        }else{
            printf("Nekorektne zadany vstup: %s\n", cas);
            chyba = 1;
        }
        if(minuty[0]<0 || minuty[0]>5 || minuty[1]<0 || minuty[1]>9){
            printf("Nekorektne zadany vstup: %s\n", cas);
            chyba = 1;
        }    
        
        //Kontrola datumu
        fscanf(*fr, "%ld", &datum);
        long int pom_datum = datum;
        int dni = pom_datum%100;
        int mesiac = (pom_datum/100)%100;
        int rok = (pom_datum/100)/100;
        int pocet_cislic = 1;

        while(pom_datum/10 != 0){
            pom_datum /= 10;
            pocet_cislic++;
        }

        if(pocet_cislic != 8 || dni<1 || dni>31 || mesiac<1 || mesiac>12 || rok<=0){
            printf("Nekorektne zadany vstup: %ld\n", datum);
            chyba = 1;
        }      
    }

    // ak nie je chyba, vypise sa hlaska
    if(chyba==0){
        printf("Data su korektne.\n");
    }

    // obnovenie pointeru na zaciatok suboru
    rewind(*fr);
    return 1;
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora nacita mer. modul a typ mer. veliciny a vytvori subor s nameranymi
hodnotami, zoradenymi podla datumu a casu
*/
int funkcia_s(int pocet_merani, long long int *id_cislo, char **mer_modul, char **typ_mer_veliciny, double *hodnota, char **cas_merania, long int *datum){
    FILE* fw = fopen("vystup_S.txt", "w");      // pointer na novy subor

    // kontrola ci su polia vytvorene
    if(id_cislo==NULL && mer_modul==NULL && typ_mer_veliciny==NULL && hodnota==NULL && cas_merania==NULL && datum==NULL){
        printf("Polia nie su vytvorene.\n");
        return 0;
    }else{
        char modul[4];
        char typ_veliciny[3];
        int poradie = 0;

        // vytvorenie dynamickych poli pre zaznamy, ktore sa budu zoradovat
        char** modul_sort = malloc(pocet_merani * sizeof(char*));
        for(int i=0; i<pocet_merani; i++) modul_sort[i] = malloc(4*sizeof(char)); 
        char** velicina_sort = malloc(pocet_merani * sizeof(char*));
        for(int i=0; i<pocet_merani; i++) velicina_sort[i] = malloc(3*sizeof(char));
        long int *datum_sort = malloc(pocet_merani * sizeof(long int));
        long int *cas_sort = malloc(pocet_merani * sizeof(long int));
        double *hodnota_sort = malloc(pocet_merani * sizeof(double));

        // nacitanie modulu a typu mer. veliciny zo vstupu
        scanf("%s %s", modul, typ_veliciny);

        // for-cyklus, ktory prechadza vsetky merania
        for(int i=0; i<pocet_merani; i++){
            int podmienka1=1, podmienka2=1;

            // for cyklus, ktory porovnava retazce a zistuje, ci plati prva podmienka
            for(int j=0; j<3; j++){
                if(modul[j] != mer_modul[i][j]){
                    podmienka1 = 0;
                    break;
                }
            }

            // v pripade, ze prva podmienka stale plati, kontroluje sa aj druha podmienka
            if(podmienka1==1){
                for(int k=0; k<2; k++){
                    if(typ_veliciny[k] != typ_mer_veliciny[i][k]){
                        podmienka2 = 0;
                        break;
                    }
                }
            }

            // ak platia obidve podmienky, zaznam sa vlozi do pola na zoradenie
            if(podmienka1==1 && podmienka2==1){
                modul_sort[poradie] = mer_modul[i];
                velicina_sort[poradie] = typ_mer_veliciny[i];
                datum_sort[poradie] = datum[i];
                cas_sort[poradie] = atoi(cas_merania[i]);
                hodnota_sort[poradie] = hodnota[i];
                poradie++;
            }
        }

        // ak je aspon jeden taky zaznam, zoradeny sa vlozi do suboru
        if(poradie != 0){
            printf("Pre dany vstup je vytvoreny txt subor.\n");
            bubbleSort(poradie, modul_sort, velicina_sort, datum_sort, cas_sort, hodnota_sort);
            for(int i=0; i<poradie; i++){
                fprintf(fw, "%ld%04ld\t %.7lf\n", datum_sort[i], cas_sort[i], hodnota_sort[i]);
            }
        }else{
            printf("Pre dany vstup neexistuju zaznamy.\n");
        }

        // dealokovanie poli na zoradovanie
        free(modul_sort);
        modul_sort = NULL;
        free(velicina_sort);
        velicina_sort = NULL;
        free(hodnota_sort);
        hodnota_sort = NULL;
        free(cas_sort);
        cas_sort = NULL;
        free(datum_sort);
        datum_sort = NULL;
    }
    // zatvorenie suboru
    fclose(fw);
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora po aktivovani vypise histogram pre typ mer. veliciny
*/
int funkcia_h(int pocet_merani, long long int *id_cislo, char **mer_modul, char **typ_mer_veliciny, double *hodnota, char **cas_merania, long int *datum){
    // kontrola ci boli polia uy vytvorene
    if(id_cislo==NULL && mer_modul==NULL && typ_mer_veliciny==NULL && hodnota==NULL && cas_merania==NULL && datum==NULL){
        printf("Polia nie su vytvorene.\n");
        return 0;
    }else{
        int pocetnost[100] = {0};
        char velicina[3];

        // nacitanie typu mer. veliciny zo vstupu
        scanf("%s", velicina);

        // pripocitanie hodnoty do pola pocetnosti v pripade, ze vstup sa rovna typu mer. veliciny
        for(int i=0; i<pocet_merani; i++){
            if(strcmp(velicina, typ_mer_veliciny[i]) == 0){
                int pozicia = hodnota[i]/5;
                pocetnost[pozicia]++;
            }
        }

        // vypis histogramu
        printf("     %s\t\t     pocetnost\n", velicina);
        for(int j=0; j<100; j++){
            if(pocetnost[j] != 0){
                printf("(%5.1f-%6.1f>\t\t%d\n", j*5.0, j*5.0+5.0, pocetnost[j]);
            }
        }
    }
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora po aktivovani analyzuje casy merania a vypise ich v redukovanom tvare
*/
int funkcia_r(int pocet_merani, long long int *id_cislo, char **mer_modul, char **typ_mer_veliciny, double *hodnota, char **cas_merania, long int *datum){
    // kontrola, ci uz boli polia vytvorene
    if(id_cislo==NULL && mer_modul==NULL && typ_mer_veliciny==NULL && hodnota==NULL && cas_merania==NULL && datum==NULL){
        printf("Polia nie su vytvorene.\n");
        return 0;
    }else{
        // vytvorenie poli
        int casy[24][60] = {{0}};
        int casy_pocet[24] = {0};

        // naplnenie poli
        for(int i=0; i<pocet_merani; i++){
            int x = atoi(cas_merania[i]);
            casy[x/100][x%100] = 1;
            casy_pocet[x/100]++;
        }

        // vypisovanie casov do konzoly
        for(int m=0; m<24; m++){
            int pocet = 1;
            if(casy_pocet[m] != 0){
                printf("%02d:", m);
                for(int n=0; n<60; n++){
                    if(casy[m][n] == 1){
                        if(pocet != 1)  printf(", ");
                        printf("%02d", n);
                        pocet++;
                    }
                }
                printf("\n");
            }  
            
        }
    }
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora nacita ID cislo mer. osoby a nasledne vymaze zaznamy s tymto ID
*/
int funkcia_z(int *pocet_merani, long long int **id_cislo, char ***mer_modul, char ***typ_mer_veliciny, double **hodnota, char ***cas_merania, long int **datum){
    // kontrola, ci uz boli polia vytvorene
    if((*id_cislo)==NULL && (*mer_modul)==NULL && (*typ_mer_veliciny)==NULL && (*hodnota)==NULL && (*cas_merania)==NULL && (*datum)==NULL){
        printf("Polia nie su vytvorene.\n");
        return 0;
    }else{
        long long int zadane_id;
        int pocet_zmazanych = 0;

        // nacitanie ID zo vstupu
        scanf("%lld", &zadane_id);

        // prehladavanie zaznamov so zadanym ID
        for(int i=0; i<*pocet_merani; i++){
            // ak sa ID rovnaju, vsetky nasledujuce zaznamy sa posunu
            while(zadane_id == (*id_cislo)[i]){
                for(int j=i; j<*pocet_merani; j++){
                    (*id_cislo)[j] = (*id_cislo)[j+1];
                    (*mer_modul)[j] = (*mer_modul)[j+1];
                    (*typ_mer_veliciny)[j] = (*typ_mer_veliciny)[j+1];
                    (*hodnota)[j] = (*hodnota)[j+1];
                    (*cas_merania)[j] = (*cas_merania)[j+1];
                    (*datum)[j] = (*datum)[j+1];
                }
                // pripocita sa pocet zmazanych zaznamov
                pocet_zmazanych++;
            }
        }
        // vypis hlasky
        printf("Zmazalo sa: %d zaznamov !\n", pocet_zmazanych);
        // zadanie noveho poctu merani (bez zmazanych zaznamov)
        *pocet_merani = *pocet_merani-pocet_zmazanych;
    }
}

int funkcia_j(int pocet_merani, long long int *id_cislo, char **mer_modul, char **typ_mer_veliciny, double *hodnota, char **cas_merania, long int *datum){
    char mer_modul1[4];
    char mer_modul2[4];
    char typ_vel[3];
    int poradie = 0;

    char** modul_sort = (char **)malloc(pocet_merani * sizeof(char*));
    for(int i=0; i<pocet_merani; i++) modul_sort[i] = (char *)malloc(4*sizeof(char)); 
    char** velicina_sort = (char **)malloc(pocet_merani * sizeof(char*));
    for(int i=0; i<pocet_merani; i++) velicina_sort[i] = (char *)malloc(3*sizeof(char));
    long int *datum_sort = (long int *)malloc(pocet_merani * sizeof(long int));
    long int *cas_sort = (long int *)malloc(pocet_merani * sizeof(long int));
    double *hodnota_sort = (double *)malloc(pocet_merani * sizeof(double));

    scanf("%s %s %s", mer_modul1, mer_modul2, typ_vel);
    printf("%s %s %s", mer_modul1, mer_modul2, typ_vel);

    // for-cyklus, ktory prechadza vsetky merania
    for(int i=0; i<pocet_merani; i++){
        int podmienka = 1;

        // for cyklus, ktory porovnava retazce a zistuje, ci plati prva podmienka
        for(int j=0; j<3; j++){
            if(mer_modul1[j] != mer_modul[i][j]){
                podmienka = 0;
                break;
            }
        }

        // ak platia obidve podmienky, zaznam sa vlozi do pola na zoradenie
        if(podmienka == 1){
            modul_sort[poradie] = mer_modul[i];
            velicina_sort[poradie] = typ_mer_veliciny[i];
            datum_sort[poradie] = datum[i];
            cas_sort[poradie] = atoi(cas_merania[i]);
            hodnota_sort[poradie] = hodnota[i];
            poradie++;
        }
    }

    if(poradie != 0){
        bubbleSort(poradie, modul_sort, velicina_sort, datum_sort, cas_sort, hodnota_sort);
        for(int i=0; i<10; i++){
            printf("%lf\n", hodnota_sort[i]);
        }
    }
}

// ---------------------------------------------------------------------------------------------------
int main(){
    // deklaracia premennych
    FILE* fr = NULL;
    char c;
    int pocet_merani = 0;
    int *p_pocet_merani = &pocet_merani;

    // vytvorenie poli s NULL pointerom
    long long int *id_cislo = NULL;
    char **mer_modul = NULL;
    char **typ_mer_veliciny = NULL;
    double *hodnota = NULL;
    char **cas_merania = NULL;
    long int *datum = NULL;

    // while-loop kym sa nezada k
    while(c != 'k'){
        // nacitanie charakteru zo vstupu
        scanf("%c", &c);
        // switch, ktory zavola funkciu na zaklade zadaneho pismena
        switch(c){
            case 'v':
                funkcia_v(&fr, pocet_merani, id_cislo, mer_modul, typ_mer_veliciny, hodnota, cas_merania, datum);
                break;
            
            case 'o':
                funkcia_o(&fr, pocet_merani, id_cislo, mer_modul, typ_mer_veliciny, hodnota, cas_merania, datum);
                break;

            case 'n':
                funkcia_n(&fr, p_pocet_merani, &id_cislo, &mer_modul, &typ_mer_veliciny, &hodnota, &cas_merania, &datum);
                break;

            case 'c':
                funkcia_c(&fr);
                break;

            case 's':
                funkcia_s(pocet_merani, id_cislo, mer_modul, typ_mer_veliciny, hodnota, cas_merania, datum);
                break;

            case 'h':
                funkcia_h(pocet_merani, id_cislo, mer_modul, typ_mer_veliciny, hodnota, cas_merania, datum);
                break;

            case 'r':
                funkcia_r(pocet_merani, id_cislo, mer_modul, typ_mer_veliciny, hodnota, cas_merania, datum);
                break;

            case 'z':
                funkcia_z(&pocet_merani, &id_cislo, &mer_modul, &typ_mer_veliciny, &hodnota, &cas_merania, &datum);
                break;

            case 'j':
                funkcia_j(pocet_merani, id_cislo, mer_modul, typ_mer_veliciny, hodnota, cas_merania, datum);
                break;
        }
    }
    
    // zatvorenie suboru
    if(fr != NULL){
        fclose(fr);
    }

    // dealokovanie poli, ak boli alokovane
    if(id_cislo!=NULL && mer_modul!=NULL && typ_mer_veliciny!=NULL && hodnota!=NULL && cas_merania!=NULL && datum!=NULL){
        free(id_cislo);
        id_cislo = NULL;
        for(int i=0; i<pocet_merani; i++){
            free(mer_modul[i]);
            mer_modul[i] = NULL;
        }
        free(mer_modul);
        mer_modul = NULL;
        for(int i=0; i<pocet_merani; i++){
            free(typ_mer_veliciny[i]);
            typ_mer_veliciny[i] = NULL;
        }
        free(typ_mer_veliciny);
        typ_mer_veliciny = NULL;
        free(hodnota);
        hodnota = NULL;
        for(int i=0; i<pocet_merani; i++){
            free(cas_merania[i]);
            cas_merania[i] = NULL;
        }
        free(cas_merania);
        cas_merania = NULL;
        free(datum);
        datum = NULL;
    }

    return 0;
}