import matplotlib.pyplot as plt
import numpy as np

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

#funkcia, ktorá prečíta všetky body zo zadaného súboru
def read_file(filename):
    measurement = []
    with open(filename, "r") as f:
        for row in f:
            color_list = []
            if row == "":
                continue
            points = row.split(";")
            for p in points[:-1]:
                color_list.append(eval(p))
            measurement.append(color_list)
    return measurement

def plot_four_measurements(measurement1, measurement2, measurement3, measurement4):
    #vytvorenie 2x2 mriežky pre výpisy
    fig, axs = plt.subplots(2, 2, figsize=(10, 8))

    # Plot each measurement in its respective subplot
    plot_measurement(axs[0, 0], measurement1, 'k = 1')
    plot_measurement(axs[0, 1], measurement2, 'k = 3')
    plot_measurement(axs[1, 0], measurement3, 'k = 7')
    plot_measurement(axs[1, 1], measurement4, 'k = 15')

    #nastavenie vlastností pre jednotlivé časti mriežky
    for ax_row in axs:
        for ax in ax_row:
            ax.set_xlim(-5000, 5000)
            ax.set_ylim(-5000, 5000)
            ax.set_xticks(np.arange(-5000, 6000, 2000))
            ax.set_yticks(np.arange(-5000, 6000, 2000))
            ax.set_xlabel("X")
            ax.set_ylabel("Y")

    fig.tight_layout()

    plt.suptitle('KNN')
    plt.savefig("knn")      #uloženie ako obrázok

    #zobrazenie
    plt.show()

#funkcia, ktorá vypíše body jedného merania do grafu
def plot_measurement(ax, measurement, title):
    for point_array in measurement:
        if len(point_array) == 0:
            continue
        x, y, color = zip(*[(point.x, point.y, point.color) for point in point_array])
        ax.scatter(x, y, marker='o', color=color[0])
    ax.set_title(title)

measurements = []
for i in range(4):
    m = read_file(f"measurement{i+1}.txt")
    measurements.append(m)

plot_four_measurements(measurements[0], measurements[1], measurements[2], measurements[3])