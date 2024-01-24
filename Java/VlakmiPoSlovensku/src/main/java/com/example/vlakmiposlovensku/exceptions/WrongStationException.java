package com.example.vlakmiposlovensku.exceptions;

import javafx.scene.control.Alert;

/**
 * Trieda <code>WrongStationException</code> reprezentuje vlastnú výnimku, ktorá je vyhadzovaná
 * pri nesprávnom zadaní názvu stanice v triede {@link com.example.vlakmiposlovensku.travel.Path}
 */
public class WrongStationException extends Exception{
    public WrongStationException(){
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setContentText("Neznáma stanica, skúste znova.");
        a.showAndWait();
    }
}
