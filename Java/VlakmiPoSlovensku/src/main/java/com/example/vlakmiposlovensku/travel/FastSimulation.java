package com.example.vlakmiposlovensku.travel;

import com.example.vlakmiposlovensku.gui.TravelGUI;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Trieda <code>FastSimulation</code> reprezentuje triedu slúžiacu na rýchlu simuláciu trasy.
 * Táto trieda je implementovaná ako návrhový vzor Strategy.
 * Trieda implementuje rozhranie {@link Simulation}.
 * Rýchla simulácia znamená, že tu nie sú implementované potreby, na ktoré treba dávať pozor.
 *
 * @see Simulation
 */
public class FastSimulation implements Simulation{
    private final Travel travel;

    public FastSimulation(Travel travel){
        this.travel = travel;
    }

    /**
     * Metóda slúžiaca na simulovanie celej trasy.
     * V triede je využitá aj viacniťovosť, keďže po každom kilometri je potrebné na chvíľu zastať a tiež
     * aktualizovať ukazovateľ počtu kilometrov.
     * Metóda umožňuje aj vystúpiť na ktorejkoľvek stanici vrámci trasy, keďže v každej stanici na chvíľu zastaví.
     * @param km        počet kilometrov celej trasy
     * @param pane      okno, na ktoré metóda vypisuje údaje
     */
    public void simulateTravel(int km, TravelGUI pane) {
        final int[] travelled = {0};
        final int[] position = {1};
        AtomicBoolean shouldContinue = new AtomicBoolean(true);
        AtomicInteger remainingKm = new AtomicInteger(km);

        pane.getWarning().setVisible(false);
        pane.getEnergy().setVisible(false);
        pane.getEat().setVisible(false);
        pane.getHunger().setVisible(false);
        pane.getSleep().setVisible(false);
        pane.getToilet().setVisible(false);
        pane.getUseToilet().setVisible(false);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                while (remainingKm.get() >= 0 && shouldContinue.get()) {
                    int finalRemainingKm = remainingKm.get();
                    Platform.runLater(() -> {
                        pane.getKilometres().setText(finalRemainingKm + " km");
                        pane.getSector().setText(travel.getPath().getPath().get(position[0]-1).getName() + " -> " + travel.getPath().getPath().get(position[0]).getName());

                        if(travelled[0] == travel.getPath().getDistances()[position[0]]){
                            pane.getCurrentStation().setText("Stanica: " + travel.getPath().getPath().get(position[0]).getName());
                            pane.getGetOff().setVisible(true);
                        }else{
                            pane.getCurrentStation().setText("");
                            pane.getGetOff().setVisible(false);
                        }
                    });

                    if(remainingKm.get() <= 0){
                        Platform.runLater(() -> setEnded(position, pane));
                        break;
                    }

                    if(travelled[0] == travel.getPath().getDistances()[position[0]]){
                        Thread.sleep(500);
                    }
                    Thread.sleep(100);
                    remainingKm.getAndDecrement();

                    travelled[0]++;
                    if(travelled[0] > travel.getPath().getDistances()[position[0]]){
                        position[0]++;
                    }
                }

                return null;
            }
        };

        pane.getGetOff().setOnAction(e -> {
            setEnded(position, pane);
            shouldContinue.set(false);
        });

        new Thread(task).start();
    }

    /**
     * Metóda ukončujúca simuláciu, pričom na okno vypíše názov cieľovej destinácie.
     * @param position      pozícia v poli staníc
     * @param pane          okno, na ktoré metóda vypisuje údaje
     */
    public void setEnded(int[] position, TravelGUI pane){
        pane.getKilometres().setText(travel.getPath().getPath().get(position[0]).getName());
        pane.getCurrentStation().setText("");
        pane.getSector().setText("");
        pane.getGetOff().setVisible(false);
    }
}
