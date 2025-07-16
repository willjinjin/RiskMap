package nz.ac.auckland.se281;

public class InvalidCountryName extends Exception{
  private final String countryName;

  public InvalidCountryName(String countryName) {
    super(MessageCli.INVALID_COUNTRY.getMessage(countryName));
    this.countryName = countryName;
  }

  public String getCountryName() {
    return countryName;
  }
}
