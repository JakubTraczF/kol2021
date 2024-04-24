import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

abstract class Country {
    // Klasa wyjątku informująca o nieznalezieniu kraju
    static class CountryNotFoundException extends Exception {
        public CountryNotFoundException(String countryName) {
            super("Country not found: " + countryName);
        }
    }

    // Klasa reprezentująca kolumny w pliku CSV
    private static class CountryColumns {
        public final int firstColumnIndex; // Indeks pierwszej kolumny
        public final int columnCount; // Liczba kolumn

        public CountryColumns(int firstColumnIndex, int columnCount) {
            this.firstColumnIndex = firstColumnIndex;
            this.columnCount = columnCount;
        }
    }

    // Ścieżki do plików CSV
    private static String confirmedCasesCsvFile;
    private static String deathsCsvFile;

    // Nazwa kraju
    private final String name;

    // Konstruktor ustawiający nazwę kraju
    public Country(String name) {
        this.name = name;
    }

    // Metoda zwracająca nazwę kraju
    public String getName() {
        return name;
    }

    // Metoda ustawiająca ścieżki do plików CSV
    public static void setFiles(String confirmedCasesCsvFile, String deathsCsvFile) throws FileNotFoundException {
        // Sprawdzamy czy pliki istnieją i są czytelne
        if (!Files.isReadable(Path.of(confirmedCasesCsvFile)) || !Files.isReadable(Path.of(deathsCsvFile))) {
            throw new FileNotFoundException("One or both CSV files not found");
        }
        // Ustawiamy ścieżki do plików
        Country.confirmedCasesCsvFile = confirmedCasesCsvFile;
        Country.deathsCsvFile = deathsCsvFile;
    }

    // Metoda pomocnicza do ustalenia indeksu kolumny dla danego kraju
    private static CountryColumns getCountryColumns(String firstLine, String countryName) throws CountryNotFoundException {
        String[] columns = firstLine.split(",");
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equalsIgnoreCase(countryName)) {
                return new CountryColumns(i, 2); // Zwracamy indeks pierwszej kolumny i liczbę kolumn
            }
        }
        throw new CountryNotFoundException(countryName); // Rzucamy wyjątek gdy nie znaleziono kraju
    }

    // Metoda zapisująca dane do pliku
    public void saveToDataFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Nagłówek pliku
            writer.write("Date\tConfirmed Cases\tDeaths\n");

            // Iterujemy po wszystkich datach i zapisujemy odpowiadające im statystyki
            for (LocalDate date : getAllDates()) {
                writer.write(date.toString() + "\t" + getConfirmedCases(date) + "\t" + getDeaths(date) + "\n");
            }
        }
    }

    // Metoda abstrakcyjna do pobierania wszystkich dat, dla których są dostępne dane
    public abstract Iterable<LocalDate> getAllDates();

    // Metoda abstrakcyjna do pobierania liczby zdiagnozowanych przypadków dla określonej daty
    public abstract int getConfirmedCases(LocalDate date);

    // Metoda abstrakcyjna do pobierania liczby zgonów dla określonej daty
    public abstract int getDeaths(LocalDate date);

    // Metoda do sortowania listy krajów według liczby zgonów w danym okresie
    public static void sortByDeaths(List<Country> countries, LocalDate startDate, LocalDate endDate) {
        countries.sort(Comparator.comparingInt(country -> -country.getTotalDeaths(startDate, endDate)));
    }

    // Metoda pomocnicza do obliczania sumy zgonów dla danego okresu
    private int getTotalDeaths(LocalDate startDate, LocalDate endDate) {
        int totalDeaths = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            totalDeaths += getDeaths(date);
        }
        return totalDeaths;
    }

    // Metoda wczytująca dane z plików CSV i tworząca obiekt klasy Country na ich podstawie
    public static Country fromCsv(String countryName) throws IOException, CountryNotFoundException {
        try (BufferedReader confirmedReader = new BufferedReader(new FileReader(confirmedCasesCsvFile));
             BufferedReader deathsReader = new BufferedReader(new FileReader(deathsCsvFile))) {

            // Wczytujemy pierwsze linie, aby określić indeksy kolumn dla danego kraju
            String confirmedFirstLine = confirmedReader.readLine();
            String deathsFirstLine = deathsReader.readLine();

            CountryColumns confirmedColumns = getCountryColumns(confirmedFirstLine, countryName);
            CountryColumns deathsColumns = getCountryColumns(deathsFirstLine, countryName);

            // Pomijamy nagłówki w plikach CSV
            confirmedReader.readLine();
            deathsReader.readLine();

            // Tworzymy mapę na dane (data -> [przypadki, zgony])
            Map<LocalDate, int[]> data = new HashMap<>();
            String confirmedLine;
            String deathsLine;
            while ((confirmedLine = confirmedReader.readLine()) != null && (deathsLine = deathsReader.readLine()) != null) {
                String[] confirmedData = confirmedLine.split(",");
                String[] deathsData = deathsLine.split(",");

                LocalDate date = LocalDate.parse(confirmedData[0]);
                int confirmedCases = Integer.parseInt(confirmedData[confirmedColumns.firstColumnIndex]);
                int deaths = Integer.parseInt(deathsData[deathsColumns.firstColumnIndex]);

                data.put(date, new int[]{confirmedCases, deaths});
            }

            // Sprawdzamy, czy kraj ma prowincje
            if (confirmedColumns.columnCount > 2 || deathsColumns.columnCount > 2) {
                // Jeśli tak, tworzymy obiekty klasy Country dla prowincji na podstawie danych
                Map<String, Country> provinces = new HashMap<>();
                // Tutaj należy dodać kod tworzący obiekty Country dla prowincji
                // Dla uproszczenia zakładam, że tablica provincesNames zawiera nazwy prowincji
                String[] provincesNames = {"Province1", "Province2"}; // Tutaj podaj rzeczywiste nazwy prowincji
                for (String provinceName : provincesNames) {
                    provinces.put(provinceName, new CountryWithoutProvinces(provinceName, data));
                }
                return new CountryWithProvinces(countryName, provinces);
            } else {
                // Jeśli nie, tworzymy obiekt klasy Country bez prowincji
                return new CountryWithoutProvinces(countryName, data);
            }
        }
    }

    // Metoda wczytująca dane dla wielu krajów jednocześnie
    public static Country[] fromCsv(String[] countryNames) {
        List<Country> countries = new ArrayList<>();

        for (String countryName : countryNames) {
            try {
                countries.add(fromCsv(countryName));
            } catch (IOException | CountryNotFoundException e) {
                System.out.println("Country not found: " + e.getMessage());
            }
        }

        return countries.toArray(new Country[0]);
    }
}