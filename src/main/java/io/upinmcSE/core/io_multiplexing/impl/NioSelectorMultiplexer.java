package io.upinmcSE.core.io_multiplexing.impl;

import io.upinmcSE.constant.Operation;
import io.upinmcSE.core.io_multiplexing.Event;
import io.upinmcSE.core.io_multiplexing.IOMultiplexer;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.logging.Logger;

public class NioSelectorMultiplexer implements IOMultiplexer {
    private static final Logger log = Logger.getLogger(NioSelectorMultiplexer.class.getName());

    private final Selector selector;
    private final int maxConnection;

    public NioSelectorMultiplexer(int maxConnection) throws IOException {
        this.selector = Selector.open();
        this.maxConnection = maxConnection;
    }

    @Override
    public void monitor(Event event) throws IOException {
        event.getChannel().configureBlocking(false);

        // map operation to selectorKey
        int interestOps = 0;
        if(event.getOp() == Operation.ACCEPT){
            interestOps = SelectionKey.OP_ACCEPT;
        }else if(event.getOp() == Operation.READ){
            interestOps = SelectionKey.OP_READ;
        }

        event.getChannel().register(selector, interestOps);

    }

    @Override
    public List<Event> waitEvents() throws IOException {
        int selected = this.selector.select();
        log.info("selected: " + selected);
        if(selected == 0) return Collections.emptyList();

        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        List<Event> genericEvents = new ArrayList<>(selectedKeys.size());

        Iterator<SelectionKey> it = selectedKeys.iterator();
        while (it.hasNext()) {
            SelectionKey key = it.next();
            SelectableChannel channel = key.channel();

            int op = 0;
            if (key.isValid() && key.isAcceptable()) op = Operation.ACCEPT;
            else if (key.isValid() && key.isReadable()) op = Operation.READ;

            genericEvents.add(new Event(channel, op));
            it.remove();
        }

        return genericEvents;
    }

    @Override
    public void close() throws IOException {
        selector.close();
    }

    public Selector getSelector() {
        return selector;
    }

    public int getMaxConnection() {
        return maxConnection;
    }
}
