package com.example.vlakmiposlovensku.travel;

import com.example.vlakmiposlovensku.gui.TravelGUI;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Trieda <code>SlowSimulation</code> reprezentuje triedu slúžiacu na pomalú simuláciu trasy.
 * Táto trieda je implementovaná ako návrhový vzor Strategy.
 * Trieda implementuje rozhranie {@link Simulation}.
 * Pomalá simulácia znamená, že tu sú implementované potreby, na ktoré treba dávať pozor.
 *
 * @see Simulation
 */
public class SlowSimulation implements Simulation{
    private final Travel travel;
    private final ArrayList<NeedsChecker> needs = new ArrayList<>();

    public SlowSimulation(Travel travel){
        this.travel = travel;
    }

    public void addNeed(NeedsChecker need){
        needs.add(need);
    }

    public void showNeeds(){
        for(NeedsChecker need : needs){
            need.show();
        }
    }

    /**
     * Metóda slúžiaca na simulovanie celej trasy.
     * V triede je využitá aj viacniťovosť, keďže po každom kilometri je potrebné na chvíľu zastať a tiež
     * aktualizovať ukazovateľ počtu kilometrov a ukazovateľ potrieb.
     * Na potreby je využitý návrhový vzor Observer.
     * Metóda umožňuje aj vystúpiť na ktorejkoľvek stanici vrámci trasy, keďže v každej stanici na chvíľu zastaví.
     * Počas trasy je potrebné dávať pozor na potreby: {@link EnergyPointer}, {@link HungerPointer} a {@link ToiletPointer}
     * @param km        počet kilometrov celej trasy
     * @param pane      okno, na ktoré metóda vypisuje údaje
     */
    public void simulateTravel(int km, TravelGUI pane) {
        final int finalKm = km;
        final int[] travelled = {0};
        final int[] position = {1};
        AtomicBoolean shouldContinue = new AtomicBoolean(true);
        AtomicInteger remainingKm = new AtomicInteger(finalKm);

        addNeed(new EnergyPointer(pane, travel.getTraveller()));
        addNeed(new HungerPointer(pane, travel.getTraveller()));
        addNeed(new ToiletPointer(pane, travel.getTraveller()));

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                while (remainingKm.get() >= 0 && shouldContinue.get()) {
                    int finalRemainingKm = remainingKm.get();
                    Platform.runLater(() -> {
                        pane.getKilometres().setText(finalRemainingKm + " km");
                        pane.getSector().setText(travel.getPath().getPath().get(position[0]-1).getName() + " -> " + travel.getPath().getPath().get(position[0]).getName());
                        showNeeds();
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
                        Thread.sleep(3000);
                    }
                    Thread.sleep(500);
                    remainingKm.getAndDecrement();
                    travel.getTraveller().decreaseEnergy();
                    travel.getTraveller().increaseHunger();
                    travel.getTraveller().increaseToilet();
                    travelled[0]++;
                    if(travelled[0] > travel.getPath().getDistances()[position[0]]){
                        position[0]++;
                    }

                    if(travel.getTraveller().getEnergy()<=20 || travel.getTraveller().getHunger()>=80 || travel.getTraveller().getToilet()>=80){
                        pane.getWarning().setVisible(true);
                    }
                }

                return null;
            }
        };

        int finalRemainingKm1 = remainingKm.get();
        pane.getSleep().setOnAction(e -> {
            int sleptKm = travel.getTraveller().sleep(finalRemainingKm1);
            if(sleptKm >= remainingKm.get()){
                remainingKm.set(0);
                travelled[0] = finalKm;

                setEnded(new int[]{travel.getPath().getPath().size() - 1}, pane);
                shouldContinue.set(false);
            }else{
                remainingKm.addAndGet(-sleptKm);
                travelled[0] += sleptKm;
            }
            while(travelled[0] > travel.getPath().getDistances()[position[0]]){
                position[0]++;
            }
            pane.getWarning().setVisible(false);
        });

        pane.getEat().setOnAction(e -> {
            travel.getTraveller().eat();
            pane.getWarning().setVisible(false);
        });

        pane.getUseToilet().setOnAction(e -> {
            travel.getTraveller().useToilet();
            pane.getWarning().setVisible(false);
        });

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
        pane.getSleep().setVisible(false);
        pane.getEnergy().setText("");
        pane.getEat().setVisible(false);
        pane.getHunger().setText("");
        pane.getUseToilet().setVisible(false);
        pane.getToilet().setText("");
        pane.getWarning().setVisible(false);
    }
}
