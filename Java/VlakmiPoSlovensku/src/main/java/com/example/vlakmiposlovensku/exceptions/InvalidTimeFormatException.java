package com.example.vlakmiposlovensku.exceptions;

import javafx.scene.control.Alert;

/**
 * Trieda <code>InvalidTimeFormatException</code> reprezentuje vlastnú výnimku, ktorá je vyhadzovaná
 * pri nesprávnom zadaní dátumu v triede {@link com.example.vlakmiposlovensku.trains.Train}
 */
public class InvalidTimeFormatException extends Exception{
    public InvalidTimeFormatException(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText("Nesprávny formát času. Formát by mal byť HH:mm");
        a.showAndWait();
    }
}
