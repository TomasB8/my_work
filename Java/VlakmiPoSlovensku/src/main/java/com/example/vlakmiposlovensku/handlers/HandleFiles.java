package com.example.vlakmiposlovensku.handlers;

import com.example.vlakmiposlovensku.trains.*;
import com.example.vlakmiposlovensku.trains.StationsList.Station;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Trieda <code>HandleFiles</code> ktorá má na starosti spracovanie údajov zo súborov, v ktorých sú
 * uložené údaje o vlakoch a staniciach.
 */
public class HandleFiles {
    private final String stationsFile = "src\\main\\java\\com\\example\\vlakmiposlovensku\\files\\stations";
    private final String edgesFile = "src\\main\\java\\com\\example\\vlakmiposlovensku\\files\\edges";
    private final String fastTrainsFile = "src\\main\\java\\com\\example\\vlakmiposlovensku\\files\\fast_trains";
    private final String personalTrainsFile = "src\\main\\java\\com\\example\\vlakmiposlovensku\\files\\personal_trains";

    public HandleFiles(){}

    /**
     * Metóda, ktorá vracia počet riadkov v súbore, aby bolo jasné, aké veľké pole
     * je potrebné vytvoriť na uloženie všetkých vlakov a staníc.
     * @param fileName      názov súboru, s ktorým metóda pracuje
     * @return              počet riadkov v súbore
     */
    public static int countLines(String fileName) {
        Path path = Paths.get(fileName);

        int lines = 0;
        try {
            lines = (int) Files.lines(path).count();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    /**
     * Metóda vytvárajúca pole staníc, ktoré sú následne vrátené, ako HashMap.
     * Metóda postupne prechádza všetky riadky súboru a stanice vkladá do Map. Načítané stanice potom vloží do
     * {@link TrainStationsGraph}, kde jednotlivé stanice reprezentujú uzly v grafe.
     * @param graph     odkaz na graf, do ktorého budú stanice vložené
     * @return          mapa vytvorených staníc
     */
    public Map<Station, Integer> createStations(TrainStationsGraph graph){
        Map<Station, Integer> stations = new HashMap<>();

        int n = countLines(stationsFile);
        Station[] stationsList = new Station[n];
        int j = 0;
        try {
            File myObj = new File(stationsFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                stationsList[j] = new Station(myReader.nextLine());
                j++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Problém pri čítaní zo súboru.");
            e.printStackTrace();
        }

        graph.setStations(stationsList);

        for(int i=0; i<stationsList.length; i++){
            stations.put(stationsList[i], i);
        }

        return stations;
    }

    /**
     * Metóda vytvárajúca spojenia medzi stanicami, ktorých váha je reprezentovaná vzdialenosťou týchto staníc
     * v kilometroch.
     * Metóda prechádza všetky riadky súboru, z ktorého si načíta stanice a vytvorí hranu medzi nimi s váhou
     * zadanou v súbore.
     * @param graph     odkaz na graf, v ktorom sa vytvárajú hrany
     */
    public void addEdges(TrainStationsGraph graph){
        int n = countLines(edgesFile);
        Station[] stations1 = new Station[n];
        Station[] stations2 = new Station[n];
        int[] km = new int[n];
        int j = 0;
        try {
            File myObj = new File(edgesFile);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String[] tmp;
                tmp = myReader.nextLine().split(",");
                stations1[j] = graph.findStationByName(tmp[0]);
                stations2[j] = graph.findStationByName(tmp[1]);
                km[j] = Integer.parseInt(tmp[2]);
                j++;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Problém pri čítaní zo súboru.");
            e.printStackTrace();
        }

        for(int k=0; k<n; k++){
            graph.addEdge(stations1[k], stations2[k], km[k]);
        }
    }

    /**
     * Metóda, ktorá zo súborov načíta jednotlivé vlaky a vloží ich do vlastnej generickej triedy {@link TArray},
     * ktorú na záver aj vráti.
     * Metóda vloží najprv rýchle vlaky a po prečítaní celého súboru pre rýchle vlaky,
     * načíta aj osobné vlaky.
     * Všetky vlaky vloží do jedného poľa, ktoré je metódou vrátené a využité v triede {@link com.example.vlakmiposlovensku.travel.Travel}
     * @return          vlastná generická trieda, pole vlakov
     */
    public TArray<Train> createTrains(){
        int n_ft = countLines(fastTrainsFile);
        int n_pt = countLines(personalTrainsFile);

        TArray<Train> trains = new TArray<>(n_ft+n_pt);

        int j = 0;
        try {
            File fastTrains = new File(fastTrainsFile);
            File personalTrains = new File(personalTrainsFile);
            Scanner ftReader = new Scanner(fastTrains);
            Scanner ptReader = new Scanner(personalTrains);
            while (ftReader.hasNextLine()) {
                String[] tmp;
                tmp = ftReader.nextLine().split(";");

                String names = tmp[0];
                int velocities = Integer.parseInt(tmp[1]);
                int capacities = Integer.parseInt(tmp[2]);
                String[] timetables = tmp[3].split(",");
                int freeTickets = Integer.parseInt(tmp[4]);
                double pricesPerKm = Double.parseDouble(tmp[5]);
                String[] routes = tmp[6].split(",");

                trains.set(j, new FastTrain(names, velocities, capacities, timetables, freeTickets, pricesPerKm, routes));
                j++;
            }

            while (ptReader.hasNextLine()) {
                String[] tmp;
                tmp = ptReader.nextLine().split(";");

                String names = tmp[0];
                int velocities = Integer.parseInt(tmp[1]);
                int capacities = Integer.parseInt(tmp[2]);
                String[] timetables = tmp[3].split(",");
                int freeTickets = Integer.parseInt(tmp[4]);
                double pricesPerKm = Double.parseDouble(tmp[5]);
                String[] routes = tmp[6].split(",");

                trains.set(j, new PersonalTrain(names, velocities, capacities, timetables, freeTickets, pricesPerKm, routes));
                j++;
            }

            ftReader.close();
            ptReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Problém pri čítaní zo súboru.");
            e.printStackTrace();
        }

        return trains;
    }
}
