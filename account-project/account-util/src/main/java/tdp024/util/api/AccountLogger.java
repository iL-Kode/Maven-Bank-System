package tdp024.util.api;

public interface AccountLogger {
    
    enum TodoLoggerLevel {
        DEBUG,
        INFO,
        NOTIFY,
        WARNING,
        ERROR,
        CRITICAL,
        ALERT,
        EMERGENCY
    }
    
    public void log(Throwable throwable);
    
    public void log(TodoLoggerLevel todoLoggerLevel, String shortMessage, String longMessage);
    
}
