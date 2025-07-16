package nz.ac.auckland.se281;

public class Country {
  String name;
  String continent;
  String fuelCost;

  public Country(String name, String continent, String fuelCost) {
    this.name = name;
    this.continent = continent;
    this.fuelCost = fuelCost;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = prime * name.length() + continent.length() + Integer.parseInt(fuelCost);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Country other = (Country) obj;
    return name.equals(other.name)
      && continent.equals(other.continent)
      && fuelCost.equals(other.fuelCost);
  }
}
