package com.example.vlakmiposlovensku.gui;

import com.example.vlakmiposlovensku.exceptions.InvalidDateFormatException;
import com.example.vlakmiposlovensku.exceptions.InvalidTimeFormatException;
import com.example.vlakmiposlovensku.exceptions.WrongStationException;
import com.example.vlakmiposlovensku.travel.Path;
import com.example.vlakmiposlovensku.travel.Travel;
import javafx.application.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Trieda <code>SystemGUI</code> reprezentuje aplikáciu, ktoré sa zobrazí po zapnutí programu.
 * Trieda je odvodená od triedy {@link Application}.
 * Trieda obsahuje prihlasovanie ale aj vyhľadanie vlaku.
 *
 * @see SystemGUI
 * @see FlowPane
 */
public class SystemGUI extends Application {

    private final ScrollPane scroll = new ScrollPane();
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final ComboBox<String> comboBox = new ComboBox<>();
    private final Button login = new Button("Prihlásiť sa");

    private final TextField fromLocation = new TextField();
    private final TextField toLocation = new TextField();
    private final DatePicker date = new DatePicker();
    private final TextField time = new TextField();
    private final Button search = new Button("Vyhľadaj");
    private final TextArea output = new TextArea();
    private final Button buyTicket = new Button("Kúpiť lístok");
    private final Button swap = new Button("↑↓");

    private final Label name = new Label("Meno:");
    private final Label surname = new Label("Priezvisko:");
    private final Label type = new Label("Typ cestujúceho:");
    private final Label from = new Label("Odkiaľ:");
    private final Label to = new Label("Kam:");
    private final Label datum = new Label("Dátum:");
    private final Label cas = new Label("Čas:");
    private final Label welcome = new Label();

    private final TextOutput printer = new TextOutput(output);

    private Path path;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Metóda na vytvorenie nového okna, kde je možné vyhľadať trasu.
     * @param mainWindow        okno, do ktorého sa vloží
     * @param travel            objekt typu {@link Travel}
     */
    public void createNewPane(Stage mainWindow, Travel travel){
        FlowPane pane = new FlowPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10000);
        pane.setVgap(5);

        pane.getChildren().add(welcome);
        pane.getChildren().add(from);
        pane.getChildren().add(fromLocation);
        pane.getChildren().add(swap);
        pane.getChildren().add(to);
        pane.getChildren().add(toLocation);
        pane.getChildren().add(datum);
        pane.getChildren().add(date);
        pane.getChildren().add(cas);
        pane.getChildren().add(time);

        pane.getChildren().add(search);
        pane.getChildren().add(output);

        welcome.setFont(new Font(20));

        output.setPrefHeight(200);
        output.setPrefWidth(600);
        output.setFont(new Font("Consolas",13));

        scroll.setContent(pane);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        date.setValue(LocalDate.now());
        final Callback<DatePicker, DateCell> dayCellFactory =
                new Callback<>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(LocalDate.now())){
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        date.setDayCellFactory(dayCellFactory);
        date.getEditor().setDisable(true);
        time.setText(LocalTime.now().format(timeFormatter));
        output.setEditable(false);
        output.setWrapText(true);

        mainWindow.setScene(new Scene(scroll,800, 600));
        mainWindow.show();

        swap.setOnAction(e -> {
            String tmp = fromLocation.getText().strip();
            fromLocation.setText(toLocation.getText().strip());
            toLocation.setText(tmp);
        });

        search.setOnAction(e -> {
            output.clear();
            pane.getChildren().remove(buyTicket);

            String from = fromLocation.getText().strip();
            String to = toLocation.getText().strip();
            String t = time.getText().strip();
            String d;
            try{
                d = travel.checkDate(date.getValue(), dateFormatter);
                this.path = new Path(travel.getTraveller(), from, to, t, d, travel.getTrains());
                travel.setPath(this.path);
                printer.printR(travel, path.getPath(), path.getTrains(), path.getDate(), path.getTimes());
                if(path.getTrains()[0] != null)
                    pane.getChildren().add(buyTicket);
            }catch(InvalidDateFormatException | InvalidTimeFormatException | WrongStationException ignored){}
        });

        buyTicket.setOnAction(e -> {
            try {
                new BuyTicketPane(mainWindow, travel.getTraveller(), travel, path);
            } catch (InvalidTimeFormatException ex) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setContentText("Nastala neočakávaná chyba, skúste to znova.");
                a.showAndWait();
            }
        });
        search.setPadding(new Insets(10, 30, 10, 30));
        buyTicket.setPadding(new Insets(10, 30, 10, 30));
        swap.setPadding(new Insets(5, 10, 5, 10));
    }

    /**
     * Metóda, ktorá sa spustí pri štarte programu.
     * @param mainWindow        okno aplikácie
     */
    public void start(Stage mainWindow) {
        mainWindow.setTitle("Vlakmi po Slovensku");

        FlowPane pane = new FlowPane();
        pane.setAlignment(Pos.CENTER);
        pane.setHgap(10000);
        pane.setVgap(5);

        Travel travel = new Travel(path);

        pane.getChildren().add(name);
        pane.getChildren().add(firstName);
        pane.getChildren().add(surname);
        pane.getChildren().add(lastName);
        pane.getChildren().add(type);
        pane.getChildren().add(comboBox);
        pane.getChildren().add(login);

        login.setPadding(new Insets(10, 30, 10, 30));

        comboBox.getItems().addAll(
                "Batoľa",
                "Dieťa",
                "Študent",
                "Dospelý",
                "Dôchodca"
        );
        comboBox.setValue("Dospelý");

        login.setOnAction(e -> {
            if(!firstName.getText().strip().equals("") && !lastName.getText().strip().equals("")){
                travel.loginUser(firstName.getText().strip(), lastName.getText().strip(), comboBox.getValue(), welcome);
                createNewPane(mainWindow, travel);
            }
        });

        mainWindow.setMaxHeight(600);
        mainWindow.setMaxWidth(800);
        mainWindow.setMinHeight(600);
        mainWindow.setMinWidth(800);
        mainWindow.setScene(new Scene(pane,800, 600));
        mainWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
