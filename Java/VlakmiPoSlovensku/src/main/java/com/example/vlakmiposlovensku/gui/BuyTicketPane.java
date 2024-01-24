package com.example.vlakmiposlovensku.gui;

import com.example.vlakmiposlovensku.exceptions.InvalidCardException;
import com.example.vlakmiposlovensku.exceptions.InvalidIDException;
import com.example.vlakmiposlovensku.exceptions.InvalidTimeFormatException;
import com.example.vlakmiposlovensku.handlers.CardHandler;
import com.example.vlakmiposlovensku.travel.Path;
import com.example.vlakmiposlovensku.travel.Travel;
import com.example.vlakmiposlovensku.travellers.Infants;
import com.example.vlakmiposlovensku.travellers.Student;
import com.example.vlakmiposlovensku.travellers.Traveller;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Trieda <code>BuyTicketPane</code> reprezentuje okno, ktoré sa zobrazí po výbere trasy {@link SystemGUI}.
 * Trieda je odvodená od triedy {@link FlowPane} a teda sa dá použiť rovnakým spôsobom.
 *
 * @see SystemGUI
 * @see FlowPane
 */
public class BuyTicketPane extends FlowPane {
    private final Label heading = new Label("Zhrnutie");
    private final Label name = new Label();
    private final TextField id_number = new TextField();
    private final TextArea output = new TextArea();
    private final Button payment = new Button("Prejsť na platbu");

    private final TextOutput printer = new TextOutput(output);

    private final Travel travel;
    private final Path path;
    private CardHandler ch;

    // Pre platbu
    private final Label warning = new Label("Ľutujeme zadaný overovací kód je nesprávny!!!");
    private final Label cardNumber = new Label("Číslo karty:");
    private final TextField tfCardNumber = new TextField();
    private final Label expiringDate = new Label("Dátum platnosti:");
    private final TextField tfExpiringDate = new TextField();
    private final Label verifyNumber = new Label("Overovací kód:");
    private final TextField tfVerifyNumber = new TextField();
    private final Button pay = new Button("Zaplatiť a simulovať");
    private final Button lastUsedCard = new Button("Načítať poslednú kartu");
    private final CheckBox fast = new CheckBox("Rýchla simulácia");

    private final Label checkVerNum = new Label("Zadajte overovací kód: ");
    private final TextField tfCheckVerNum = new TextField();
    private final Button check = new Button("Overiť");

    public BuyTicketPane(Stage mainWindow, Traveller traveller, Travel travel, Path path) throws InvalidTimeFormatException {
        this.setAlignment(Pos.CENTER);
        this.setHgap(10000);
        this.setVgap(5);

        this.travel = travel;
        this.path = path;
        this.ch = new CardHandler();

        this.getChildren().add(heading);

        heading.setFont(new Font(24));
        heading.setStyle("-fx-font-weight: bold;");
        heading.setPadding(new Insets(20,0,20,0));

        name.setText("Meno: " + traveller.inform());
        name.setFont(new Font(18));
        name.setPadding(new Insets(0,0,20,0));;

        this.getChildren().add(name);
        this.getChildren().add(output);

        output.setPrefHeight(200);
        output.setPrefWidth(600);
        output.setFont(new Font("Consolas",13));
        output.setEditable(false);
        output.setWrapText(true);

        printer.printR(travel, path.getPath(), path.getTrains(), path.getDate(), path.getTimes());
        if(traveller instanceof Student && path.getPrice() == 0){
            this.getChildren().add(id_number);
            this.getChildren().add(fast);
            payment.setText("Simulovať");
        }else if(traveller instanceof Infants){
            this.getChildren().add(fast);
            payment.setText("Simulovať");
        }
        this.getChildren().add(payment);

        payment.setOnAction(e -> {
            if(traveller instanceof Student && path.getPrice() == 0){
                try{
                    travel.checkID(id_number.getText().strip());
                    TravelGUI travelGUI = new TravelGUI(mainWindow, path);
                    travel.changeSimulation(fast.isSelected());
                    travel.simulate(path.getKilometres(), travelGUI);
                }catch(InvalidIDException ignored){}
            }else if(traveller instanceof Infants){
                TravelGUI travelGUI = new TravelGUI(mainWindow, path);
                travel.changeSimulation(fast.isSelected());
                travel.simulate(path.getKilometres(), travelGUI);
            }else{
                createPaymentWindow(mainWindow);
            }

        });

        mainWindow.setScene(new Scene(this,800, 600));
        mainWindow.show();
    }

    /**
     * Metóda vracajúca TextField pre číslo karty.
     * @return          TextField na zápis čísla karty
     */
    public TextField getTfCardNumber() {
        return tfCardNumber;
    }

    /**
     * Metóda vracajúca TextField pre dátum platnosti karty.
     * @return          TextField pre dátum platnosti karty
     */
    public TextField getTfExpiringDate() {
        return tfExpiringDate;
    }

    /**
     * Metóda vracajúca TextField pre overovací kód karty.
     * @return          TextField pre overovací kód karty
     */
    public TextField getTfVerifyNumber() {
        return tfVerifyNumber;
    }

    /**
     * Metóda vracajúca Label pre varovanie.
     * @return      Label pre výpis varovania
     */
    public Label getWarning() {
        return warning;
    }

    /**
     * Metóda vytvárajúca okno pre zadanie platobnej karty a následnú platbu.
     * @param mainWindow        okno, kam sa vloží
     */
    private void createPaymentWindow(Stage mainWindow){
        FlowPane flowPane = new FlowPane();

        flowPane.setAlignment(Pos.CENTER);
        flowPane.setHgap(10000);
        flowPane.setVgap(5);

        tfCardNumber.setPromptText("XXXX XXXX XXXX XXXX");
        tfExpiringDate.setPromptText("01/24");
        tfVerifyNumber.setPromptText("XXX");

        flowPane.getChildren().add(warning);
        flowPane.getChildren().add(cardNumber);
        flowPane.getChildren().add(tfCardNumber);
        flowPane.getChildren().add(expiringDate);
        flowPane.getChildren().add(tfExpiringDate);
        flowPane.getChildren().add(verifyNumber);
        flowPane.getChildren().add(tfVerifyNumber);
        flowPane.getChildren().add(fast);
        flowPane.getChildren().add(pay);
        flowPane.getChildren().add(lastUsedCard);
        flowPane.getChildren().add(checkVerNum);
        flowPane.getChildren().add(tfCheckVerNum);
        flowPane.getChildren().add(check);

        warning.setPadding(new Insets(0, 0, 50, 0));
        warning.setFont(new Font(25));
        warning.setStyle("-fx-text-fill: red;");

        warning.setVisible(false);
        checkVerNum.setVisible(false);
        tfCheckVerNum.setVisible(false);
        check.setVisible(false);

        pay.setOnAction(e -> {
            String number = tfCardNumber.getText().strip();
            String expiry = tfExpiringDate.getText().strip();
            String verification = tfVerifyNumber.getText().strip();
            try{
                ch = new CardHandler(number, expiry, verification);
                ch.checkCard();
                ch.save();
                TravelGUI travelGUI = new TravelGUI(mainWindow, path);
                travel.changeSimulation(fast.isSelected());
                travel.simulate(path.getKilometres(), travelGUI);
            }catch(InvalidCardException ignored){
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        lastUsedCard.setOnAction(e -> {
            checkVerNum.setVisible(true);
            tfCheckVerNum.setVisible(true);
            check.setVisible(true);
        });

        check.setOnAction(e -> {
            try {
                if(ch.load(this, tfCheckVerNum.getText().strip())){
                    checkVerNum.setVisible(false);
                    tfCheckVerNum.setVisible(false);
                    check.setVisible(false);
                    warning.setVisible(false);
                }
            } catch (ClassNotFoundException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        mainWindow.setScene(new Scene(flowPane,800, 600));
        mainWindow.show();
    }
}
