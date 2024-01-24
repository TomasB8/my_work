package com.example.vlakmiposlovensku.travel;

import com.example.vlakmiposlovensku.gui.TravelGUI;
import com.example.vlakmiposlovensku.handlers.FunctionsInterface;
import com.example.vlakmiposlovensku.travellers.Traveller;

/**
 * Trieda <code>ToiletPointer</code> slúži na vypísanie hladu v triede {@link SlowSimulation}.
 * Táto trieda využíva návrhový vzor Observer.
 * Trieda je odvodená od rozhrania {@link NeedsChecker}.
 *
 * @see SlowSimulation
 * @see NeedsChecker
 */
public class ToiletPointer implements NeedsChecker{
    private final TravelGUI pane;
    private final Traveller traveller;

    public ToiletPointer(TravelGUI pane, Traveller traveller){
        this.pane = pane;
        this.traveller = traveller;
    }

    /**
     * Metóda vypisujúca potrebu na WC.
     * V metóde sa nachádza lambda výraz pre zaokrúhlenie desatinného čísla na dve desatinné miesta.
     */
    public void show(){
        FunctionsInterface lambdaRound = (value) -> Math.round(value * 100.0) / 100.0;
        pane.getToilet().setText("Potreba WC: " + lambdaRound.round(traveller.getToilet()) + "/100");
    }
}
