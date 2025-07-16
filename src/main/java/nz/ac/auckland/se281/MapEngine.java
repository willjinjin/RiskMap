package nz.ac.auckland.se281;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/** This class is the main entry point. */
public class MapEngine {
  Map<Country, List<Country>> worldMap = new LinkedHashMap<>();
  Map<String, Country> countryLookUp = new LinkedHashMap<>();

  public MapEngine() {
    // add other code here if you wan
    loadMap(); // keep this mehtod invocation
  }

  /** invoked one time only when constracting the MapEngine class. */
  private void loadMap() {

    List<String> countries = Utils.readCountries();
    List<String> adjacencies = Utils.readAdjacencies();

    loadCountries(countries);
    loadAdjacencies(adjacencies);
  }

  public void loadCountries(List<String> countries) {
    for (String country : countries) {
      String[] parts = country.split(",");
      String name = parts[0].trim();
      String continent = parts[1].trim();
      String fuelCost = parts[2].trim();

      Country c = new Country(name, continent, fuelCost);
      countryLookUp.put(name, c);
      worldMap.put(c, new ArrayList<>());
    }
  }

  public void loadAdjacencies(List<String> adjacencies) {
    for (String adjacent : adjacencies) {
      String[] parts = adjacent.split(",");
      String countryName = parts[0].trim();
      Country country = countryLookUp.get(countryName);
      for (int i = 1; i < parts.length; i++) {
        String neighborName = parts[i].trim();
        Country neighbor = countryLookUp.get(neighborName);
        if (!worldMap.get(country).contains(neighbor)) {
          worldMap.get(country).add(neighbor);
        }
      }
    }
  }

  /** this method is invoked when the user run the command info-country. */
  public void showInfoCountry() {
    String countryName = promptForCountryName();
    printCountryInfo(countryName);
  }

  public String promptForCountryName() {
    try {
      MessageCli.INSERT_COUNTRY.printMessage();
      String countryName = Utils.scanner.nextLine().trim();
      countryName = Utils.capitalizeFirstLetterOfEachWord(countryName);
      if (!countryLookUp.containsKey(countryName)) {
        throw new InvalidCountryName(countryName);
      }
      return countryName;
    } catch (InvalidCountryName e) {
      MessageCli.INVALID_COUNTRY.printMessage(e.getCountryName());
      return promptForCountryName();
    }
  }

  public void printCountryInfo(String countryName) {
    Country country = countryLookUp.get(countryName);
    String continent = country.continent;
    String fuelCost = country.fuelCost;
    List<Country> neighbors = worldMap.get(country);
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < neighbors.size(); i++) {
      sb.append(neighbors.get(i).name);
      if (i < neighbors.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    String neighborsString = sb.toString();
    MessageCli.COUNTRY_INFO.printMessage(countryName, continent, fuelCost, neighborsString);
  }

  /** this method is invoked when the user run the command route. */
  public void showRoute() {
    String sourceName = promptForValidCurrentCountry();
    String destinationName = promptForValidDestinationCountry();

    if (sourceName.equals(destinationName)) {
      MessageCli.NO_CROSSBORDER_TRAVEL.printMessage();
      return;
    }

    Country source = countryLookUp.get(sourceName);
    Country destination = countryLookUp.get(destinationName);

    List<Country> path = findShortestRoute(source, destination);

    printShortestRouteInfo(path);
    printTotalFuelCost(path);
    Map<String, Integer> continentsVisited = getContinentsVisited(path);
    printContinentsVisited(continentsVisited);
  }

  public String promptForValidCurrentCountry() {
    String sourceName = null;
    while (true) {
      try {
        MessageCli.INSERT_SOURCE.printMessage();
        sourceName = Utils.scanner.nextLine().trim();
        sourceName = Utils.capitalizeFirstLetterOfEachWord(sourceName);
        if (!countryLookUp.containsKey(sourceName)) {
          throw new InvalidSourceCountryException(sourceName);
        }
        return sourceName;
      } catch (InvalidSourceCountryException e) {
        MessageCli.INVALID_COUNTRY.printMessage(e.getCountryName());
      }
    }
  }

  public String promptForValidDestinationCountry() {
    String destinationName = null;
    while (true) {
      try {
        MessageCli.INSERT_DESTINATION.printMessage();
        destinationName = Utils.scanner.nextLine().trim();
        destinationName = Utils.capitalizeFirstLetterOfEachWord(destinationName);
        if (!countryLookUp.containsKey(destinationName)) {
          throw new InvalidDestinationCountryException(destinationName);
        }
        return destinationName;
      } catch (InvalidDestinationCountryException e) {
        MessageCli.INVALID_COUNTRY.printMessage(e.getCountryName());
      }
    }
  }

  public List<Country> findShortestRoute(Country source, Country destination) {
    Queue<Country> queue = new LinkedList<>();
    Set<Country> visited = new HashSet<>();
    Map<Country, Country> previous = new HashMap<>();

    queue.add(source);
    visited.add(source);

    while (!queue.isEmpty()) {
      Country current = queue.poll();
      if (current.equals(destination)) {
        break;
      }
      for (Country neighbor : worldMap.get(current)) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          previous.put(neighbor, current);
          queue.add(neighbor);
        }
      }
    }

    List<Country> path = new ArrayList<>();
    Country current = destination;
    while (current != null) {
      path.add(current);
      current = previous.get(current);
    }
    return path;
  }

  public void printShortestRouteInfo(List<Country> path) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = path.size() - 1; i >= 0; i--) {
      sb.append(path.get(i).name);
      if (i > 0) {
        sb.append(", ");
      }
    }
    sb.append("]");
    String route = sb.toString();
    MessageCli.ROUTE_INFO.printMessage(route);
  }

  public void printTotalFuelCost(List<Country> path) {
    int totalFuelCost = 0;
    for (int i = path.size() - 2; i > 0; i--) { // Exclude first and last
      String countryName = path.get(i).name;
      Country country = countryLookUp.get(countryName);
      totalFuelCost += Integer.parseInt(country.fuelCost);
    }
    MessageCli.FUEL_INFO.printMessage(String.valueOf(totalFuelCost));
  }

  public Map<String, Integer> getContinentsVisited(List<Country> path) {
    Map<String, Integer> continentFuel = new LinkedHashMap<>();

    String firstContinent = path.get(path.size() - 1).continent;
    continentFuel.put(firstContinent, 0);

    for (int i = path.size() - 2; i > 0; i--) {
      Country country = path.get(i);
      String continent = country.continent;
      int fuel = Integer.parseInt(country.fuelCost);
      continentFuel.put(continent, continentFuel.getOrDefault(continent, 0) + fuel);
    }

    String lastContinent = path.get(0).continent;
    continentFuel.computeIfAbsent(lastContinent, k -> 0);

    return continentFuel;
  }

  public void printContinentsVisited(Map<String, Integer> continentFuel) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    int count = 0;
    Country fuelMax = null;
    int size = continentFuel.size();
    for (String continent : continentFuel.keySet()) {
      sb.append(continent).append(" (").append(continentFuel.get(continent)).append(")");
      count++;
      if (count < size) {
        sb.append(", ");
      }
      if (fuelMax == null || continentFuel.get(continent) > continentFuel.get(fuelMax.continent)) {
        fuelMax = new Country(continent, continent, String.valueOf(continentFuel.get(continent)));
      }
    }
    sb.append("]");
    String highestFuelContinent = fuelMax.name + " (" + fuelMax.fuelCost + ")";
    MessageCli.CONTINENT_INFO.printMessage(sb.toString());
    MessageCli.FUEL_CONTINENT_INFO.printMessage(highestFuelContinent);
  }
}
