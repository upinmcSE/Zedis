package io.upinmcSE.think;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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
        try(
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ){
            char[] buffer = new char[1024];
            in.read(buffer);

            out.write("HTTP/1.1 200 OK\r\n\r\nBoThanhDzai|r\n");
            out.flush();
            log.info("Client disconnected !");
        }catch (IOException e){
            log.warning(e.getMessage());
        }
    }
}
