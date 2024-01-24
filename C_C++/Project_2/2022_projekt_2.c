#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//definicia struktury zaznamu so vsetkymi polozkami
typedef struct zaznam{
    long long int id_osoby;
    char meno_osoby[30];
    char mer_modul[5];
    char typ_mer_veliciny[3];
    double hodnota;
    char cas_merania[5];
    long int datum;
    struct zaznam *dalsi;
} ZAZNAM;

// funkcia, ktora sluzi na vymenenie dvoch zaznamov v spajanom zozname
void vymen(ZAZNAM *a, ZAZNAM *b) { 
    //nacitanie poloziek zo struktury a do pomocnych premennych
    long long int pom_id = a->id_osoby;
    char pom_meno[30];
    strcpy(pom_meno, a->meno_osoby);
    char pom_modul[5];
    strcpy(pom_modul, a->mer_modul);
    char pom_velicina[3];
    strcpy(pom_velicina, a->typ_mer_veliciny);
    double pom_hodnota = a->hodnota;
    char pom_cas[5];
    strcpy(pom_cas, a->cas_merania);
    long int pom_datum = a->datum;

    //vymenenie poloziek zo struktury b do struktury a
    a->id_osoby = b->id_osoby;
    strcpy(a->meno_osoby, b->meno_osoby);
    strcpy(a->mer_modul, b->mer_modul);
    strcpy(a->typ_mer_veliciny, b->typ_mer_veliciny);
    a->hodnota = b->hodnota;
    strcpy(a->cas_merania, b->cas_merania);
    a->datum = b->datum;

    //vlozenie poloziek z pomocnych premennych do struktury b
    b->id_osoby = pom_id;
    strcpy(b->meno_osoby, pom_meno);
    strcpy(b->mer_modul, pom_modul);
    strcpy(b->typ_mer_veliciny, pom_velicina);
    b->hodnota = pom_hodnota;
    strcpy(b->cas_merania, pom_cas);
    b->datum = pom_datum;
} 

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora nacita zaznamy z meracieho modulu do spajaneho
zoznamu struktur
*/
int funkcia_n(FILE **fr, ZAZNAM **zaciatok, int *p_pocet_zaznamov){
    //kontrola, ci sa da otvorit zadany subor
    if(*fr == NULL){
        if ((*fr = fopen("dataloger_V2.txt", "r")) == NULL) {
            printf("Zaznamy neboli nacitane!\n");
            return 0;
        }
    }
    
    //ak bol zoznam uz vytvoreny, najskor sa uvolni pamat
    if(zaciatok != NULL){
        while(*zaciatok != NULL){
            free(*zaciatok);
            *zaciatok = (*zaciatok)->dalsi;
        }
        *zaciatok = NULL;
    }

    //spocitanie zaznamov v subore podla poctu $$$
    char retazec[20];       //pomocna premenna
    *p_pocet_zaznamov = 0;
    while(fscanf(*fr,"%s",retazec)!=EOF){
        if(strcmp(retazec, "$$$")==0){
            *p_pocet_zaznamov += 1;
        }
    }
    rewind(*fr);        //obnovenie ukazovatela na zaciatok suboru

    ZAZNAM *aktualny = *zaciatok;

    //nacitanie jednotlivych zaznamov do zoznamu
    for(int i=0; i<*p_pocet_zaznamov; i++){
        ZAZNAM *vrchol = (ZAZNAM*)malloc(sizeof(ZAZNAM));       //premenna typu ZAZNAM, ktora sa naplni hodnotami
        char pole[4];
        char meno[30];

        fscanf(*fr, "%s", pole)!=1;
        fscanf(*fr, "%lld ", &(vrchol->id_osoby));
        fgets((vrchol->meno_osoby), 30, *fr);
        fscanf(*fr, "%s", (vrchol->mer_modul));
        fscanf(*fr, "%s", (vrchol->typ_mer_veliciny));
        fscanf(*fr, "%lf", &(vrchol->hodnota));
        fscanf(*fr, "%s", (vrchol->cas_merania));
        fscanf(*fr, "%ld", &(vrchol->datum));
        vrchol->dalsi = NULL;

        //ak je zoznam prazdny, tento vrchol sa prida na zaciatok zoznamu
        if(aktualny == NULL){
            aktualny = vrchol;
            *zaciatok = vrchol;
        }else{
            //ak nie zaznam sa prida za posledny zaznam v zozname
            aktualny->dalsi = vrchol;
            aktualny = aktualny->dalsi;
        }
    }
    printf("Nacitalo sa %d zaznamov.\n", *p_pocet_zaznamov);
    rewind(*fr);
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora vypisuje obsah celeho spajaneho zoznamu zaznamov
*/
int funkcia_v(ZAZNAM *zaciatok){
    int poradie = 1;
    ZAZNAM *p_aktualny = zaciatok;  //vytvorenie ukazovatela na aktualny prvok v zozname

    //ak je spajany zoznam vytvoreny, vykona sa nasledujuci blok kodu
    if(zaciatok != NULL){
        //kym nepride na koniec zoznamu, vypisuje vsetky udaje
        while(p_aktualny != NULL){
            printf("%d:\n",poradie);
            printf("ID cislo mer. osoby: %lld\n", p_aktualny->id_osoby);
            printf("Meno osoby: %s", p_aktualny->meno_osoby);
            printf("Mer. modul: %s\n", p_aktualny->mer_modul);
            printf("Typ mer. veliciny: %s\n", p_aktualny->typ_mer_veliciny);
            printf("Hodnota: %lf\n", p_aktualny->hodnota);
            printf("Cas merania: %s\n", p_aktualny->cas_merania);
            printf("Datum: %ld\n", p_aktualny->datum);

            //posunutie ukazovatela na dalsi prvok
            p_aktualny = p_aktualny->dalsi;
            poradie++;  //zvysenie poradia o 1
        }
    }
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia na zmazanie zaznamov podla zadaneho ID
*/
int funkcia_z(ZAZNAM **u_u_zaciatok, int *pocet_zaznamov){
    long long int zadane_id;
    ZAZNAM *p_aktualny, *p_predchadzajuci, *pom;    //vytvorenie pomocnych premennych
    int i = 0;

    //nacitanie ID zo vstupu
    scanf("%lld", &zadane_id);

    //ak nie je vytvoreny spajany zoznam, funkcia skonci
    if (!(*u_u_zaciatok))
        return 0;

    //while-cyklus, ktory sa vykonava ak sa ID prveho prvku v zozname rovna zadanemu ID
    while ((*u_u_zaciatok) && (*u_u_zaciatok)->id_osoby == zadane_id){
        pom = (*u_u_zaciatok);  //nastavenie pomocnej premennej na zaciatok zoznamu
        printf("Zaznam pre ID: %lld pre modul %s bol vymazany.\n", (*u_u_zaciatok)->id_osoby, (*u_u_zaciatok)->mer_modul);
        //zaciatok sa nastavi na nasledujuci prvok
        (*u_u_zaciatok) = (*u_u_zaciatok)->dalsi;
        free(pom);  //uvolni sa pamat
        (*pocet_zaznamov)--;
    }
    //nastavenie pomocnych ukazovatelov na zoznam
    p_aktualny = *u_u_zaciatok;
    p_predchadzajuci = NULL;
    //while-cyklus, ktory sa vykonava kym nepride na koniec zoznamu
    while (p_aktualny) {
        // ak sa ID rovnaju, vymaze sa dany prvok a uvolni sa pamat
        if (p_aktualny->id_osoby == zadane_id){
            pom = p_aktualny;
            printf("Zaznam pre ID: %lld pre modul %s bol vymazany.\n", p_aktualny->id_osoby, p_aktualny->mer_modul);
            p_predchadzajuci->dalsi = p_aktualny->dalsi;
            free(pom);
            (*pocet_zaznamov)--;
        }else{
            //ak sa nerovnaju, ukazovatel na predchadzajuci prvok sa nastavi na aktualny
            p_predchadzajuci = p_aktualny;
        }
        //posunutie ukazovatela na nasledujuci prvok zoznamu
        p_aktualny = p_aktualny->dalsi;
    }
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia na usporiadanie spajaneho zoznamu podla poloziek datum a cas
*/
void funkcia_u(ZAZNAM **zaciatok){
    int vymenene, i = 0;    //premenna, ktora zistuje ci boli polozky vymenene a pocitadlo 
    ZAZNAM *ukaz1;      //ukazovatel na prvok zoznamu
    ZAZNAM *ptr = NULL;
    ZAZNAM *p_aktualny = *zaciatok; 
    int chyba = 0;
  
    //ak je zoznam prazdny, funkcia skonci
    if ((*zaciatok) == NULL) 
        return; 
  
    do{ 
        //inicializacia premennych
        vymenene = 0; 
        ukaz1 = (*zaciatok); 
  
        //while-cyklus na usporiadanie zoznamu
        while (ukaz1->dalsi != ptr) { 
            //ak je datum aktualneho zaznamu vacsi ako nasledujuceho, vymenia sa
            if (ukaz1->datum > ukaz1->dalsi->datum){ 
                vymen(ukaz1, ukaz1->dalsi); 
                vymenene = 1; 
            //ak sa datumy rovnaju, porovnavaju sa rovnakym sposobom casy
            }else if(ukaz1->datum == ukaz1->dalsi->datum){
                if(atoi(ukaz1->cas_merania) > atoi(ukaz1->dalsi->cas_merania)){
                    vymen(ukaz1, ukaz1->dalsi); 
                    vymenene = 1; 
                } 
            }
            //posunutie ukazovatela na nasledujuci prvok
            ukaz1 = ukaz1->dalsi;  
        } 
        ptr = ukaz1;
        i++;
    }while (vymenene); 

    //vykona sa len ak spajany zoznam nie je prazdny
    if(zaciatok != NULL){
        //while-cyklus, ktory skonci ked nasledujuci prvok je NULL
        while(p_aktualny->dalsi != NULL){
            //kontrola, ci je datum v nasledujucom prvku v zozname vacsi
            if(p_aktualny->datum > p_aktualny->dalsi->datum){
                chyba = 1;      //nastavenie chyby na 1
                break;
            //ak sa datumy rovnaju, porovnava sa cas
            }else if(p_aktualny->datum == p_aktualny->dalsi->datum){
                if(atoi(p_aktualny->cas_merania) > atoi(p_aktualny->dalsi->cas_merania)){
                    chyba = 1;  //nastavenie chyby na 1
                    break;
                }
            }
            //posun ukazovatela na nasledujuci prvok
            p_aktualny = p_aktualny->dalsi;
        }
    }
    //vypis na zaklade toho, ci nastala nejaka chyba v usporiadani
    if(chyba == 0){
        printf("Spajany zoznam bol usporiadany.\n");
    }else{
        printf("Chyba.\n");
    }
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia na pridanie zaznamu do spajaneho zoznamu zaznamov
*/
int funkcia_p(ZAZNAM **u_u_zaciatok, int *pocet_zaznamov){
    int pozicia;

    //nacitanie pozicie, na ktoru sa ma zaznam pridat
    scanf("%d", &pozicia);
    //ak je zadana pozicia vacsia ako pocet prvkov, nastavi sa na posledny prvok
    if(pozicia > *pocet_zaznamov+1){
        pozicia = *pocet_zaznamov+1;
    }

    //ak je pozicia zaporna alebo nulova, funkcia skonci
    if(pozicia<=0)
        return 0;

    //premenna na uchovanie aktualnej pozicie v zozname
    ZAZNAM *u_aktualny = *u_u_zaciatok;
    //for-cyklus, ktory posunie ukazovatel na prvok, po ktorom ma byt novy prvok pridany
    for(int i=1; i<pozicia-1; i++){
        u_aktualny = u_aktualny->dalsi;
    }
    //zvysenie poctu zaznamov o 1
    *pocet_zaznamov += 1;
    //alokovanie pamate pre novy zaznam
    ZAZNAM *vrchol = (ZAZNAM*)malloc(sizeof(ZAZNAM));
    //nacitanie jednotlivych poloziek pre vrchol zo vstupu
    scanf("%lld ", &(vrchol->id_osoby));
    fgets((vrchol->meno_osoby), 30, stdin);
    scanf("%s", (vrchol->mer_modul));
    scanf("%s", (vrchol->typ_mer_veliciny));
    scanf("%lf", &(vrchol->hodnota));
    scanf("%s", (vrchol->cas_merania));
    scanf("%ld", &(vrchol->datum));

    //ak je pocet zaznamov <= pozicia, pridany prvok bude posledny
    if(*pocet_zaznamov <= pozicia){
        vrchol->dalsi = NULL;
    }else{  //v opacnom pripade k nemu bude pripojeny zvysok zoznamu
        if(pozicia==1){
            vrchol->dalsi = u_aktualny;
        }else{
            vrchol->dalsi = u_aktualny->dalsi;
        }
    }
    
    //ak je pozicia 1, vrchol sa vlozi na zaciatok zoznamu
    if(pozicia == 1){
        (*u_u_zaciatok) = vrchol;
    }else{
        //posunutie vrcholu na nasledujuci prvok
        u_aktualny->dalsi = vrchol;
    }
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia, ktora umozni pouzivatelovi vyhladat a vypisat vsetky
polozky zaznamu podla nazvu mer_modulu, zadaneho zo vstupu
*/
int funkcia_h(ZAZNAM *zaciatok){
    char modul[4];
    int poradie = 0;

    //nacitanie mer_modulu zo vstupu
    scanf("%s", modul);

    //vytvorenie pomocneho ukazovatela na zaciatok zoznamu
    ZAZNAM *aktualny = zaciatok;
    //while-cyklus, ktory prejdem celym zoznamom
    while(aktualny != NULL){
        //ak sa zadany modul a modul v zazname rovnaju, vypise ho na vystup
        if(strcmp(aktualny->mer_modul, modul)==0){
            printf("%d:\n",poradie);
            printf("ID cislo mer. osoby: %lld\n", aktualny->id_osoby);
            printf("Meno osoby: %s", aktualny->meno_osoby);
            printf("Mer. modul: %s\n", aktualny->mer_modul);
            printf("Typ mer. veliciny: %s\n", aktualny->typ_mer_veliciny);
            printf("Hodnota: %lf\n", aktualny->hodnota);
            printf("Cas merania: %s\n", aktualny->cas_merania);
            printf("Datum: %ld\n", aktualny->datum);

            poradie++;
        }
        //posunutie ukazovatela na nasledujuci zaznam
        aktualny = aktualny->dalsi;
    }
    //ak sa nenasiel ani jeden taky zaznam, vypise sa nasledujuca sprava
    if(poradie==0){
        printf("Pre meraci modul %s nie su zaznamy.\n", modul);
    }
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia na prehodenie dvoch zaznamov v spajanom zozname
*/
void funkcia_r(ZAZNAM **zaciatok, int pocet_zaznamov){
    int c1, c2;
    
    //nacitanie pozicii dvoch zaznamov zo vstupu
    scanf("%d %d", &c1, &c2);
    //ak su zadane hodnoty mimo intervalov, funkcia skonci
    if(c1<0 || c2<0 || c1>pocet_zaznamov || c2>pocet_zaznamov)
        return;

    //vytvorenie premennych pre vrcholy na vymenenie a ukazovatel na aktualny zaznam
    ZAZNAM *vrchol1, *vrchol2;
    ZAZNAM *aktualny = *zaciatok;

    //najdenie prveho vrcholu, ktory chceme prehodit
    for(int i=1; i<c1; i++){
        aktualny = aktualny->dalsi;
    }
    vrchol1 = aktualny;
    //resetovanie ukazovatela opat na zaciatok zoznamu
    aktualny = *zaciatok;

    //najdenie druheho vrcholu, ktory chceme prehodit
    for(int i=1; i<c2; i++){
        aktualny = aktualny->dalsi;
    }
    vrchol2 = aktualny;

    //volanie funkcie na vymenu vrcholov zoznamu
    vymen(vrchol1, vrchol2);
}

// ---------------------------------------------------------------------------------------------------
/*
funkcia main, ktora sa spusta pri spusteni programu
*/
int main(){
    //vytvorenie premennych
    FILE *fr;
    char c;
    ZAZNAM *zaciatok = NULL;
    int pocet_zaznamov = 0;

    //while-cyklus, ktory sa ukonci pri zadani pismena k
    while(c != 'k'){
        scanf("%c", &c);

        //switch, ktory sleduje, ci bolo zadane jedno z pismen, na ktore je vytvorena funkcia
        switch(c){
            case 'n':
                funkcia_n(&fr, &zaciatok, &pocet_zaznamov);
                break;
            
            case 'v':
                funkcia_v(zaciatok);
                break;

            case 'z':
                funkcia_z(&zaciatok, &pocet_zaznamov);
                break;

            case 'u':
                funkcia_u(&zaciatok);
                break;

            case 'p':
                funkcia_p(&zaciatok, &pocet_zaznamov);
                break;

            case 'h':
                funkcia_h(zaciatok);
                break;

            case 'r':
                funkcia_r(&zaciatok, pocet_zaznamov);
                break;
        }
    }
    //po skonceni while-cyklu, ak bol zoznam vytvoreny, uvolni sa pamat pri kazdom zazname
    if(zaciatok != NULL){
        while(zaciatok != NULL){
            ZAZNAM *pom = zaciatok;
            zaciatok = zaciatok->dalsi;
            free(pom);
        }
    }

    return 0;
}