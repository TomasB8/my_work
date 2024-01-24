from random import randint, random, randrange, choice, sample
from copy import deepcopy
from operator import itemgetter

"""
Trieda reprezentujúca záhradu, ktorú má mních svojimi prechodmi pohrabať.
"""
class Garden:
    #konštruktor triedy Garden
    def __init__(self, rows, cols, rocks, leafs):
        self.rows = rows                #počet riadkov 
        self.cols = cols                #počet stĺpcov
        self.rocks = rocks              #pole obsahujúce súradnice kameňov v záhrade
        self.leafs = leafs              #dvojrozmerné pole obsahujúce súradnice listov, prvé pole - žlté, druhé pole - pomarančové, tretie - červené listy
        #ak zadáme pole listov, spočítajú sa počty jednotlivých farieb listov
        if len(leafs) != 0:
            self.yellow = len(self.leafs[0])
            self.orange = len(self.leafs[1])
            self.red = len(self.leafs[2])
        #inak sú to nuly
        else:
            self.yellow = 0
            self.orange = 0
            self.red = 0
        self.garden = self.generate_garden()        #vygenerovanie záhrady spolu s kameňmi a listami
    
    #metóda, ktorá generuje záhradu s kameňmi a listami
    def generate_garden(self):
        new_garden = []
        #for-cyklus, ktorý vytvára dvojrozmerné pole, v ktorom je záhrada reprezentovaná
        for row in range(self.rows):
            new_row = [0] * self.cols
            new_garden.append(new_row)

        #naplnenie záhrady kameňmi
        for rock in self.rocks:
            new_garden[rock[0]][rock[1]] = "X"

        #naplnenie záhrady listami
        for i in range(len(self.leafs)):
            for leaf in self.leafs[i]:
                #prvé pole sú žlté listy
                if i==0:
                    new_garden[leaf[0]][leaf[1]] = "Z"
                #druhé pole sú pomarančové listy
                elif i == 1:
                    new_garden[leaf[0]][leaf[1]] = "P"
                #tretie pole sú červené listy
                elif i==2:
                    new_garden[leaf[0]][leaf[1]] = "C"

        return new_garden
    
    #metóda vypisujúca záhradu do terminálu
    def print_garden(self):
        print()         #odriatkovanie
        for row in range(self.rows):
            for col in range(self.cols):
                if isinstance(self.garden[row][col], int):
                    print(f"{self.garden[row][col]:4}", end="")
                else:
                    print(f"{self.garden[row][col]:>4}", end="")
            print()     #odriatkovanie

"""
Trieda reprezentujúca jednotlivé gény, teda miesta, z ktorých bude mních vychádzať.
Pohyby:
    1 - hore
    2 - vpravo
    3 - dole
    4 - vľavo
"""
class Gene:
    #konštruktor triedy Gene
    def __init__(self, number, rows, cols):
        self.number = number
        self.rows = rows
        self.cols = cols
        self.pos, self.mov = self.get_position()

    #funkcia, ktorá zistí, z ktorého políčka má mních vychádzať, a tiež smer, v ktorom sa má pohybovať
    def get_position(self):
        n = self.number #miesto na okraji záhrady
        x = 0           #x-ová súradnica
        y = 0           #y-ová súradnica
        mov = 0         #smer pohybu
        #ak je n medzi 0 a počtom stĺpcov
        if n >= 0 and n < self.cols:
            x = 0
            y = n
            mov = 3         #bude sa pohybovať smerom nadol
        #ak je n medzi počtom stĺpcov a súčtom počtu stĺpcov a počtu riadkov
        elif n >= self.cols and n < self.cols+self.rows:
            x = n - self.cols
            y = self.cols-1
            mov = 4         #bude sa pohybovať smerom vľavo
        #ak je n medzi súčtom počtu stĺpcov a počtu riadkov a súčtom dvojnásobku počtu stĺpcov a počtu riadkov
        elif n >= self.cols+self.rows and n < 2*self.cols+self.rows:
            x = self.rows - 1
            y = 2*self.cols + self.rows - n - 1
            mov = 1         #bude sa pohybovať smerom nahor
        #ak je n medzi súčtom dvojnásobku počtu stĺpcov a počtu riadkov a dvojnásobku súčtu počtu riadkov a stĺpcov
        elif n >= 2*self.cols+self.rows and n < 2*(self.cols+self.rows):
            x = 2*(self.rows+self.cols) - n - 1
            y = 0
            mov = 2

        return ((x, y), mov)

"""
Trieda reprezentujúca jednotlivé chromozómy, teda jedincov z populácie.
"""
class Chromosome:
    #konštruktor triedy Chromosome
    def __init__(self, garden, first=False):
        self.garden = garden
        self.fitness = 0
        #ak je to chromozóm prvej generácie, tak generujeme gény náhodne
        if first:
            self.genes = self.generate_genes()                  #gény, ako sa bude mních pohybovať
            self.orient_genes = self.gen_orient_genes()         #gény, kam sa mních otočí pri možnosti voľby
        else:
            self.genes = []
            self.orient_genes = [] 

    def __str__(self):
        result = "("
        for gene in self.genes:
            result += f"{str(gene.number)} "
        return result[:-1]+")" 

    #funkcia generujúca náhodné gény pre chromozómy prvej generácie
    def generate_genes(self):
        number_of_genes = self.garden.rows + self.garden.cols       #počet týchto génov sa rovná polovici obvodu záhrady
        genes = []
        used_numbers = []       #pole na kontrolu, či sme už také číslo použili
        i = 0
        while i < number_of_genes:
            o = 2*self.garden.rows + 2*self.garden.cols     #obvod záhrady
            pos = randint(0, o-1)           #náhodná pozícia na obvode záhrady
            #ak sa takýto gén ešte nevyskytuje, vytvorí sa a uloží sa medzi gény daného chromozómu
            if pos not in used_numbers:
                genes.append(Gene(pos, self.garden.rows, self.garden.cols))
                used_numbers.append(pos)
                i += 1
        
        return genes
    
    #funkcia generujúca gény na rozhodovanie, kam sa má mních otočiť v prípade voľby
    def gen_orient_genes(self):
        genes = []
        for i in range(len(self.garden.rocks)):
            genes.append(choice(["r", "l"]))        #náhodný výber z možností: vpravo / vľavo
        
        return genes
    
    #metóda zabezpečujúca pohyb
    def make_move(self, x, y, n):
        if self.garden.garden[x][y] == "Z":
            self.garden.yellow -= 1
        elif self.garden.garden[x][y] == "P":
            self.garden.orange -= 1
        elif self.garden.garden[x][y] == "C":
            self.garden.red -= 1
        self.garden.garden[x][y] = n

    #funkcia kontrolujúca, či sa mních môže pohnúť na nasledujúce políčko
    def check_move(self, x, y):
        if self.garden.garden[x][y] == 0:       #nič sa tam nenachádza
            return True
        elif self.check_leaf(x, y):             #ak sa nachádza, skontrolujeme, či je to list a či ho môžeme zobrať
            return True
        return False
    
    #funkcia zabezpečujúca zbieranie listov a kontrolu, či sa na dané políčko môže mních presunúť
    def check_leaf(self, x, y):
        try:
            #ak sa tam nachádza žltý list, môže ho zobrať
            if self.garden.garden[x][y] == "Z":
                #self.garden.yellow -= 1     #zníži počet žltých listov o 1
                return True
            #ak sa tam nachádza pomarančový list a všetky žlté listy boli vyzbierané, môže ho zobrať
            elif self.garden.yellow == 0 and self.garden.garden[x][y] == "P":
                #self.garden.orange -= 1     #zníži počet pomarančových listov o 1
                return True
            #ak sa tam nachádza červený list a všetky žlté a pomarančové listy boli vyzbierané, môže ho zobrať
            elif self.garden.yellow == 0 and self.garden.orange == 0 and self.garden.garden[x][y] == "C":
                #self.garden.red -= 1        #zníži počet červených listov o 1
                return True
            else:
                return False
        except IndexError:
            return False
    
    #funkcia, ktorá zisťuje kam sa má mních otočiť v prípade kolízie
    def find_dir(self, x, y, mov, o):
        #ak sa pohybuje smerom hore
        if mov == 1:
            #ak sa môže otočiť do obidvoch smerov
            if ((y < self.garden.cols-1 and (self.garden.garden[x][y+1] == 0 or self.check_leaf(x, y+1))) and (y >= 0 and (self.garden.garden[x][y-1] == 0 or self.check_leaf(x, y-1)))):
                #pozrie sa do génov a zistí kam sa má otočiť a príslušný smer pohybu vráti
                if(self.orient_genes[o%(len(self.orient_genes))] == "r"):
                    return 2
                elif(self.orient_genes[o%(len(self.orient_genes))] == "l"):
                    return 4
            #môže sa otočiť iba vpravo
            elif y < self.garden.cols-1 and (self.garden.garden[x][y+1] == 0 or self.check_leaf(x, y+1)):
                return 2
            #môže sa otočiť iba vľavo
            elif y >= 0 and (self.garden.garden[x][y-1] == 0 or self.check_leaf(x, y-1)):
                return 4
            #nemôže sa otočiť nikam
            else:
                return -1
            
        #ak sa pohybuje smerom vpravo
        elif mov == 2:
            #ak sa môže otočiť do obidvoch smerov
            if (x >= 0 and (self.garden.garden[x-1][y] == 0 or self.check_leaf(x-1,y))) and (x < self.garden.rows-1 and (self.garden.garden[x+1][y] == 0 or self.check_leaf(x+1,y))):
                #pozrie sa do génov a zistí kam sa má otočiť a príslušný smer pohybu vráti
                if(self.orient_genes[o%(len(self.orient_genes))] == "r"):
                    return 3
                elif(self.orient_genes[o%(len(self.orient_genes))] == "l"):
                    return 1
            #môže sa otočiť iba vľavo
            elif x >= 0 and (self.garden.garden[x-1][y] == 0 or self.check_leaf(x-1,y)):
                return 1
            #môže sa otočiť iba vpravo
            elif x < self.garden.rows-1 and (self.garden.garden[x+1][y] == 0 or self.check_leaf(x+1,y)):
                return 3
            #nemôže sa otočiť nikam
            else:
                return -1
            
        #ak sa pohybuje smerom dole
        elif mov == 3:
            #ak sa môže otočiť do obidvoch smerov
            if (y < self.garden.cols-1 and (self.garden.garden[x][y+1] == 0 or self.check_leaf(x, y+1))) and (y >= 0 and (self.garden.garden[x][y-1] == 0 or self.check_leaf(x, y-1))):
                #pozrie sa do génov a zistí kam sa má otočiť a príslušný smer pohybu vráti
                if(self.orient_genes[o%(len(self.orient_genes))] == "r"):
                    return 4
                elif(self.orient_genes[o%(len(self.orient_genes))] == "l"):
                    return 2
            #môže sa otočiť iba vľavo
            elif y < self.garden.cols-1 and (self.garden.garden[x][y+1] == 0 or self.check_leaf(x, y+1)):
                return 2
            #môže sa otočiť iba vpravo
            elif y >= 0 and (self.garden.garden[x][y-1] == 0 or self.check_leaf(x, y-1)):
                return 4
            #nemôže sa otočiť nikam
            else:
                return -1
            
        #ak sa pohybuje smerom vľavo
        elif mov == 4:
            #ak sa môže otočiť do obidvoch smerov
            if (x < self.garden.rows-1 and (self.garden.garden[x+1][y] == 0 or self.check_leaf(x+1, y))) and (x >= 0 and (self.garden.garden[x-1][y] == 0 or self.check_leaf(x-1, y))):
                #pozrie sa do génov a zistí kam sa má otočiť a príslušný smer pohybu vráti
                if(self.genes[o%(len(self.orient_genes))] == "r"):
                    return 1
                elif(self.genes[o%(len(self.orient_genes))] == "l"):
                    return 3
            #môže sa otočiť iba vľavo
            elif x < self.garden.rows-1 and (self.garden.garden[x+1][y] == 0 or self.check_leaf(x+1, y)):
                return 3
            #môže sa otočiť iba vpravo
            elif x >= 0 and (self.garden.garden[x-1][y] == 0 or self.check_leaf(x-1, y)):
                return 1
            #nemôže sa otočiť nikam
            else:
                return -1
    
    #funkcia zabezpečujúca prejdenie záhrady
    def make_path(self):
        number = 1
        new_garden = deepcopy(self.garden)      #skopíruje záhradu
        o = 0
        #for-cyklus prechádza postupne všetky gény vytvára pre ne cestu
        for gene in self.genes:
            x, y = gene.pos     #získanie začiatočných súradníc génu
            mov = gene.mov      #pohyb génu
            end = False
            made_move = False
            #while-cyklus, ktorý beží kým je mních na políčkach v záhrade
            while 0<=x<self.garden.rows and 0<=y<self.garden.cols:
                #pohyb smerom nahor
                if mov == 1:
                    #skontroluje, či sa na dnané políčko môže posunúť, ak áno posunie sa
                    if self.check_move(x, y):
                        self.make_move(x, y, number)
                        made_move = True
                        x -= 1
                    #ak nie, zistí kam sa môže mních otočiť
                    else:
                        if x == self.garden.rows-1:
                            break
                        mov = self.find_dir(x+1, y, 1, o)
                        o += 1
                        x += 1
                        if mov==2:
                            y+=1
                        else:
                            y-=1
                #pohyb smerom vpravo
                elif mov == 2:
                    #skontroluje, či sa na dnané políčko môže posunúť, ak áno posunie sa
                    if self.check_move(x, y):
                        self.make_move(x, y, number)
                        made_move = True
                        y += 1
                    #ak nie, zistí kam sa môže mních otočiť
                    else:
                        if y == 0:
                            break
                        mov = self.find_dir(x, y-1, 2, o)
                        o += 1
                        y -= 1
                        if mov==1:
                            x-=1
                        else:
                            x+=1
                #pohyb smerom nadol
                elif mov == 3:
                    #skontroluje, či sa na dnané políčko môže posunúť, ak áno posunie sa
                    if self.check_move(x, y):
                        self.make_move(x, y, number)
                        made_move = True
                        x += 1
                    #ak nie, zistí kam sa môže mních otočiť
                    else:
                        if x == 0:
                            break
                        mov = self.find_dir(x-1, y, 3, o)
                        o += 1
                        x -= 1
                        if mov==2:
                            y+=1
                        else:
                            y-=1
                #pohyb smerom vľavo
                elif mov == 4:
                    #skontroluje, či sa na dnané políčko môže posunúť, ak áno posunie sa
                    if self.check_move(x, y):
                        self.make_move(x, y, number)
                        made_move = True
                        y -= 1
                    #ak nie, zistí kam sa môže mních otočiť
                    else:
                        if y == self.garden.cols-1:
                            break
                        mov = self.find_dir(x, y+1, 4, o)
                        o += 1
                        y += 1
                        if mov==1:
                            x-=1
                        else:
                            x+=1
                else:
                    end = True
                    break
            #ak daný gén spravil ťah, zvýši sa hodnota o 1
            if made_move:
                number += 1
            if end:
                break

            new_garden = deepcopy(self.garden)      #skopírovanie záhrady

        return new_garden
    
    #funkcia, ktorá zisťuje fitness hodnotu daného chromozómu, teda počet pohrabaných políčok
    def fitness_func(self):
        empty = 0
        for i in range(self.garden.rows):
            for j in range(self.garden.cols):
                if self.garden.garden[i][j] == 0 or self.garden.garden[i][j] in ["Z", "P", "C"]:
                    empty += 1
        return self.garden.rows * self.garden.cols - len(self.garden.rocks) - empty

#funkcia zabezpečujúca mutácie génov
def mutation(chromosome):
    #for-cyklus prechádza všetky gény
    for i in range(len(chromosome.genes)):
        number = random()       #vygeneruje náhodné číslo
        #s pravdepodobnosťou 10% bude daný gén mutovať
        if number < 0.1:
            o = 2*chromosome.garden.rows + 2*chromosome.garden.cols
            pos = randint(0, o-1)       #vygeneruje sa nová náhodná pozícia
            chromosome.genes[i] = Gene(pos, chromosome.garden.rows, chromosome.garden.cols)
    
    #for-cyklus prechádza všetky gény otáčania
    for i in range(len(chromosome.orient_genes)):
        number = random()
        #s pravdepodobnosťou 0,5% bude daný gén mutovať
        if number < 0.005:
            if chromosome.orient_genes[i] == "r":
                chromosome.orient_genes[i] = "l"
            else:
                chromosome.orient_genes[i] = "r"

#funkcia zabezpečujúca kríženie dvoch chromozómov
def crossing(chromosome1, chromosome2, garden):
    #vygenerovanie náhodných čísel
    mutateNum = random()
    crossProb = random()
    newChrom = Chromosome(deepcopy(garden))     #nový chromozóm
    pivotPoint = randrange(len(chromosome1.genes))              #pivotný prvok pre gény
    pivotOrient = randrange(len(chromosome1.orient_genes))      #pivotný prvok pre orientačné gény
    #ak je číslo crossProb medzi 0,4 a 0,8 nastane toto kríženie
    if 0.4 < crossProb < 0.8:
        #vyberajú sa náhodné gény postupne od obidvoch rodičov
        newChrom.genes = []
        for i in range(len(chromosome1.genes)):
            newChrom.genes.append(choice((chromosome1.genes[i], chromosome2.genes[i])))
        for i in range(len(chromosome1.orient_genes)):
            newChrom.orient_genes.append(choice((chromosome1.orient_genes[i], chromosome2.orient_genes[i])))
    elif crossProb <= 0.4:
        #jedna časť sa berie od rodiča1 a druhá časť od rodiča2
        newChrom.genes = chromosome1.genes[:pivotPoint] + chromosome2.genes[pivotPoint:]
        newChrom.orient_genes = chromosome1.orient_genes[:pivotOrient] + chromosome2.orient_genes[pivotOrient:]
    else:
        #žiadne kríženie, berú sa všetky gény od jedného rodiča
        newChrom.genes = choice((chromosome1.genes, chromosome2.genes))
        newChrom.orient_genes = choice((chromosome1.orient_genes, chromosome2.orient_genes))

    #pravdepodobnosť, že pre daný chromozóm môže nastať mutácia je 50%
    if mutateNum < 0.5:
        mutation(newChrom)

    return newChrom

#funkcia zabezpečujúca tvorbu novej generácie
def generate_gen(chromosomes, garden):
    new_generation = []
    new_generation.append(chromosomes[0][1])        #najlepší prvok z predchádzahúcej generácie sa presunie aj do novej generácie
    #chromosomes[0][1].garden.print_garden()
    
    #generácia sa bude napĺňať, kým sa počet jej chromozómov nebude rovnať predchádzajúcej generácii
    while len(new_generation) < len(chromosomes):
        selectionNum = random()         #náhodné číslo
        parent1 = None
        parent2 = None
        #ruletový výber
        if selectionNum < 0.33:
            total = sum(item[0] for item in chromosomes)
            randNum1 = randint(0, total)
            randNum2 = randint(0, total)
            for_total = 0
            x = 0
            for chrom in chromosomes:
                x += 1
                for_total += chrom[0]
                if(for_total >= randNum1) and parent1 == None:
                    parent1 = chrom[1]
                if(for_total >= randNum2) and parent2 == None:
                    parent2 = chrom[1]
            new_generation.append(crossing(parent1, parent2, deepcopy(garden)))
        
        #turnajový výber rodičov
        elif 0.33 < selectionNum < 0.66:
            parent1 = None
            parent2 = None
            sublist = sample(chromosomes, 10)
            sublist.sort(key=lambda a: a[0], reverse=True)
            parent1 = sublist[0][1]
            parent2 = sublist[0][1]

            new_generation.append(crossing(parent1, parent2, deepcopy(garden)))
        
        #výber podľa poradia
        else:
            subtract = 1
            total = ((1 + len(chromosomes))*len(chromosomes))//2        #vypocet suctu n clenov aritmetickej postupnosti
            randNum1 = randint(0, total)
            randNum2 = randint(0, total)
            for i in range(len(chromosomes), 0, -1):
                randNum1 -= subtract
                randNum2 -= subtract
                subtract += 1
                if(randNum1 <= 0) and parent1 == None:
                    parent1 = chromosomes[i-1][1]
                if(randNum2 <= 0) and parent2 == None:
                    parent2 = chromosomes[i-1][1]
            new_generation.append(crossing(parent1, parent2, deepcopy(garden)))

    return new_generation