package com.example.vlakmiposlovensku.travel;

import com.example.vlakmiposlovensku.exceptions.InvalidDateFormatException;
import com.example.vlakmiposlovensku.exceptions.InvalidIDException;
import com.example.vlakmiposlovensku.handlers.HandleFiles;
import com.example.vlakmiposlovensku.gui.TextOutput;
import com.example.vlakmiposlovensku.gui.TravelGUI;
import com.example.vlakmiposlovensku.handlers.TArray;
import com.example.vlakmiposlovensku.trains.*;
import com.example.vlakmiposlovensku.travellers.*;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Trieda <code>Travel</code> reprezentuje cestovanie vlakom určitého typu cestujúceho.
 * Vlaky sú reprezentované ako objekty triedy {@link Train} a cestujúci ako objekty triedy {@link Traveller}.
 * Vlaky sú vložené do vlastnej generickej triedy {@link TArray}.
 * {@link Simulation} slúži na implementáciu návrhového vzoru Strategy.
 * V triede {@link Path} sú uložené potrebné informácie o celej trase, ktoré táto trieda využíva.
 *
 * @see Train
 * @see Traveller
 * @see TArray
 * @see Simulation
 * @see Path
 */
public class Travel {

    private TArray<Train> trains;
    private Path path;
    private Traveller traveller;
    private Simulation simulation;

    public Travel(Path path){
        this.trains = new TArray<>(10);
        this.path = path;

        HandleFiles fHandler = new HandleFiles();

        this.trains = fHandler.createTrains();
        this.simulation = new FastSimulation(this);
    }

    /**
     * Metóda vracajúca objekt triedy {@link Path}.
     * @return      objekt triedy {@link Path}
     */
    public Path getPath() {
        return path;
    }

    /**
     * Metóda nastavujúca objekt triedy {@link Path}.
     */
    public void setPath(Path path) {
        this.path = path;
    }

    /**
     * Metóda vytvárajúca pole vlakov.
     */
    public void setTrains(TArray<Train> trains) {
        this.trains = trains;
    }

    /**
     * Metóda vracajúca pole vlakov.
     * @return      pole vlakov
     */
    public TArray<Train> getTrains() {
        return trains;
    }

    /**
     * Metóda vracajúca objekt typu {@link Traveller}.
     * @return      objekt typu {@link Traveller}
     */
    public Traveller getTraveller() {
        return traveller;
    }

    /**
     * Metóda, ktorá na základe boolean hodnoty, zmení simuláciu na pomalú.
     * @param fast      hodnota z GUI (checkbox)
     */
    public void changeSimulation(boolean fast){
        if(!fast){
            this.simulation = new SlowSimulation(this);
        }
    }

    /**
     * Metóda spúšťajúca simuláciu.
     * @param km        počet kilometrov
     * @param pane      okno, na ktoré sa budú vypisovať údaje
     */
    public void simulate(int km, TravelGUI pane){
        simulation.simulateTravel(km, pane);
    }

    /**
     * Metóda kontrolujúca správnosť zadaného ID v prípade študenta.
     * ID musí mať práve 7 čísel.
     * @param id        zadané ID pre cestovanie zadarmo
     * @throws InvalidIDException       nesprávne zadané ID študenta
     */
    public void checkID(String id) throws InvalidIDException {
        if(!id.matches("\\d{7}")){
            throw new InvalidIDException();
        }
    }

    /**
     * Metóda kontrolujúca správnosť zadaného dátumu.
     * @param datum         zadaný dátum
     * @param formatter     požadovaný formát dátumu
     * @return              dátum
     * @throws InvalidDateFormatException       nesprávne zadaný dátum
     */
    public String checkDate(LocalDate datum, DateTimeFormatter formatter) throws InvalidDateFormatException {
        try{
            String d = datum.format(formatter);
            if(!d.matches("\\d{2}\\.\\d{2}\\.\\d{4}")){
                throw new InvalidDateFormatException();
            }else{
                return d;
            }
        }catch (DateTimeParseException | NullPointerException e){
            throw new InvalidDateFormatException();
        }
    }

    /**
     * Metóda, ktorá prihlási cestujúceho, pričom na základe výberu uloží typ cestujúceho.
     * @param firstName         krstné meno cestujúceho
     * @param lastName          priezvisko cestujúceho
     * @param type              typ cestujúceho
     * @param welcome           text na privítanie
     */
    public void loginUser(String firstName, String lastName, String type, Label welcome){
        switch(type){
            case "Batoľa":
                this.traveller = new Infants(firstName, lastName);
                TextOutput.welcome(welcome, this);
                break;

            case "Dieťa":
                this.traveller = new Kids(firstName, lastName);
                TextOutput.welcome(welcome, this);
                break;

            case "Študent":
                this.traveller = new Student(firstName, lastName);
                TextOutput.welcome(welcome, this);
                break;

            case "Dospelý":
                this.traveller = new Adult(firstName, lastName);
                TextOutput.welcome(welcome, this);
                break;

            case "Dôchodca":
                this.traveller = new Pensioner(firstName, lastName);
                TextOutput.welcome(welcome, this);
                break;
        }
    }
}
