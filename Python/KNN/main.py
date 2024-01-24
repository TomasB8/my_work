import math
from random import randint, choice, random
import copy
import time

"""
Treida reprezentujúca jednotlivé štvorce v mape, ktoré slúžia na optimalizáciu riešenia
"""
class Square:
    #konštruktor triedy
    def __init__(self, min_x, max_x, min_y, max_y):
        self.min_x = min_x
        self.max_x = max_x
        self.min_y = min_y
        self.max_y = max_y
        self.points = []

    #pridanie bodu do daného štvorca
    def add_point(self, point):
        self.points.append(point)

"""
Trieda reprezentujúca jednotlivé body.
"""
class Point:
    #konštruktor triedy
    def __init__(self, x, y, color):
        self.x = x
        self.y = y
        self.color = color

    #definícia, kedy sa dva Pointy rovnajú
    def __eq__(self, Point):
        if self.x == Point.x and self.y == Point.y and self.color == Point.color:
            return True
        return False
    
    #stringová reprezentácia triedy Point
    def __str__(self):
        return f"Point({self.x}, {self.y}, \"{self.color}\")"


COUNT = 10000       #počet bodov na generovanie z jednotlivých intervalov

#vytvorenie počiatočných bodov
RP1 = Point(-4500, -4400, "red")
RP2 = Point(-4100, -3000, "red")
RP3 = Point(-1800, -2400, "red")
RP4 = Point(-2500, -3400, "red")
RP5 = Point(-2000, -1400, "red")

GP1 = Point(4500, -4400, "green")
GP2 = Point(4100, -3000, "green")
GP3 = Point(1800, -2400, "green")
GP4 = Point(2500, -3400, "green")
GP5 = Point(2000, -1400, "green")

BP1 = Point(-4500, 4400, "blue")
BP2 = Point(-4100, 3000, "blue")
BP3 = Point(-1800, 2400, "blue")
BP4 = Point(-2500, 3400, "blue")
BP5 = Point(-2000, 1400, "blue")

PP1 = Point(4500, 4400, "purple")
PP2 = Point(4100, 3000, "purple")
PP3 = Point(1800, 2400, "purple")
PP4 = Point(2500, 3400, "purple")
PP5 = Point(2000, 1400, "purple")

grid = []       #premenná na reprezentáciu mapy

#funkcia, ktorá generuje n bodov v zadanom rozmedzí bodov, ktorým priradí zadanú farbu
def generate_points(n, min_x, max_x, min_y, max_y, color):
    points = []
    #while-cyklus, ktorý beží až kým nie je počet bodov n
    while len(points) != n:
        #s pravdepodobnosťou 99% sú body generované zo zadaného intervalu
        if random() < 0.99:
            x_coordinate = randint(min_x, max_x)
            y_coordinate = randint(min_y, max_y)
        #s pravdepodobnosťou 1% sú body generované z celého priestoru
        else:
            x_coordinate = randint(-5000, 5000)
            y_coordinate = randint(-5000, 5000)
        new_point = Point(x_coordinate, y_coordinate, color)        #vytvorenie bodu
        #ak sa v poli nenachádza bod na tých istých súradniciach, vloží sa tam
        if new_point not in points:
            points.append(new_point)
    return points

#funkcia, ktora zmieša polia všetkých farieb tak, aby nikdy nešli po sebe 2 body rovnakej farby
def mix_arrays(arr1, arr2, arr3, arr4):
    mixed_array = []
    last_selected = None

    for i in range(len(arr1)):
        values = [arr1[i], arr2[i], arr3[i], arr4[i]]       #uloženie i-tych prvkov zo všetkých polí
        #vyprázdňovanie poľa values
        while len(values) != 0:
            chosen = choice(values)     #náhodný výber prvku z poľa values
            #kontrola, či táto farba nebola posledná
            while chosen.color == last_selected:
                chosen = choice(values)
            values.pop(values.index(chosen))    #odstránenie vybraného prvku z poľa values
            mixed_array.append(chosen)          #vloženie do zmiešaného poľa
            last_selected = chosen.color

    return mixed_array

#funkcia, ktorá vráti riadok a stĺpec štvorca, v ktorom sa nachádza zadaný bod
def find_square(point):
    x = point.x
    y = point.y
    if y < 0:
        y = 4999 + abs(y)
    else:
        y = 4999 - y
    if x < 0:
        x = 5000 - abs(x)
    else:
        x = 5000 + x
    return (min(y//500,19), min(x//500,19))

#funkcia, ktorá vráti všetky susedné štvorce zadaného štvorca
def find_neighnouring_squares(square):
    row, col = square
    result = [square]
    if row-1 >= 0 and col-1 >= 0:
        result.append((row-1, col-1))
    if row-1 >= 0 and 0 <= col <= 19:
        result.append((row-1, col))
    if row-1 >= 0 and col+1 <= 19:
        result.append((row-1, col+1))
    if col-1 >= 0 and 0 <= row <= 19:
        result.append((row, col-1))
    if col+1 <= 19 and 0 <= row <= 19:
        result.append((row, col+1))
    if row+1 <= 19 and col-1 >= 0:
        result.append((row+1, col-1))
    if row+1 <= 19 and 0 <= col <= 19:
        result.append((row+1, col))
    if row+1 <= 19 and col+1 <= 19:
        result.append((row+1, col+1))
    return result

#inicializácia gridu
def initialize_grid():
    global grid
    #rozdeľenie gridu na 400 malých štvorcov veľkosti 500x500
    grid = [
        [Square(x, y, x + 500, y + 500) for x in range(-5000, 5000, 500)]
        for y in range(-5000, 5000, 500)
    ]
    #naplnenie gridu počiatočnými bodmi
    for point in [RP1,RP2,RP3,RP4,RP5,GP1,GP2,GP3,GP4,GP5,BP1,BP2,BP3,BP4,BP5,PP1,PP2,PP3,PP4,PP5]:
        sq = find_square(point)
        grid[sq[0]][sq[1]].add_point(point)

red_points = []
green_points = []
blue_points = []
purple_points = []
neigh = []
#vygenerovanie COUNT bodov pre každú z farieb
red_points.extend(generate_points(COUNT, -5000, 499, -5000, 499, "red"))
green_points.extend(generate_points(COUNT, -500, 5000, -5000, 499, "green"))
blue_points.extend(generate_points(COUNT, -5000, 499, -500, 5000, "blue"))
purple_points.extend(generate_points(COUNT, -500, 5000, -500, 5000, "purple"))

#zmiešanie polí
all_points = mix_arrays(red_points, green_points, blue_points, purple_points)

#funkcia, ktorá zaklasifikuje bod na zadaných súradniciach x a y, a vráti frabu
def classify(x, y, k):
    distances = []
    #počítanie počtu susedov z jednotlivých farieb
    red_count = 0
    green_count = 0
    blue_count = 0
    purple_count = 0

    #nájdenie všetkých bodov zo všetkých susedných štvorcov, s ktorými sa bude počítať vzdialenosť
    all_p = []
    for row, col in neigh:
        all_p.extend(grid[row][col].points)

    #ak je počet bodov v susedných štvorcoch menší ako k, vzdialenosti sa počítajú so všetkými bodmi v priestore
    if len(all_p) < k:
        all_p = []
        for i in range(19):
            for j in range(19):
                all_p.extend(grid[i][j].points)

    #počítanie vzdialenosti pre všetky body v poli all_p
    for point in all_p:
        distance = math.sqrt((x-point.x)**2 + (y-point.y)**2)
        distances.append((distance, point.color))

    #zoradenie vzdialeností od najmenšej po najväčšiu
    distances = sorted(distances, key=lambda x: x[0])

    #spočítanie farieb pre k najbližších susedov
    for i in range(k):
        color = distances[i][1]
        if color == "red":
            red_count += 1
        elif color == "green":
            green_count += 1
        elif color == "blue":
            blue_count += 1
        else:
            purple_count += 1

    #nájdenie najväčšieho počtu a vrátenie prislúchajúcej farby
    max_colors = sorted((("red", red_count), ("green", green_count), ("blue", blue_count), ("purple", purple_count)), key=lambda x: x[1], reverse=True)
    #print(max_colors)
    max_count = max_colors[0][1]
    if max_colors[1][1] == max_count:
        for _, nearest_color in distances:
            for color, count in max_colors:
                if color == nearest_color:
                    if count == max_count:
                        return nearest_color

    return max_colors[0][0]

#funkcia, ktorá prejde všetky body s rôznymi k = [1, 3, 7, 15]
def evaluate_classificator():
    global neigh, GRID

    order = 0

    for k in [1, 3, 7, 15]:
        #počítanie chýb
        red_wrong = 0
        green_wrong = 0
        blue_wrong = 0
        purple_wrong = 0

        #polia so všetkými bodmi pre jednotlivé farby
        red_final = []
        green_final = []
        blue_final = []
        purple_final = []

        initialize_grid()       #inicializácia gridu
        this_all_points = copy.deepcopy(all_points)         #skopírovanie všetkých bodov do nového poľa, aby sme nemenili pôvodné body

        #for-cyklus, ktorý prechádza všetky body 
        for point in this_all_points:
            sq = find_square(point)                 #nájdenie štvorca, do ktorého bod patrí
            neigh = find_neighnouring_squares(sq)   #nájdenie susedných štvorcov
            classified_color = classify(point.x, point.y, k)    #klasifikácia bodu
            #spočítanie chybne umiestnených bodov
            if classified_color != point.color:
                if point.color == "red":
                    red_wrong += 1
                elif point.color == "green":
                    green_wrong += 1
                elif point.color == "blue":
                    blue_wrong += 1
                else:
                    purple_wrong += 1
            
            point.color = classified_color      #zmena farby bodu
            
            #uloženie bodu do prislúchajúceho poľa podľa novej farby
            if classified_color == "red":
                red_final.append(point)
            elif classified_color == "green":
                green_final.append(point)
            elif classified_color == "blue":
                blue_final.append(point)
            else:
                purple_final.append(point)

            grid[sq[0]][sq[1]].add_point(point)     #pridanie bodu do štvorca v gride

        #ukladanie všetkých polí do textových súborov
        order += 1
        with open(f"measurement{order}.txt", "w") as f:
            for ls in [red_final, green_final, blue_final, purple_final]:
                for point in ls:
                    print(point, end=";", file=f)
                print("\n", end="", file=f)

        #vypísanie štatistiky
        print("#######################################################")
        print(f"k = {k}")
        print("-------------------------------------------------------")
        print("Red:", COUNT, ",", red_wrong)
        print("Red-correct:", round((1-red_wrong/COUNT)*100, 2), "%")
        print("---")
        print("Green:", COUNT, ",", green_wrong)
        print("Green-correct:", round((1-green_wrong/COUNT)*100, 2), "%")
        print("---")
        print("Blue:", COUNT, ",", blue_wrong)
        print("Blue-correct:", round((1-blue_wrong/COUNT)*100, 2), "%")
        print("---")
        print("Purple:", COUNT, ",", purple_wrong)
        print("Purple-correct:", round((1-purple_wrong/COUNT)*100, 2), "%")
        print("#######################################################")
    
#meranie času
start = time.time()
evaluate_classificator()
end = time.time()
print("Time:", end-start, "s")