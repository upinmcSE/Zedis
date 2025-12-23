package io.upinmcSE.core.io_multiplexing;

import java.nio.channels.SelectableChannel;

public class Event {
    private SelectableChannel channel;
    private int op;

    public Event(SelectableChannel channel, int op) {
        this.channel = channel;
        this.op = op;
    }

    public SelectableChannel getChannel() {
        return channel;
    }

    public void setChannel(SelectableChannel channel) {
        this.channel = channel;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }
}
