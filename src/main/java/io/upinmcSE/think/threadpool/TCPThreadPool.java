package io.upinmcSE.think.threadpool;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class TCPThreadPool {
    private static final Logger log = Logger.getLogger(TCPThreadPool.class.getName());

    public static void main(String[] args) {
        int port = 8080;

        try(ServerSocket serverSocket = new ServerSocket(port)){
            log.info("Listening on port " + port);

            // create pool
            ThreadPool pool = new ThreadPool(2);
            pool.start();

            while(true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connection from " + clientSocket.getRemoteSocketAddress());
                pool.addJob(clientSocket);
            }
        }catch (IOException e){
            log.warning(e.getMessage());
        }

    }
}

