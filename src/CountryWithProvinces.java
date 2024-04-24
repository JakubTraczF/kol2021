import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// Klasa CountryWithProvinces dziedzicząca po klasie abstrakcyjnej Country
class CountryWithProvinces extends Country {
    // Prywatne pole przechowujące mapę prowincji, gdzie kluczem jest nazwa prowincji, a wartością obiekt klasy Country
    private final Map<String, Country> provinces;

    // Konstruktor klasy CountryWithProvinces
    public CountryWithProvinces(String name, Map<String, Country> provinces) {
        // Wywołanie konstruktora klasy nadrzędnej Country z podaną nazwą kraju
        super(name);
        // Inicjalizacja mapy prowincji
        this.provinces = provinces;
    }

    // Implementacja metody getConfirmedCases dla CountryWithProvinces
    @Override
    public int getConfirmedCases(LocalDate date) {
        // Inicjalizacja zmiennej sumującej potwierdzone przypadki
        int sum = 0;
        // Iteracja po wszystkich prowincjach
        for (Country province : provinces.values()) {
            // Dodawanie liczby potwierdzonych przypadków z danej prowincji do sumy
            sum += province.getConfirmedCases(date);
        }
        // Zwracanie sumy potwierdzonych przypadków dla wszystkich prowincji
        return sum;
    }

    // Implementacja metody getDeaths dla CountryWithProvinces
    @Override
    public int getDeaths(LocalDate date) {
        // Inicjalizacja zmiennej sumującej liczby zgonów
        int sum = 0;
        // Iteracja po wszystkich prowincjach
        for (Country province : provinces.values()) {
            // Dodawanie liczby zgonów z danej prowincji do sumy
            sum += province.getDeaths(date);
        }
        // Zwracanie sumy zgonów dla wszystkich prowincji
        return sum;
    }

    // Implementacja metody getAllDates dla CountryWithProvinces
    @Override
    public Iterable<LocalDate> getAllDates() {
        // Mapa przechowująca wszystkie unikalne daty, z wartościami typu Boolean jako flagami
        Map<LocalDate, Boolean> allDates = new HashMap<>();
        // Iteracja po wszystkich prowincjach
        for (Country province : provinces.values()) {
            // Iteracja po wszystkich datach w danej prowincji
            for (LocalDate date : province.getAllDates()) {
                // Dodawanie dat do mapy (flaga jest tutaj tylko jako placeholder, klucz jest używany do zapewnienia unikalności)
                allDates.put(date, true);
            }
        }
        // Zwracanie zbioru wszystkich dat
        return allDates.keySet();
    }
}
