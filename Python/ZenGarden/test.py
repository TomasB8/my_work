from main import Garden, Chromosome, generate_gen
from copy import deepcopy
import time

#funkcia, ktorá rieši danú záhradu
def test_garden(garden, whole_garden):
    generation = []
    best_fitness = 0
    genNum = 0

    #jednu populáciu tvorí 60 chromozómov
    for i in range(60):
        ch = Chromosome(deepcopy(garden), True)
        generation.append(ch)
    try:
        #kým sa nedosiahne riešenie, alebo neprejde 1000 generácii
        while best_fitness != whole_garden and genNum != 1000:
            fitnesses = []
            for ch in generation:
                ch.garden = ch.make_path()
                ch.fitness = ch.fitness_func()
                fitnesses.append((ch.fitness, ch))

            fitnesses.sort(key=lambda a: a[0], reverse=True)        #zoradenia podľa fitness
            best_fitness = fitnesses[0][0]

            #dosiahnuté riešenie - ukončenie cyklu
            if(best_fitness == whole_garden):
                break

            generation = generate_gen(fitnesses, deepcopy(garden))

            #po každých 100 generáciách sa vypisuje stav
            if genNum%100 == 0:
                print(genNum, " -> ", best_fitness, "/", whole_garden)
            genNum += 1

        print("Generation:", genNum)
        fitnesses[0][1].garden.print_garden()
        print("Genes:", fitnesses[0][1])
    except KeyboardInterrupt:
        print("Generation:", genNum)
        fitnesses[0][1].garden.print_garden()
        print("Genes:", fitnesses[0][1])

print("GARDEN 1")
print("--------------------------------------------------")
x = 10
y = 12
rocks = [(2,1), (4,2), (3,4), (1,5), (6,8), (6,9)]
leafs = []
garden = Garden(x, y, rocks, leafs)
whole_garden = x * y - len(garden.rocks)
start_time = time.time()
test_garden(garden, whole_garden)
end_time = time.time()
elapsed_time = end_time - start_time
print("Elapsed time: ", elapsed_time, "s") 

print("\n################################################################################################\n")

print("GARDEN 2")
print("--------------------------------------------------")
x = 5
y = 6
rocks = [(2,3), (2,4), (3,1), (0,0), (1,2)]
leafs = []
garden = Garden(x, y, rocks, leafs)
whole_garden = x * y - len(garden.rocks)
start_time = time.time()
test_garden(garden, whole_garden)
end_time = time.time()
elapsed_time = end_time - start_time
print("Elapsed time: ", elapsed_time, "s")

print("\n################################################################################################\n")

print("GARDEN 3")
print("--------------------------------------------------")
x = 10
y = 12
rocks = [(0,5), (0,6), (9,5), (9,6)]
leafs = [[(4, 5), (4, 7), (5, 4), (5, 6)],
              [(2, 3), (2, 8), (3, 2), (3, 9), (5, 3), (6, 9), (7, 8)],
              [(0, 1), (0, 10), (1, 0), (1, 11), (6, 0), (7,1), (8, 11), (9, 10)]]
garden = Garden(x, y, rocks, leafs)
whole_garden = x * y - len(garden.rocks)
start_time = time.time()
test_garden(garden, whole_garden)
end_time = time.time()
elapsed_time = end_time - start_time
print("Elapsed time: ", elapsed_time, "s")

print("\n################################################################################################\n")

print("GARDEN 4")
print("--------------------------------------------------")
x = 8
y = 12
rocks = [(4,5), (4,6), (4,7), (5,5), (5,7), (6,5), (6,6), (6,7)]
leafs = []
garden = Garden(x, y, rocks, leafs)
whole_garden = x * y - len(garden.rocks)
start_time = time.time()
test_garden(garden, whole_garden)
end_time = time.time()
elapsed_time = end_time - start_time
print("Elapsed time: ", elapsed_time, "s")