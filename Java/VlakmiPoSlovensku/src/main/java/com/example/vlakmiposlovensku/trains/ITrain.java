package com.example.vlakmiposlovensku.trains;

import com.example.vlakmiposlovensku.exceptions.InvalidTimeFormatException;
import com.example.vlakmiposlovensku.travel.Path;
import com.example.vlakmiposlovensku.travellers.Traveller;

/**
 * Rozhranie <code>ITrain</code> obsahuje predpis metód pre vlaky {@link Train}.
 *
 * @see Train
 */
public interface ITrain {
    String findNearestDeparture(String time, Path path) throws InvalidTimeFormatException;
    int countTime(int velocity, int distance);
    String addToTime(Path path, String time, int minutes) throws InvalidTimeFormatException;
    double count_price(Traveller traveller, int total_km);
    boolean checkFree();
}
