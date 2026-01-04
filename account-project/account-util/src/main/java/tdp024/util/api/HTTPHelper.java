package tdp024.util.api;

public interface HTTPHelper {

    public String get(String endpoint, String... parameters);
    public String postJSON(String endpoint, String[] queryParameters, String[] dataParameters);
}
