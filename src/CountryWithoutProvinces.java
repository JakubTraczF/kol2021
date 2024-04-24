import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

// Klasa CountryWithoutProvinces dziedzicząca po klasie abstrakcyjnej Country
class CountryWithoutProvinces extends Country {
    // Prywatne pole przechowujące dane dotyczące potwierdzonych przypadków i zgonów dla danego kraju
    private final Map<LocalDate, int[]> data;

    // Konstruktor klasy CountryWithoutProvinces
    public CountryWithoutProvinces(String name, Map<LocalDate, int[]> data) {
        // Wywołanie konstruktora klasy nadrzędnej Country z podaną nazwą kraju
        super(name);
        // Inicjalizacja mapy przechowującej dane dotyczące potwierdzonych przypadków i zgonów
        this.data = data;
    }

    // Implementacja metody getConfirmedCases dla CountryWithoutProvinces
    @Override
    public int getConfirmedCases(LocalDate date) {
        // Pobranie statystyk dla danej daty z mapy danych
        int[] stats = data.get(date);
        // Jeśli statystyki dla danej daty istnieją, zwróć liczbę potwierdzonych przypadków, w przeciwnym razie zwróć 0
        return stats != null ? stats[0] : 0;
    }

    // Implementacja metody getDeaths dla CountryWithoutProvinces
    @Override
    public int getDeaths(LocalDate date) {
        // Pobranie statystyk dla danej daty z mapy danych
        int[] stats = data.get(date);
        // Jeśli statystyki dla danej daty istnieją, zwróć liczbę zgonów, w przeciwnym razie zwróć 0
        return stats != null ? stats[1] : 0;
    }

    // Implementacja metody getAllDates dla CountryWithoutProvinces
    @Override
    public Iterable<LocalDate> getAllDates() {
        // Zwróć zbiór zawierający wszystkie daty dostępne w mapie danych
        return data.keySet();
    }
}