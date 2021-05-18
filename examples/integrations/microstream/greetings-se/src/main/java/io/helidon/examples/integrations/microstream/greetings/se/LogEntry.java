package io.helidon.examples.integrations.microstream.greetings.se;

import java.time.LocalDateTime;

public class LogEntry {
    private String name;
    private LocalDateTime dateTime;

    public LogEntry(String name, LocalDateTime dateTime) {
        super();
        this.name = name;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

}
