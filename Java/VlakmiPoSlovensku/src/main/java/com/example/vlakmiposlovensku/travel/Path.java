package com.example.vlakmiposlovensku.travel;

import com.example.vlakmiposlovensku.exceptions.InvalidTimeFormatException;
import com.example.vlakmiposlovensku.exceptions.WrongStationException;
import com.example.vlakmiposlovensku.handlers.TArray;
import com.example.vlakmiposlovensku.trains.StationsList.Station;
import com.example.vlakmiposlovensku.trains.StationsList;
import com.example.vlakmiposlovensku.trains.Train;
import com.example.vlakmiposlovensku.trains.TrainStationsGraph;
import com.example.vlakmiposlovensku.travellers.Traveller;

import java.util.List;
import java.util.Objects;

/**
 * Trieda <code>Path</code> slúžiaca na uloženie potrebných informácii o trase.
 * Trieda tieto informácie skladuje a ďalšie triedy to neskôr využívajú.
 */
public class Path {
    private String[][] times;
    private final String date;
    private int kilometres;
    private double price;
    private boolean overMidnight;
    private final int[] distances;
    private int[] number_of_stations;

    private final Traveller traveller;
    private final Train[] trains;
    private final List<Station> path;
    private final TrainStationsGraph graph;

    public Path(Traveller traveller, String from, String to, String time, String date, TArray<Train> allTrains) throws InvalidTimeFormatException, WrongStationException {
        this.traveller = traveller;
        this.resetOverMidnight();
        this.graph =  new TrainStationsGraph();
        this.path = graph.shortestPath(from, to);
        this.number_of_stations = new int[100];
        this.number_of_stations[0] = 0;
        this.trains = findTrains(path, allTrains);
        this.date = date;
        this.distances = countDistances();
        //travel.setPath(this);
        prepare(time);
    }

    /**
     * Metóda vracajúca pole vlakov.
     * @return      pole vlakov
     */
    public Train[] getTrains() {
        return trains;
    }

    /**
     * Metóda vracajúca celú cenu trasy.
     * @return      celková cena trasy
     */
    public double getPrice() {
        return price;
    }

    /**
     * Metóda nastavujúca cenu trasy.
     * @param price     cena
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Metóda resetujúca cenu trasy.
     */
    public void resetPrice(){
        this.price = 0;
    }

    /**
     * Metóda vracajúca počet kilometrov trasy.
     * @return      celkový počet kilometrov trasy
     */
    public int getKilometres() {
        return kilometres;
    }

    /**
     * Metóda nastavujúca počet kilometrov.
     * @param kilometres        počet kilometrov
     */
    public void setKilometres(int kilometres) {
        this.kilometres = kilometres;
    }

    /**
     * Metóda vracajúca vzdialenosti.
     * @return      vzdialenosti
     */
    public int[] getDistances() {
        return distances;
    }

    /**
     * Metóda nastavujúca prechod po polnoci.
     * @param overMidnight      nastavenie prechodu cez polnoc
     */
    public void setOverMidnight(boolean overMidnight){
        this.overMidnight = overMidnight;
    }

    /**
     * Metóda resetujúca prechod po polnoci.
     */
    public void resetOverMidnight(){
        this.overMidnight = false;
    }

    /**
     * Metóda zisťujúca, či už je po polnoci.
     * @return          či je po polnoci
     */
    public boolean isOverMidnight() {
        return overMidnight;
    }

    /**
     * Metóda nastavujúca počet staníc.
     * @param x             pozícia
     * @param station       stanica
     */
    public void setNumber_of_stations(int x, int station) {
        this.number_of_stations[x] = station;
    }

    /**
     * Metóda vracajúca počet staníc.
     * @return          počet staníc
     */
    public int[] getNumber_of_stations() {
        return number_of_stations;
    }

    /**
     * Metóda resetujúca počet staníc.
     */
    public void resetNumberOfStations(){
        this.number_of_stations = new int[10];
        this.number_of_stations[0] = 0;
    }

    /**
     * Metóda vracajúca trasu.
     * @return          trasa
     */
    public List<Station> getPath() {
        return path;
    }

    /**
     * Metóda vracajúca časy.
     * @return          časy odchodu vlakov
     */
    public String[][] getTimes() {
        return times;
    }

    /**
     * Metóda vracajúca dátum trasy.
     * @return          dátum trasy
     */
    public String getDate() {
        return date;
    }

    /**
     * Metóda nastavujúca časy odchodov vlakov.
     * @param times     časy vlakov
     */
    public void setTimes(String[][] times) {
        this.times = times;
    }

    /**
     * Metóda vracajúca graf so stanicami.
     * @return          graf so stanicami
     */
    public TrainStationsGraph getGraph() {
        return graph;
    }

    /**
     * Metóda slúžiaca na prípravu a načítanie jednotlivých atribútov trasy.
     * Metóda spočíta celkovú cenu trasy, celkovú dĺžku trasy, ale aj pripraví jednotlivé časy odchodov vlakov.
     * Všetko potrebné uloží do atribútov, a preto nič nevracia.
     * @param time      počiatočný čas zadaný používateľom
     * @throws InvalidTimeFormatException       ak je zle zadaný čas
     */
    private void prepare(String time) throws InvalidTimeFormatException {
        double price;
        int kilometres;
        String[][] times = new String[this.trains.length][2];

        times[0][1] = time;

        resetPrice();

        String lastTime = time;
        for(int i=0; i<this.trains.length; i++) {
            if (this.trains[i] != null) {
                kilometres = countKm(i);
                price = countPrice(i, kilometres);

                times[i] = prepareTime(i, kilometres, lastTime);

                lastTime = times[i][1];
                this.setKilometres(this.getKilometres() + kilometres);
                this.setPrice(this.getPrice() + price);
            }
        }

        this.setTimes(times);
    }

    /**
     * Metóda pripravujúca odchody vlakov.
     * @param i         pozícia v poli časov
     * @param km        počet kilometrov
     * @param time      čas odchodu
     * @return          časy odchodov vlakov
     * @throws InvalidTimeFormatException       ak je nesprávne zadaný čas
     */
    private String[] prepareTime(int i, int km, String time) throws InvalidTimeFormatException {
        String t1;
        String t2;
        if (i == 0) {
            try{
                t1 = this.trains[0].findNearestDeparture(time, this);
                int lengthMin = this.trains[0].countTime(this.trains[0].getVelocity(), km);
                t2 = this.trains[0].addToTime(this, t1, lengthMin);
            }catch(InvalidTimeFormatException e){
                throw e;
            }
        } else {
            try{
                t1 = this.trains[i].findNearestDeparture(time, this);
                int lengthMin = this.trains[i].countTime(this.trains[i].getVelocity(), km);
                t2 = this.trains[i].addToTime(this, t1, lengthMin);

            }catch(InvalidTimeFormatException e){
                throw e;
            }
        }
        return new String[]{t1,t2};
    }

    /**
     * Metóda, ktorá spočíta počet kilometrov daného vlaku a pripočíta ju do atribútu.
     * @param i     pozícia v poli
     * @return      počet kilometrov
     */
    private int countKm(int i){
        int kilometres = 0;
        int[] number_of_stations = getNumber_of_stations();
        for (int k = number_of_stations[i] + 1; k <= number_of_stations[i + 1]; k++) {
            kilometres += graph.getWeight(path.get(k - 1), path.get(k));
        }

        return kilometres;
    }

    /**
     * Metóda počítajúca vzdialenosti medzi jednotlivými stanicami.
     * @return      vzdialenosti medzi jednotlivými stanicami
     */
    private int[] countDistances(){
        int[] dis = new int[path.size()];
        dis[0] = 0;
        for(int i=1; i<path.size(); i++){
            dis[i] = dis[i-1] + graph.getWeight(path.get(i-1), path.get(i));
        }
        return dis;
    }

    /**
     * Metóda počítajúca cenu trasy pre jednotlivé vlaky.
     * Metóda túto cenu pripočíta do atribútu, čím vznikne celková cena trasy.
     * @param i     pozícia v poli
     * @param km    počet kilometrov
     * @return      cena trasy
     */
    private double countPrice(int i, int km){
        double price = 0;
        price += this.trains[i].count_price(traveller, km);

        return price;
    }

    /**
     * Metóda hľadajúca vlaky, ktoré čo najlepšie vyhovujú danej trase.
     * Metóda nájde vlaky, ktoré prejdú čo najdlhšiu trasu a tie uloží do poľa.
     * Následne z poslednej stanice hľadá ďalšie vlaky, ktoré opäť prejdú čo najdlhšiu trasu.
     * @param path      trasa, ktorú je potrebné prejsť
     * @return          zoznam vlakov
     */
    public Train[] findTrains(List<Station> path, TArray<Train> allTrains) throws WrongStationException {
        Train[] suitableTrains = new Train[10];
        //TArray<Train> trains = travel.getTrains();
        int k = 0;
        int l = 0;

        if(path == null){
            throw new WrongStationException();
        }

        while(l < path.toArray().length){
            int maxStations = 0;
            int maxStationsIndex = -1;

            for(int j=0; j<allTrains.size(); j++){
                if(allTrains.get(j) == null)
                    break;

                int i = l;

                StationsList current = allTrains.get(j).getRoute();
                int found = 0;
                while(current != null){
                    if(i < path.toArray().length && Objects.equals(path.get(i).getName(), current.getStation().getName())){
                        current = current.getNext();
                        i++;
                        found++;
                        while(i < path.toArray().length && current != null && Objects.equals(path.get(i).getName(), current.getStation().getName())){
                            current = current.getNext();
                            i++;
                            found++;
                        }
                    } else {
                        current = current.getNext();
                    }
                }

                if (found > maxStations) {
                    maxStations = found;
                    maxStationsIndex = j;
                }
            }

            if(maxStations == 0){
                return suitableTrains;
            }

            if (maxStationsIndex != -1) {
                suitableTrains[k] = allTrains.get(maxStationsIndex);
                this.setNumber_of_stations(k+1, l+maxStations-1);
                k++;
                l += maxStations;
            }
        }

        if(suitableTrains[0] == null){
            return null;
        }

        return suitableTrains;
    }
}
