package nz.ac.auckland.se281;

public class InvalidDestinationCountryException extends Exception {
  private String countryName;
  public InvalidDestinationCountryException(String countryName) {
    this.countryName = countryName;
  }
  public String getCountryName() {
    return countryName;
  }
}
