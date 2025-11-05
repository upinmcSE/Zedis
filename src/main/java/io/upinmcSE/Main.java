package io.upinmcSE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class Main {
    private final static Logger log = Logger.getLogger(Main.class.getName());

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
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ){
            String clientMsg;
            while((clientMsg = in.readLine()) != null){
                log.info("Received from client: " + clientMsg);
                out.println("Server received: " + clientMsg);
                if(clientMsg.equals("bye")){
                    break;
                }
            }
            log.info("Client disconnected !");
        }catch (IOException e){
            log.warning(e.getMessage());
        }
    }
}