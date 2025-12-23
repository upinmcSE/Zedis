package io.upinmcSE.think;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class TcpServer {
    private final static Logger log = Logger.getLogger(TcpServer.class.getName());

    public static void main(String[] args) {
        int port = 8080;

        try(ServerSocket serverSocket = new ServerSocket(port)){
            log.info("Listening on port " + port);
            while(true){
                Socket clientSocket = serverSocket.accept();
                log.info("New connection from " + clientSocket.getRemoteSocketAddress());

                new Thread(() -> handleConnection(clientSocket)).start();
            }
        }catch(IOException e){
            log.warning(e.getMessage());
        }

    }

    public static void handleConnection(Socket socket){
       log.info("handleConnection ..." + socket.getRemoteSocketAddress());
       try{
           while (true){
               String cmd = readCommand(socket);
               writeResponse(cmd, socket);
           }
       }catch(IOException e){
           log.warning(e.getMessage());
       }

    }

    private static String readCommand(Socket socket) throws IOException {
        byte[] buf = new byte[1024];

        InputStream inputStream = socket.getInputStream();

        int n = inputStream.read(buf);

        if (n == -1) {
            throw new IOException("Connection closed by client");
        }
        return new String(buf, 0, n, StandardCharsets.UTF_8);
    }

    private static void writeResponse(String cmd, Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        outputStream.write(cmd.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
