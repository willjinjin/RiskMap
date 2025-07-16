package nz.ac.auckland.se281;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import static nz.ac.auckland.se281.Main.Command.INFO_COUNTRY;
import static nz.ac.auckland.se281.Main.Command.ROUTE;
import static nz.ac.auckland.se281.MessageCli.CONTINENT_INFO;
import static nz.ac.auckland.se281.MessageCli.COUNTRY_INFO;
import static nz.ac.auckland.se281.MessageCli.FUEL_CONTINENT_INFO;
import static nz.ac.auckland.se281.MessageCli.FUEL_INFO;
import static nz.ac.auckland.se281.MessageCli.INVALID_COUNTRY;
import static nz.ac.auckland.se281.MessageCli.NO_CROSSBORDER_TRAVEL;
import static nz.ac.auckland.se281.MessageCli.ROUTE_INFO;

@RunWith(Suite.class)
@SuiteClasses({
  MainTest.Task1.class ,MainTest.Task2.class
})
public class MainTest {

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task1 extends SysCliTest {

    public Task1() {
      super(Main.class);
    }

    @Test
    public void T1_01_info_india() throws Exception {
      runCommands(INFO_COUNTRY, "India");
      assertContains(
          COUNTRY_INFO.getMessage("India", "Asia", "3", "[Middle East, Afghanistan, China, Siam]"));
    }

    @Test
    public void T1_02_info_invalid_then_brazil() throws Exception {
      runCommands(INFO_COUNTRY, "hello", "Brazil");
      assertContains(INVALID_COUNTRY.getMessage("Hello"));
      assertContains(
          COUNTRY_INFO.getMessage(
              "Brazil", "South America", "2", "[Venezuela, North Africa, Argentina, Peru]"));
    }

    @Test
    public void T1_03_info_case_sensitive_irkutsk() throws Exception {
      runCommands(INFO_COUNTRY, "irkuTsK", "irkutsk");
      assertDoesNotContain(INVALID_COUNTRY.getMessage("irkuTsK"));
      assertContains(INVALID_COUNTRY.getMessage("IrkuTsK"));
      assertContains(
          COUNTRY_INFO.getMessage(
              "Irkutsk", "Asia", "4", "[Siberia, Yakutsk, Kamchatka, Mongolia]"));
    }

    @Test
    public void T1_04_info_multiple_invalid_then_alaska() throws Exception {
      runCommands(INFO_COUNTRY, "h", "w", "j", "Alaska");
      assertContains(INVALID_COUNTRY.getMessage("H"));
      assertContains(INVALID_COUNTRY.getMessage("W"));
      assertContains(INVALID_COUNTRY.getMessage("J"));
      assertContains(
          COUNTRY_INFO.getMessage(
              "Alaska", "North America", "1", "[Alberta, Northwest Territory, Kamchatka]"));
    }

    @Test
    public void T1_05_info_congo_and_siam() throws Exception {
      runCommands(INFO_COUNTRY, "Congo", INFO_COUNTRY, "Siam");
      assertContains(
          COUNTRY_INFO.getMessage(
              "Congo", "Africa", "1", "[North Africa, East Africa, South Africa]"));
      assertContains(COUNTRY_INFO.getMessage("Siam", "Asia", "9", "[India, China, Indonesia]"));
    }

    @Test
    public void T1_06_info_newguinea_siam_madagascar() throws Exception {
      runCommands(INFO_COUNTRY, "New Guinea", INFO_COUNTRY, "Siam", INFO_COUNTRY, "Madagascar");
      assertContains(
          COUNTRY_INFO.getMessage(
              "New Guinea", "Australia", "2", "[Indonesia, Eastern Australia, Western Australia]"));
      assertContains(COUNTRY_INFO.getMessage("Siam", "Asia", "9", "[India, China, Indonesia]"));
      assertContains(
          COUNTRY_INFO.getMessage("Madagascar", "Africa", "4", "[East Africa, South Africa]"));
    }
  }

  @FixMethodOrder(MethodSorters.NAME_ASCENDING)
  public static class Task2 extends SysCliTest {

    public Task2() {
      super(Main.class);
    }

    @Test
    public void T2_01_no_crossborder_same_country_alberta() throws Exception {
      runCommands(ROUTE, "Alberta", "Alberta");
      assertContains(NO_CROSSBORDER_TRAVEL.getMessage());
      assertDoesNotContain("The fastest route is: ");
    }

    @Test
    public void T2_02_no_crossborder_same_country_egypt() throws Exception {
      runCommands(ROUTE, "Egypt", "Egypt");
      assertContains(NO_CROSSBORDER_TRAVEL.getMessage());
      assertDoesNotContain("You will spend this amount of fuel for your journey");
    }

    @Test
    public void T2_03_route_direct_india_siam() throws Exception {
      runCommands(ROUTE, "India", "Siam");
      assertContains(ROUTE_INFO.getMessage("[India, Siam]"));
    }

    @Test
    public void T2_04_route_invalid_then_valid_start_country() throws Exception {
      runCommands(ROUTE, "inDiA", "India", "Siam");
      assertContains(INVALID_COUNTRY.getMessage("InDiA"));
      assertContains(ROUTE_INFO.getMessage("[India, Siam]"));
    }

    @Test
    public void T2_05_route_multiple_invalid_then_valid() throws Exception {
      runCommands(ROUTE, "inDiA", "India", "hello", "world", "Siam");
      assertContains(INVALID_COUNTRY.getMessage("InDiA"));
      assertContains(INVALID_COUNTRY.getMessage("Hello"));
      assertContains(INVALID_COUNTRY.getMessage("World"));
      assertContains(ROUTE_INFO.getMessage("[India, Siam]"));
    }

    @Test
    public void T2_06_route_length_3_congo_egypt() throws Exception {
      runCommands(ROUTE, "Congo", "Egypt");
      assertContains(ROUTE_INFO.getMessage("[Congo, North Africa, Egypt]"));
    }

    @Test
    public void T2_07_route_length_4_argentina_congo() throws Exception {
      runCommands(ROUTE, "argentina", "congo");
      assertContains(ROUTE_INFO.getMessage("[Argentina, Brazil, North Africa, Congo]"));
    }

    @Test
    public void T2_08_route_length_4_congo_argentina() throws Exception {
      runCommands(ROUTE, "congo", "argentina");
      assertContains(ROUTE_INFO.getMessage("[Congo, North Africa, Brazil, Argentina]"));
    }

    @Test
    public void T2_09_route_long_eastern_aus_gb() throws Exception {
      runCommands(ROUTE, "Eastern Australia", "Great Britain");
      assertContains(
          ROUTE_INFO.getMessage(
              "[Eastern Australia, New Guinea, Indonesia, Siam, India, Middle East, Ukraine,"
                  + " Scandinavia, Great Britain]"));
    }

    @Test
    public void T2_10_continent_info_europe_asia_aus() throws Exception {
      runCommands(ROUTE, "Great Britain", "Eastern Australia");
      assertContains(
          ROUTE_INFO.getMessage(
              "[Great Britain, Scandinavia, Ukraine, Ural, China, Siam, Indonesia, New Guinea,"
                  + " Eastern Australia]"));
      assertContains(CONTINENT_INFO.getMessage("[Europe (10), Asia (22), Australia (5)]"));
    }

    @Test
    public void T2_11_continent_info_single_asia() throws Exception {
      runCommands(ROUTE, "Ural", "Japan");
      assertContains(ROUTE_INFO.getMessage("[Ural, Siberia, Mongolia, Japan]"));
      assertContains(CONTINENT_INFO.getMessage("[Asia (18)]"));
    }

    @Test
    public void T2_12_continent_info_africa_europe_asia() throws Exception {
      runCommands(ROUTE, "North Africa", "Kamchatka");
      assertContains(
          ROUTE_INFO.getMessage(
              "[North Africa, Southern Europe, Ukraine, Ural, Siberia, Yakutsk, Kamchatka]"));
      assertContains(CONTINENT_INFO.getMessage("[Africa (0), Europe (11), Asia (33)]"));
    }

    @Test
    public void T2_13_fuel_info_japan_alberta() throws Exception {
      runCommands(ROUTE, "Japan", "Alberta");
      assertContains(ROUTE_INFO.getMessage("[Japan, Kamchatka, Alaska, Alberta]"));
      assertContains(FUEL_INFO.getMessage("7"));
    }

    @Test
    public void T2_14_fuel_info_zero_cost_greenland_iceland() throws Exception {
      runCommands(ROUTE, "Greenland", "Iceland");
      assertContains(ROUTE_INFO.getMessage("[Greenland, Iceland]"));
      assertContains(FUEL_INFO.getMessage("0"));
    }

    @Test
    public void T2_15_fuel_info_ural_venezuela() throws Exception {
      runCommands(ROUTE, "Ural", "Venezuela");
      assertContains(
          ROUTE_INFO.getMessage(
              "[Ural, Ukraine, Southern Europe, North Africa, Brazil, Venezuela]"));
      assertContains(
          CONTINENT_INFO.getMessage("[Asia (0), Europe (11), Africa (5), South America (2)]"));
      assertContains(FUEL_INFO.getMessage("18"));
    }

    @Test
    public void T2_16_fuel_continent_max_europe() throws Exception {
      runCommands(ROUTE, "Ural", "Brazil");
      assertContains(
          ROUTE_INFO.getMessage("[Ural, Ukraine, Southern Europe, North Africa, Brazil]"));
      assertContains(
          CONTINENT_INFO.getMessage("[Asia (0), Europe (11), Africa (5), South America (0)]"));
      assertContains(FUEL_INFO.getMessage("16"));
      assertContains(FUEL_CONTINENT_INFO.getMessage("Europe (11)"));
    }

    @Test
    public void T2_17_fuel_continent_max_north_america() throws Exception {
      runCommands(ROUTE, "Argentina", "Japan");
      assertContains(
          ROUTE_INFO.getMessage(
              "[Argentina, Peru, Venezuela, Central America, Western United States, Alberta,"
                  + " Alaska, Kamchatka, Japan]"));
      assertContains(
          CONTINENT_INFO.getMessage("[South America (7), North America (15), Asia (6)]"));
      assertContains(FUEL_INFO.getMessage("28"));
      assertContains(FUEL_CONTINENT_INFO.getMessage("North America (15)"));
    }

    @Test
    public void T2_18_fuel_continent_max_australia() throws Exception {
      runCommands(ROUTE, "Western Australia", "Siam");
      assertContains(ROUTE_INFO.getMessage("[Western Australia, Indonesia, Siam]"));
      assertContains(CONTINENT_INFO.getMessage("[Australia (3), Asia (0)]"));
      assertContains(FUEL_INFO.getMessage("3"));
      assertContains(FUEL_CONTINENT_INFO.getMessage("Australia (3)"));
    }
  }
}
