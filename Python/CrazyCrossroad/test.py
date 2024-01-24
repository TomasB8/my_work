from main import State, Vehicle, BFS, DFS
import time

vehicles = []
print("***********************************************")
print("\t\tPRIKLAD 1")
print("***********************************************")
vehicles.append(Vehicle("cervene", 2, 3, 2, "h"))
vehicles.append(Vehicle("oranzove", 2, 1, 1, "h"))
vehicles.append(Vehicle("zlte", 3, 2, 1, "v"))
vehicles.append(Vehicle("fialove", 2, 5, 1, "v"))
vehicles.append(Vehicle("zelene", 3, 2, 4, "v"))
vehicles.append(Vehicle("svetlomodre", 3, 6, 3, "h"))
vehicles.append(Vehicle("sive", 2, 5, 5, "h"))
vehicles.append(Vehicle("tmavomodre", 3, 1, 6, "v"))
state = State(vehicles, None, "")
print("Počiatočný stav: " + str(state))
print("-----------------------------------------------")
print("\t\t  BFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = BFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
while final_state.predecessor is not None:
    movements.append(final_state.movement)
    final_state = final_state.predecessor
for m in movements[::-1]:
    print(m)
print(f"Čas: {end-start} ms")

print("-----------------------------------------------")
print("\t\t  DFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = DFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
while final_state.predecessor is not None:
    movements.append(final_state.movement)
    final_state = final_state.predecessor
for m in movements[::-1]:
    print(m)
print(f"Čas: {end-start} ms")


vehicles = []
print("***********************************************")
print("\t\tPRIKLAD 2")
print("***********************************************")
vehicles.append(Vehicle("cervene", 2, 5, 2, "h"))
vehicles.append(Vehicle("oranzove", 2, 3, 3, "h"))
vehicles.append(Vehicle("zlte", 3, 4, 4, "v"))
vehicles.append(Vehicle("fialove", 2, 1, 3, "v"))
vehicles.append(Vehicle("zelene", 3, 2, 1, "v"))
vehicles.append(Vehicle("svetlomodre", 3, 1, 4, "h"))
vehicles.append(Vehicle("sive", 2, 2, 5, "h"))
vehicles.append(Vehicle("tmavomodre", 3, 4, 6, "v"))
state = State(vehicles, None, "")
print("Počiatočný stav: " + str(state))
print("-----------------------------------------------")
print("\t\t  BFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = BFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
if final_state is not None:
    while final_state.predecessor is not None:
        movements.append(final_state.movement)
        final_state = final_state.predecessor
    for m in movements[::-1]:
        print(m)
print(f"Čas: {end-start} ms")

print("-----------------------------------------------")
print("\t\t  DFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = DFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
if final_state is not None:
    while final_state.predecessor is not None:
        movements.append(final_state.movement)
        final_state = final_state.predecessor
    for m in movements[::-1]:
        print(m)
print(f"Čas: {end-start} ms")


vehicles = []
print("***********************************************")
print("\t\tPRIKLAD 3")
print("***********************************************")
vehicles.append(Vehicle("cervene", 2, 6, 1, "h"))
vehicles.append(Vehicle("oranzove", 2, 3, 3, "h"))
vehicles.append(Vehicle("zlte", 3, 1, 5, "v"))
vehicles.append(Vehicle("fialove", 2, 5, 3, "v"))
vehicles.append(Vehicle("zelene", 3, 4, 4, "v"))
vehicles.append(Vehicle("svetlomodre", 3, 4, 1, "h"))
vehicles.append(Vehicle("sive", 2, 5, 5, "h"))
vehicles.append(Vehicle("tmavomodre", 3, 1, 2, "v"))
state = State(vehicles, None, "")
print("Počiatočný stav: " + str(state))
print("-----------------------------------------------")
print("\t\t  BFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = BFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
if final_state is not None:
    while final_state.predecessor is not None:
        movements.append(final_state.movement)
        final_state = final_state.predecessor
    for m in movements[::-1]:
        print(m)
print(f"Čas: {end-start} ms")

print("-----------------------------------------------")
print("\t\t  DFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = DFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
if final_state is not None:
    while final_state.predecessor is not None:
        movements.append(final_state.movement)
        final_state = final_state.predecessor
    for m in movements[::-1]:
        print(m)
print(f"Čas: {end-start} ms")


vehicles = []
print("***********************************************")
print("\t\tPRIKLAD 4")
print("***********************************************")
vehicles.append(Vehicle("cervene", 2, 6, 1, "h"))
vehicles.append(Vehicle("oranzove", 2, 4, 1, "h"))
vehicles.append(Vehicle("zlte", 3, 4, 6, "v"))
vehicles.append(Vehicle("fialove", 2, 1, 4, "v"))
vehicles.append(Vehicle("zelene", 3, 1, 1, "v"))
vehicles.append(Vehicle("svetlomodre", 3, 3, 4, "h"))
vehicles.append(Vehicle("sive", 2, 1, 2, "h"))
vehicles.append(Vehicle("tmavomodre", 3, 4, 3, "v"))
state = State(vehicles, None, "")
print("Počiatočný stav: " + str(state))
print("-----------------------------------------------")
print("\t\t  BFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = BFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
if final_state is not None:
    while final_state.predecessor is not None:
        movements.append(final_state.movement)
        final_state = final_state.predecessor
    for m in movements[::-1]:
        print(m)
print(f"Čas: {end-start} ms")

print("-----------------------------------------------")
print("\t\t  DFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = DFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
if final_state is not None:
    while final_state.predecessor is not None:
        movements.append(final_state.movement)
        final_state = final_state.predecessor
    for m in movements[::-1]:
        print(m)
print(f"Čas: {end-start} ms")


vehicles = []
print("***********************************************")
print("\t\tPRIKLAD 5")
print("***********************************************")
vehicles.append(Vehicle("cervene", 2, 2, 1, "h"))
vehicles.append(Vehicle("oranzove", 2, 4, 3, "h"))
vehicles.append(Vehicle("zlte", 3, 4, 1, "v"))
vehicles.append(Vehicle("fialove", 2, 1, 3, "v"))
vehicles.append(Vehicle("zelene", 3, 1, 5, "v"))
vehicles.append(Vehicle("svetlomodre", 3, 5, 3, "h"))
vehicles.append(Vehicle("sive", 2, 6, 2, "h"))
vehicles.append(Vehicle("tmavomodre", 3, 4, 6, "v"))
state = State(vehicles, None, "")
print("Počiatočný stav: " + str(state))
print("-----------------------------------------------")
print("\t\t  BFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = BFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
if final_state is not None:
    while final_state.predecessor is not None:
        movements.append(final_state.movement)
        final_state = final_state.predecessor
    for m in movements[::-1]:
        print(m)
print(f"Čas: {end-start} ms")

print("-----------------------------------------------")
print("\t\t  DFS:")
print("-----------------------------------------------")
start = round(time.time()*1000)
final_state = DFS(state)
end = round(time.time()*1000)
print("Konečný stav: " + str(final_state))
movements = []
if final_state is not None:
    while final_state.predecessor is not None:
        movements.append(final_state.movement)
        final_state = final_state.predecessor
    for m in movements[::-1]:
        print(m)
print(f"Čas: {end-start} ms")
