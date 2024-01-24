package com.example.vlakmiposlovensku.travel;

import com.example.vlakmiposlovensku.gui.TravelGUI;

/**
 * Rozhranie <code>Simulation</code> slúžiace na využitie návrhového vzoru Strategy.
 * Je možné si vybrať z dvoch stratégii: {@link SlowSimulation} alebo {@link FastSimulation}.
 *
 * @see SlowSimulation
 * @see FastSimulation
 */
public interface Simulation {
    void simulateTravel(int km, TravelGUI pane);
}
