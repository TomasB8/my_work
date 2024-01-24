package com.example.vlakmiposlovensku.exceptions;

import javafx.scene.control.Alert;

/**
 * Trieda <code>InvalidIDException</code> reprezentuje vlastnú výnimku, ktorá je vyhadzovaná
 * pri nesprávnom zadaní čísla ID študenta pri bezplatnej preprave
 * v triede {@link com.example.vlakmiposlovensku.travel.Travel}
 */
public class InvalidIDException extends Exception {
    public InvalidIDException(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText("Nesprávne zadané číslo.");
        a.showAndWait();
    }
}
