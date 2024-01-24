package com.example.vlakmiposlovensku.exceptions;

import javafx.scene.control.Alert;

/**
 * Trieda <code>InvalidDateFormatException</code> reprezentuje vlastnú výnimku, ktorá je vyhadzovaná
 * pri nesprávnom zadaní dátumu v triede {@link com.example.vlakmiposlovensku.gui.SystemGUI}
 */
public class InvalidDateFormatException extends Exception{
    public InvalidDateFormatException(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText("Nesprávny formát dátumu. Formát by mal byť dd.MM.yyyy");
        a.showAndWait();
    }
}