package io.upinmcSE.core.io_multiplexing;

import java.io.IOException;
import java.util.List;

public interface IOMultiplexer extends AutoCloseable {
    void monitor(Event event) throws IOException;
    List<Event> waitEvents() throws IOException;
    void close() throws IOException;
}
