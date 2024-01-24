package com.example.vlakmiposlovensku.gui;

import com.example.vlakmiposlovensku.exceptions.InvalidTimeFormatException;
import com.example.vlakmiposlovensku.handlers.FunctionsInterface;
import com.example.vlakmiposlovensku.trains.Train;
import com.example.vlakmiposlovensku.travel.Path;
import com.example.vlakmiposlovensku.travel.Travel;
import com.example.vlakmiposlovensku.trains.StationsList.Station;
import com.example.vlakmiposlovensku.travellers.Student;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Trieda <code>TextOutput</code> slúži na vypísanie potrebných informácii do okna aplikácie.
 */
public class TextOutput {
    private final TextArea output;

    public TextOutput(TextArea output){
        this.output = output;
    }

    /**
     * Metóda, ktorá privíta cestujúceho.
     * @param welcome        Label, do ktorého sa text vloží
     * @param travel            objekt typu {@link Travel}
     */
    public static void welcome(Label welcome, Travel travel){
        welcome.setText("Dobrý deň, " + travel.getTraveller().inform());
    }

    /**
     * Metóda slúžiaca na vypísanie nájdenej trasy, vlakov, časov, obsadenosti vlakov, ale aj ceny a
     * počtu kilometrov.
     * @param travel        objekt typu {@link Travel}
     * @param path          objekt typu {@link Path}
     * @param trains        pole vlakov
     * @param date          dátum trasy
     * @param times         časy odchodov vlakov
     * @throws InvalidTimeFormatException       Ak je nesprávne zadaný čas
     */
    public void printR(Travel travel, List<Station> path, Train[] trains, String date, String[][] times) throws InvalidTimeFormatException {
        FunctionsInterface lambdaRound = (value) -> Math.round(value * 100.0) / 100.0;

        if(trains[0] == null){
            output.appendText("Pre zvolenú trasu neexistuje vyhovujúci vlak!!!");
            return;
        }

        if(travel.getPath().isOverMidnight()){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDate datum = LocalDate.parse(date, formatter);
            datum = datum.plusDays(1);
            output.appendText(formatter.format(datum) + "\n");
        }else{
            output.appendText(date + "\n");
        }

        output.appendText("...............................................................................\n");

        for(int i=0; i<path.size(); i++){
            output.appendText(path.get(i).getName());
            if(i+1 == path.size()){
                output.appendText("\n");
            }else{
                output.appendText(" -> ");
            }
        }

        output.appendText("...............................................................................\n");

        for(int i=0; i<trains.length; i++){
            if(trains[i] != null){
                String trainS = String.format("%s -> %s - %s", trains[i].getName(),path.get(travel.getPath().getNumber_of_stations()[i]).getName(),
                        path.get(travel.getPath().getNumber_of_stations()[i+1]).getName());
                output.appendText(String.format("%-45s %s -> %s %10d/%d\n",trainS,times[i][0],times[i][1],trains[i].getFreeSeats(),
                        trains[i].getCapacity()));
                if(travel.getTraveller() instanceof Student && !trains[i].isFree()){
                    output.appendText("\tNa tento vlak už nie sú voľné lístky.\n");
                }
            }
        }

        output.appendText("Počet kilometrov: " + travel.getPath().getKilometres() + " km\n");
        output.appendText("Celková suma: " + lambdaRound.round(travel.getPath().getPrice()) + "€\n");
        output.appendText("-------------------------------------------------------------------------------\n");
    }
}
