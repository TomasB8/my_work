ROWS = 6
COLS = 6

"""
Trieda reprezentujúca jednotlivé vozidlá.
Atribúty:
    color = farba vozidla
    size = dĺžka vozidla (2 - auto; 3 - kamión)
    row = pozícia podľa riadkov
    col = pozícia podľa stĺpcov
    orientation = orientácia vozidla (v - vertikálne; h - horizontálne)
"""
class Vehicle:
    def __init__(self, color, size, row, col, orientation):
        self.color = color
        self.size = size
        self.row = row
        self.col = col
        self.orientation = orientation

    #reprezentácia vozidla 
    def __str__(self):
        return "(" + self.color + " " + str(self.size) + " " + str(self.row) + " " + str(self.col) + " " + self.orientation + ")"
    
    #pohyb vpravo
    def move_right(self, fields):
        self.col += fields

    #pohyb vľavo
    def move_left(self, fields):
        self.col -= fields

    #pohyb hore
    def move_up(self, fields):
        self.row -= fields

    #pohyb dole
    def move_down(self, fields):
        self.row += fields

"""
Trieda reprezentujúca stav hracieho poľa.
Atribúty:
    vehicles = zoznam vozidiel
    predecessor = predchádzajúci stav
"""
class State:
    def __init__(self, vehicles, predecessor, movement):
        self.vehicles = vehicles
        self.predecessor = predecessor
        self.movement = movement

    #reprezentácia stavu pri výpise
    def __str__(self):
        result = ""
        for v in self.vehicles:
            result += str(v)
        return result
    """
    metóda, ktorá zisťuje, či sa červené auto dostalo von z križovatky a teda, či hra skončila
    """
    def check_end(self):
        red_veh = self.vehicles[0]
        if red_veh.col+red_veh.size-1 == COLS:
            return True
    """
    Metóda kontrolujúca, či je vozidlo stále v hracom poli a či neprekročilo hranice.
    V prípade kolízie vráti hodnotu: False,
    inak True.
    """
    def check_border(self, vehicle, dir, fields):
        col = vehicle.col
        row = vehicle.row
        size = vehicle.size

        #kontroluje pravú hranicu poľa
        if(dir == "right"):
            if (col+fields+size-1) > COLS:
                return False
        
        #kontroluje ľavú hranicu poľa
        elif(dir == "left"):
            if(col-fields) < 1:
                return False
            
        #kontroluje hornú hranicu poľa
        elif(dir == "up"):
            if(row-fields) < 1:
                return False
            
        #kontroluje dolnú hranicu poľa
        elif(dir == "down"):
            if(row+fields+size-1) > ROWS:
                return False
            
        return True
    
    """
    Metóda kontrolujúca kolízie s ostatnými vozidlami.
    V prípade kolízie vráti False,
    inak True.
    """
    def check_collision(self, i, dir, fields):
        v_to_move = self.vehicles[i]                                      #získanie vozidla, ktoré sa má hýbať
        tmp_vehicles = self.vehicles                                      #skopírovanie vozidiel do novej premennej
        tmp_vehicles = tmp_vehicles[:i] + tmp_vehicles[i+1:]              #odstránenie vozidla, ktoré sa bude hýbať z dočasných vozidiel

        #pohyb vpravo
        if(dir == "right"):
            for veh in tmp_vehicles:
                new_col = v_to_move.col + fields + v_to_move.size-1         #výpočet novej súradnice stĺpca
                if veh.orientation == "h" and v_to_move.row == veh.row and new_col == veh.col:      #ak sa druhé vozidlo pohybuje horizontálne, tak ku kolízii dôjde, keď sa budú rovnať ich súradnice aj riadkov aj stĺpcov
                    return False
                
                elif new_col == veh.col and veh.orientation == "v":         #keď sa druhé vozidlo pohybuje vertikálne a ich súradnice stĺpcov sa rovnajú
                    if(v_to_move.row >= veh.row) and (v_to_move.row <= veh.row+veh.size-1):     #hýbajúce sa vozidlo sa nachádza v rozsahu, ktorý pokrýva druhé vozidlo = kolízia
                        return False
                    
        #pohyb vľavo
        elif(dir == "left"):
            for veh in tmp_vehicles:
                new_col = v_to_move.col - fields                            #výpočet novej súradnice stĺpca
                if veh.orientation == "h" and v_to_move.row == veh.row and ((new_col >= veh.col) and (new_col <= veh.col+veh.size-1)):      #ak sa druhé vozidlo pohybuje horizontálne, tak ku kolízii dôjde, keď sa budú rovnať ich súradnice riadkov a pohybujúce sa vozidlo bude nachádzať v rozsahu stĺpcov, ktoré sú pokryté druhým vozidlom
                    return False
                
                elif veh.orientation == "v" and new_col == veh.col:         #keď sa druhé vozidlo pohybuje vertikálne a ich súradnice stĺpcov sa rovnajú
                    if(v_to_move.row >= veh.row) and (v_to_move.row <= veh.row+veh.size-1):     #hýbajúce sa vozidlo sa nachádza v rozsahu, ktorý pokrýva druhé vozidlo = kolízia
                        return False
                    
        #pohyb hore
        elif(dir == "up"):
            for veh in tmp_vehicles:
                new_row = v_to_move.row - fields                            #výpočet novej súradnice stĺpca
                if veh.orientation == "v" and v_to_move.col == veh.col and ((new_row >= veh.row) and (new_row <= veh.row+veh.size-1)):      #ak sa druhé vozidlo pohybuje vertikálne, tak ku kolízii dôjde, keď sa budú rovnať ich súradnice stĺpcov a pohybujúce sa vozidlo bude nachádzať v rozsahu riadkov, ktoré sú pokryté druhým vozidlom
                    return False
                
                elif veh.orientation == "h" and new_row == veh.row:         #keď sa druhé vozidlo pohybuje horizontálne a ich súradnice stĺpcov sa rovnajú
                    if(v_to_move.col >= veh.col) and (v_to_move.col <= veh.col+veh.size-1):     #hýbajúce sa vozidlo sa nachádza v rozsahu, ktorý pokrýva druhé vozidlo = kolízia
                        return False
                    
        #pohyb dole
        elif(dir == "down"):
            for veh in tmp_vehicles:                
                new_row = v_to_move.row + fields + v_to_move.size-1         #výpočet novej súradnice stĺpca
                if veh.orientation == "v" and v_to_move.col == veh.col and new_row == veh.row:          #ak sa druhé vozidlo pohybuje vertikálne, tak ku kolízii dôjde, keď sa budú rovnať ich súradnice aj riadkov aj stĺpcov
                    return False
                
                elif veh.orientation == "h" and new_row == veh.row:         #keď sa druhé vozidlo pohybuje horizontálne a ich súradnice stĺpcov sa rovnajú
                    if(v_to_move.col >= veh.col) and (v_to_move.col <= veh.col+veh.size-1):     #hýbajúce sa vozidlo sa nachádza v rozsahu, ktorý pokrýva druhé vozidlo = kolízia
                        return False
                    
        return True
    
    """
    Metóda zapezpečujúca pohyb vozidla v danom smere o zadaný počet políčok
    """
    def move(self, vehicle, dir, fields):
        v_to_move = self.vehicles[vehicle]
        if(dir == "right") and (v_to_move.orientation == "h"):
            v_to_move.move_right(fields)

        elif(dir == "left") and (v_to_move.orientation == "h"):
            v_to_move.move_left(fields)

        elif(dir == "up") and (v_to_move.orientation == "v"):
            v_to_move.move_up(fields)

        elif(dir == "down") and (v_to_move.orientation == "v"):
            v_to_move.move_down(fields)

    """
    Metóda, ktorá pre dané vozidlo nájde rozsah polí, kam sa môže pohybovať.
    Pre všetky čísla od 1 po veľkosť poľa, zistí, či sa tam môže vozidlo pohnúť, a teda či nepríde ku kolízii s iným vozidlom alebo hranicou.
    Metóda vráti rozsah zistených hodnôt v poli.
    """
    def getRange(self, i, dir):
        r = 0
        if dir == "right":
            for x in range(1, COLS+1):
                if self.check_border(self.vehicles[i], "right", x) and self.check_collision(i, "right", x):
                    r = x
                else:
                    break;
        elif dir == "left":
            for x in range(1, COLS+1):
                if self.check_border(self.vehicles[i], "left", x) and self.check_collision(i, "left", x):
                    r = x
                else:
                    break;
        elif dir == "up":
            for x in range(1, ROWS+1):
                if self.check_border(self.vehicles[i], "up", x) and self.check_collision(i, "up", x):
                    r = x
                else:
                    break;
        elif dir == "down":
            for x in range(1, ROWS+1):
                if self.check_border(self.vehicles[i], "down", x) and self.check_collision(i, "down", x):
                    r = x
                else:
                    break;
        
        return list(range(1,r+1))

"""
Funkcia, ktorá skopíruje vozidlá do nového stavu
"""
def copy_vehicles(old):
    new = list()
    for v in old:
        new.append(Vehicle(v.color, v.size, v.row, v.col, v.orientation))
    return new

"""
Funkcia, ktorá implementuje algoritmus hľadania do šírky.
"""
def BFS(state):
    queue = []                              #premenná reprezentujúca rad
    queue.append(state)                     #vloženie prvého stavu
    checked_states = {}                     #slovník, ktorý ukladá už objavené stavy
    checked_states[str(state)] = state      #vloženie stavu medzi už objavené stavy
    finished = False
    final_state = None
    try:
        #while-cyklus, ktorý beží, až kým nenájde riešenie
        while not finished:
            state = queue.pop(0)            #výber prvého stavu z radu
            #for-cyklus, ktorý prechádza každé vozidlo a pre každý pohyb, ktorý môže vykonať vytvorí nový stav
            for i in range(len(state.vehicles)):
                vehicle = state.vehicles[i]         #výber vozidla
                if vehicle.orientation == "h":
                    ran_r = state.getRange(i, "right")          #stavy pre pohyb doprava
                    for x in ran_r:
                        #vytvorenie nového stavu
                        veh = copy_vehicles(state.vehicles)
                        new_board = State(veh, state, f"VPRAVO({vehicle.color}, {x})")
                        new_board.move(i, "right", x)
                        if new_board.check_end():       #kontrola, či stav nie je konečný, ak je, tak skončí
                            final_state = new_board
                            finished = True
                        #kontrola, či sa takýto stav ešte nenachádza v už objavených stavoch, ak nie, pridá ho do radu a do objavených stavov
                        if not str(new_board) in checked_states:
                            queue.append(new_board)
                            checked_states[str(new_board)] = new_board

                    ran_l = state.getRange(i, "left")           #stavy pre pohyb doľava
                    for x in ran_l:
                        #vytvorenie nového stavu
                        veh = copy_vehicles(state.vehicles)
                        new_board = State(veh, state, f"VLAVO({vehicle.color}, {x})")
                        new_board.move(i, "left", x)
                        if new_board.check_end():       #kontrola, či stav nie je konečný, ak je, tak skončí
                            final_state = new_board
                            finished = True
                        #kontrola, či sa takýto stav ešte nenachádza v už objavených stavoch, ak nie, pridá ho do radu a do objavených stavov
                        if not str(new_board) in checked_states:
                            queue.append(new_board)
                            checked_states[str(new_board)] = new_board

                elif vehicle.orientation == "v":
                    ran_u = state.getRange(i, "up")             #stavy pre pohyb nahor
                    for x in ran_u:
                        #vytvorenie nového stavu
                        veh = copy_vehicles(state.vehicles)
                        new_board = State(veh, state, f"HORE({vehicle.color}, {x})")
                        new_board.move(i, "up", x)
                        if new_board.check_end():       #kontrola, či stav nie je konečný, ak je, tak skončí
                            final_state = new_board
                            finished = True
                        #kontrola, či sa takýto stav ešte nenachádza v už objavených stavoch, ak nie, pridá ho do radu a do objavených stavov
                        if not str(new_board) in checked_states:
                            queue.append(new_board)
                            checked_states[str(new_board)] = new_board

                    ran_d = state.getRange(i, "down")           #stavy pre pohyb nahor
                    for x in ran_d:
                        #vytvorenie nového stavu
                        veh = copy_vehicles(state.vehicles)
                        new_board = State(veh, state, f"DOLE({vehicle.color}, {x})")
                        new_board.move(i, "down", x)
                        if new_board.check_end():       #kontrola, či stav nie je konečný, ak je, tak skončí
                            final_state = new_board
                            finished = True
                        #kontrola, či sa takýto stav ešte nenachádza v už objavených stavoch, ak nie, pridá ho do radu a do objavených stavov
                        if not str(new_board) in checked_states:
                            queue.append(new_board)
                            checked_states[str(new_board)] = new_board
    except IndexError:
        print("Úloha nemá riešenie.")

    return final_state

"""
Funkcia, ktorá implementuje algoritmus hľadania do hĺbky.
"""
def DFS(state):
    stack = []                              #premenná reprezentujúca zásobník
    stack.append(state)                     #vloženie prvého stavu
    checked_states = {}                     #slovník, ktorý ukladá už objavené stavy
    checked_states[str(state)] = state      #vloženie stavu medzi už objavené stavy
    finished = False
    final_state = None
    try:
        #while-cyklus, ktorý beží, až kým nenájde riešenie
        while not finished:
            state = stack.pop(-1)               #výber posledného stavu zo zásobníka
            #for-cyklus, ktorý prechádza každé vozidlo a pre každý pohyb, ktorý môže vykonať vytvorí nový stav
            for i in range(len(state.vehicles)-1, -1, -1):
                vehicle = state.vehicles[i]     #výber vozidla
                if vehicle.orientation == "h":
                    ran_r = state.getRange(i, "right")                  #stavy pre pohyb doprava
                    for x in ran_r:
                        #vytvorenie nového stavu
                        veh = copy_vehicles(state.vehicles)
                        new_board = State(veh, state, f"VPRAVO({vehicle.color}, {x})")
                        new_board.move(i, "right", x)
                        if new_board.check_end():                       #kontrola, či stav nie je konečný, ak je, tak skončí
                            final_state = new_board
                            finished = True
                        #kontrola, či sa takýto stav ešte nenachádza v už objavených stavoch, ak nie, pridá ho do radu a do objavených stavov
                        if not str(new_board) in checked_states:
                            stack.append(new_board)
                            checked_states[str(new_board)] = new_board

                    ran_l = state.getRange(i, "left")                   #stavy pre pohyb doľava
                    for x in ran_l:
                        #vytvorenie nového stavu
                        veh = copy_vehicles(state.vehicles)
                        new_board = State(veh, state, f"VLAVO({vehicle.color}, {x})")
                        new_board.move(i, "left", x)
                        if new_board.check_end():                       #kontrola, či stav nie je konečný, ak je, tak skončí
                            final_state = new_board
                            finished = True
                        #kontrola, či sa takýto stav ešte nenachádza v už objavených stavoch, ak nie, pridá ho do radu a do objavených stavov
                        if not str(new_board) in checked_states:
                            stack.append(new_board)
                            checked_states[str(new_board)] = new_board

                elif vehicle.orientation == "v":
                    ran_u = state.getRange(i, "up")                     #stavy pre pohyb nahor
                    for x in ran_u:
                        #vytvorenie nového stavu
                        veh = copy_vehicles(state.vehicles)
                        new_board = State(veh, state, f"HORE({vehicle.color}, {x})")
                        new_board.move(i, "up", x)
                        if new_board.check_end():                       #kontrola, či stav nie je konečný, ak je, tak skončí
                            final_state = new_board
                            finished = True
                        #kontrola, či sa takýto stav ešte nenachádza v už objavených stavoch, ak nie, pridá ho do radu a do objavených stavov
                        if not str(new_board) in checked_states:
                            stack.append(new_board)
                            checked_states[str(new_board)] = new_board

                    ran_d = state.getRange(i, "down")                   #stavy pre pohyb nadol
                    for x in ran_d:
                        #vytvorenie nového stavu
                        veh = copy_vehicles(state.vehicles)
                        new_board = State(veh, state, f"DOLE({vehicle.color}, {x})")
                        new_board.move(i, "down", x)
                        if new_board.check_end():                       #kontrola, či stav nie je konečný, ak je, tak skončí
                            final_state = new_board
                            finished = True
                        #kontrola, či sa takýto stav ešte nenachádza v už objavených stavoch, ak nie, pridá ho do radu a do objavených stavov
                        if not str(new_board) in checked_states:
                            stack.append(new_board)
                            checked_states[str(new_board)] = new_board
    except IndexError:
        print("Úloha nemá riešenie.")

    return final_state