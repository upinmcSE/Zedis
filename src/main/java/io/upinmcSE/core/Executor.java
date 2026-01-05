package io.upinmcSE.core;

import io.upinmcSE.core.io_multiplexing.Event;

import java.util.List;

public interface Executor {
    byte[] cmdPing(String[] args);
    byte[] cmdSet(String[] args);
    byte[] cmdGet(String[] args);
    byte[] cmdTtl(String[] args);
    byte[] cmdExpire(String[] args);
    byte[] cmdDel(String[] args);
    byte[] cmdExists(String[] args);
    void executeAndResponse(Command cmd, Event event);
}
