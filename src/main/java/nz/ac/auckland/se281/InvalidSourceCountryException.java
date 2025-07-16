package nz.ac.auckland.se281;

public class InvalidSourceCountryException extends Exception {
  private String countryName;
  public InvalidSourceCountryException(String countryName) {
    this.countryName = countryName;
  }
  public String getCountryName() {
    return countryName;
  }
}
