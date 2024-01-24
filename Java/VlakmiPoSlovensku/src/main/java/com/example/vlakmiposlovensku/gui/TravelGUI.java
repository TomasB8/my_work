package com.example.vlakmiposlovensku.gui;

import com.example.vlakmiposlovensku.travel.Path;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Trieda <code>TravelGUI</code> reprezentuje okno, ktoré sa zobrazí po zakúpení lístka {@link BuyTicketPane}.
 * Trieda je odvodená od triedy {@link FlowPane} a teda sa dá použiť rovnakým spôsobom.
 *
 * @see BuyTicketPane
 * @see FlowPane
 */
public class TravelGUI extends FlowPane {

    private final Label warning = new Label("Skontrolujte potreby!!!");
    private final Label kilometres = new Label();
    private final Label sector = new Label();
    private final Label currentStation = new Label();
    private final Label energy = new Label();
    private final Label hunger = new Label();
    private final Label toilet = new Label();
    private final Button getOff = new Button("Vystúpiť");
    private final Button sleep = new Button("Spať");
    private final Button eat = new Button("Jesť");
    private final Button useToilet = new Button("Použiť WC");

    public TravelGUI(Stage mainWindow, Path path){
        this.setAlignment(Pos.CENTER);
        this.setHgap(10000);
        this.setVgap(5);

        warning.setPadding(new Insets(0, 0, 50, 0));
        warning.setFont(new Font(25));
        warning.setStyle("-fx-text-fill: red;");
        warning.setVisible(false);
        this.getChildren().add(warning);

        kilometres.setFont(new Font(40));

        this.getChildren().add(currentStation);
        this.getChildren().add(getOff);

        this.getChildren().add(sector);
        this.getChildren().add(kilometres);
        kilometres.setText(String.valueOf(path.getKilometres()));
        getOff.setVisible(false);

        this.getChildren().add(energy);
        this.getChildren().add(sleep);
        this.getChildren().add(hunger);
        this.getChildren().add(eat);
        this.getChildren().add(toilet);
        this.getChildren().add(useToilet);

        mainWindow.setScene(new Scene(this, 800, 600));
        mainWindow.show();
    }

    /**
     * Metóda vracajúca Label pre varovanie.
     * @return      Label pre varovanie
     */
    public Label getWarning() {
        return warning;
    }

    /**
     * Metóda vracajúca Label pre kilometre.
     * @return      Label pre kilometre
     */
    public Label getKilometres() {
        return kilometres;
    }

    /**
     * Metóda vracajúca Label pre sektor.
     * @return      Label pre sektor
     */
    public Label getSector() {
        return sector;
    }

    /**
     * Metóda vracajúca Label pre aktuálnu stanicu.
     * @return      Label pre aktuálnu stanicu
     */
    public Label getCurrentStation() {
        return currentStation;
    }

    /**
     * Metóda vracajúca Label pre energiu.
     * @return      Label pre energiu
     */
    public Label getEnergy() {
        return energy;
    }

    /**
     * Metóda vracajúca Label pre hlad.
     * @return      Label pre hlad
     */
    public Label getHunger() {
        return hunger;
    }

    /**
     * Metóda vracajúca Label pre potrebu WC.
     * @return      Label pre potrebu WC
     */
    public Label getToilet() {
        return toilet;
    }

    /**
     * Metóda vracajúca Button pre vystúpenie.
     * @return      Button pre vystúpenie
     */
    public Button getGetOff() {
        return getOff;
    }

    /**
     * Metóda vracajúca Button pre spánok.
     * @return      Button pre spánok
     */
    public Button getSleep() {
        return sleep;
    }

    /**
     * Metóda vracajúca Button pre jedenie.
     * @return      Button pre jedenie
     */
    public Button getEat() {
        return eat;
    }

    /**
     * Metóda vracajúca Button pre použitie WC.
     * @return      Button pre použitie WC
     */
    public Button getUseToilet() {
        return useToilet;
    }
}
