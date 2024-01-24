package com.example.vlakmiposlovensku.trains;

import com.example.vlakmiposlovensku.exceptions.WrongStationException;
import com.example.vlakmiposlovensku.handlers.HandleFiles;
import com.example.vlakmiposlovensku.trains.StationsList.Station;

import java.util.*;

/**
 * Trieda <code>TrainStationsGraph</code> reprezentuje stanice vo forme grafu.
 * Do tejto triedy sú vložené všetky stanice z triedy {@link Station} medzi ktorými sú hrany v prípade,
 * že sa dá nejakým vlakom dostať z jednej stanice do druhej.
 * Tento graf sa napĺňa v triede {@link HandleFiles}.
 *
 * @see Station
 * @see HandleFiles
 */
public class TrainStationsGraph {
    private final int[][] adjacencyMatrix;
    private final Map<Station, Integer> stationIndexMap;
    private Station[] stations;

    public TrainStationsGraph(){
        HandleFiles fHandler = new HandleFiles();
        this.stationIndexMap = fHandler.createStations(this);
        this.adjacencyMatrix = new int[stations.length][stations.length];
        fHandler.addEdges(this);
    }

    /**
     * Metóda vkladajúca stanice typu {@link Station} do atribútu stations.
     * @param stations      stanice
     */
    public void setStations(Station[] stations){
        this.stations = stations;
    }

    /**
     * Metóda pridávajúca hranu medzi dvomi stanicami, pričom váha je ich vzdialenosť v kilometroch.
     * @param fromStation       počiatočná stanica
     * @param toStation         konečná stanica
     * @param weight            vzdialenosť staníc v kilometroch
     */
    public void addEdge(Station fromStation, Station toStation, int weight){
        int fromIndex = stationIndexMap.get(fromStation);
        int toIndex = stationIndexMap.get(toStation);
        adjacencyMatrix[fromIndex][toIndex] = weight;
        adjacencyMatrix[toIndex][fromIndex] = weight;
    }

    /**
     * Metóda vracajúca váhu hrany (vzdialenosť v kilometroch) medzi dvomi stanicami.
     * @param fromStation       počiatočná stanica
     * @param toStation         konečná stanica
     * @return                  vzdialenosť medzi dvomi stanicami
     */
    public int getWeight(Station fromStation, Station toStation){
        int fromIndex = stationIndexMap.get(fromStation);
        int toIndex = stationIndexMap.get(toStation);
        return adjacencyMatrix[fromIndex][toIndex];
    }

    /**
     * Metóda, ktorá prechádza pole staníc, v ktorom chce nájsť stanicu so zadaným menom.
     * @param name      názov hľadanej stanice
     * @return          stanica so zadaným menom
     */
    public Station findStationByName(String name) {
        for (Station station : stations) {
            if (station.name.equals(name)) {
                return station;
            }
        }
        return null;
    }

    /**
     * Metóda, ktorá slúži na nájdenie najkratšej cesty z jednej stanice do druhej.
     * Na nájdenie takejto trasy používa Dijkstrov algoritmus, ktorý slúži na nájdenie najkratšej cesty
     * v hranovo-ohodnotenom grafe.
     * @param startStation      počiatočná stanica
     * @param endStation        konečná stanica
     * @return                  zoznam staníc tvoriacich najkratšiu trasu
     */
    public List<Station> shortestPath(String startStation, String endStation) throws WrongStationException {
        Station from = findStationByName(startStation);
        Station to = findStationByName(endStation);

        if(stationIndexMap.get(from)==null || stationIndexMap.get(to)==null){
            throw new WrongStationException();
        }
        //try{
            int start = stationIndexMap.get(from);
            int end = stationIndexMap.get(to);

            int[] distances = new int[stations.length];
            boolean[] visited = new boolean[stations.length];
            int[] previous = new int[stations.length];

            Arrays.fill(distances, Integer.MAX_VALUE);
            distances[start] = 0;

            PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingInt(i -> distances[i]));
            queue.offer(start);

            while (!queue.isEmpty()) {
                int current = queue.poll();
                if (current == end) {
                    break;
                }
                if (visited[current]) {
                    continue;
                }
                visited[current] = true;

                for (int i = 0; i < stations.length; i++) {
                    if (adjacencyMatrix[current][i] > 0) {
                        int nextDistance = distances[current] + adjacencyMatrix[current][i];
                        if (nextDistance < distances[i]) {
                            distances[i] = nextDistance;
                            previous[i] = current;
                            queue.offer(i);
                        }
                    }
                }
            }

            List<Station> path = new ArrayList<>();
            for (int current = end; current != start; current = previous[current]) {
                path.add(stations[current]);
            }
            path.add(from);
            Collections.reverse(path);

            return path;
            /*
        }catch(NullPointerException e){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setContentText("Nesprávne zadaná stanica, skúste to znova.");
            a.showAndWait();
        }

             */
        //return null;
    }
}
