package tdp024.util.api;

public interface AccountJsonSerializer {

    public <T> T fromJson(String json, Class<T> clazz);
    
    public String toJson(Object object);
}
