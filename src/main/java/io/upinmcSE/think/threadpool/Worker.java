package io.upinmcSE.think.threadpool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

class Worker extends Thread {
    private final int id;
    private final BlockingQueue<Job> jobQueue;

    public Worker(int id, BlockingQueue<Job> jobQueue) {
        this.id = id;
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Job job = jobQueue.take(); // wait when haven't job
                Socket socket = job.getSocket();
                System.out.println(getName() + " handling connection from " + socket.getRemoteSocketAddress());
                handleConnection(socket);

            }
        } catch (InterruptedException ex) {
            System.out.println(getName() + " interrupted and stopping.");
        }
    }

    private void handleConnection(Socket socket) {
        try (socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {

            char[] buffer = new char[1000];
            in.read(buffer);

            Thread.sleep(10000);

            out.write("HTTP/1.1 200 OK\r\n\r\nBoThanhDzai\r\n");
            out.flush();

        } catch (Exception e) {
            System.err.println(getName() + " error: " + e.getMessage());
        }
    }
}
