package io.upinmcSE.server;

import io.upinmcSE.constant.Constants;
import io.upinmcSE.constant.Operation;
import io.upinmcSE.core.io_multiplexing.Event;
import io.upinmcSE.core.io_multiplexing.IOMultiplexer;
import io.upinmcSE.core.io_multiplexing.IOMultiplexerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

public class Zedis {
    private final static Logger log = Logger.getLogger(Zedis.class.getName());

    public static void main(String[] args) {
        int port = 3000;
        try(
                IOMultiplexer multiplexer = IOMultiplexerFactory.create(Constants.maxConnection); // 1: init epoll
                ServerSocketChannel serverChannel = ServerSocketChannel.open() // 2: open server (non-blocking)
        )
        {
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            log.info("Zedis Server started on port " + port);

            multiplexer.monitor(new Event(serverChannel, Operation.ACCEPT)); // 3: register event accept connect

            while (true){
                List<Event> events = multiplexer.waitEvents();
                for (Event event : events) {
                    log.info("Operation: " + event.getOp());
                    if(event.getOp() == Operation.ACCEPT){
                        handleAccept(event, multiplexer);
                    }else if(event.getOp() == Operation.READ){
                        readCommand(event);
                    }
                }
            }
        }catch (IOException e){
            log.warning(e.getMessage());
        }
    }

    private static void handleAccept(Event event, IOMultiplexer multiplexer) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) event.getChannel();
        SocketChannel clientChannel = serverChannel.accept();

        if (clientChannel != null) {
            log.info("New connection: " + clientChannel.getRemoteAddress());

            // Set Non-blocking cho client
            clientChannel.configureBlocking(false);

            // Enable TCP_NODELAY: the same flush()
            clientChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);

            // register client connect
            multiplexer.monitor(new Event(clientChannel, Operation.READ));
        }
    }

    private static void readCommand(Event event) throws IOException{
        SocketChannel clientChannel = (SocketChannel) event.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        int n = clientChannel.read(buffer);

        if (n == -1) {
            log.info("Client closed connection");
            clientChannel.close();
            return;
        }

        if (n > 0) {
            // change mode buffer from Write to Read
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);

            String cmd = new String(data, 0, n, StandardCharsets.UTF_8);

            log.info("Command: " + cmd);

            // call method  and response
            writeResponse(cmd, clientChannel);
        }
    }

    private static void writeResponse(String cmd, SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(cmd.getBytes(StandardCharsets.UTF_8));
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}
