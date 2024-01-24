package com.example.vlakmiposlovensku.exceptions;

import javafx.scene.control.Alert;

/**
 * Trieda <code>InvalidCardException</code> reprezentuje vlastnú výnimku, ktorá je vyhadzovaná
 * pri nesprávnom zadaní platobnej karty v triede {@link com.example.vlakmiposlovensku.handlers.CardHandler}
 */
public class InvalidCardException extends Exception{
    public InvalidCardException(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText("Nesprávne zadaná platobná karta, skontrolujte údaje.");
        a.showAndWait();
    }
}
