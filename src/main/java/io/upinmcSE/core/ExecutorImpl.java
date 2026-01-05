package io.upinmcSE.core;

import io.upinmcSE.core.io_multiplexing.Event;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ExecutorImpl implements Executor{
    @Override
    public byte[] cmdPing(String[] args) {
        return new byte[0];
    }

    @Override
    public byte[] cmdSet(String[] args) {
        return new byte[0];
    }

    @Override
    public byte[] cmdGet(String[] args) {
        return new byte[0];
    }

    @Override
    public byte[] cmdTtl(String[] args) {
        return new byte[0];
    }

    @Override
    public byte[] cmdExpire(String[] args) {
        return new byte[0];
    }

    @Override
    public byte[] cmdDel(String[] args) {
        return new byte[0];
    }

    @Override
    public byte[] cmdExists(String[] args) {
        return new byte[0];
    }

    @Override
    public void executeAndResponse(Command cmd, Event event) {
        byte[] res;
        switch (cmd.getCmd()) {
            case "PING" -> res = cmdPing(cmd.getArgs());
            case "SET" -> res = cmdSet(cmd.getArgs());
            case "GET" -> res = cmdGet(cmd.getArgs());
            case "TTL" -> res = cmdTtl(cmd.getArgs());
            case "EXPIRE" -> res = cmdExpire(cmd.getArgs());
            case "DEL" -> res = cmdDel(cmd.getArgs());
            case "EXISTS" -> res = cmdExists(cmd.getArgs());
            default -> res = "-CMD NOT FOUND\\r\\n".getBytes(StandardCharsets.UTF_8);
        }
    }
}
