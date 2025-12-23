package io.upinmcSE.core.io_multiplexing;

import io.upinmcSE.core.io_multiplexing.impl.NioSelectorMultiplexer;

import java.io.IOException;

public class IOMultiplexerFactory {
    public static IOMultiplexer create(int maxConnection) throws IOException {
        return new NioSelectorMultiplexer(maxConnection);
    }
}
