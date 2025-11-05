package io.upinmcSE.think.threadpool;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class ThreadPool {
    private final BlockingQueue<Job> jobQueue;
    private final List<Worker> workers;

    public ThreadPool(int numberOfWorkers) {
        this.jobQueue = new LinkedBlockingQueue<>();
        this.workers = new LinkedList<>();
        for (int i = 0; i < numberOfWorkers; i++) {
            Worker worker = new Worker(i, jobQueue);
            workers.add(worker);
        }
    }

    public void start() {
        for (Worker worker : workers) {
            worker.start();
        }
    }

    public void addJob(Socket socket) {
        jobQueue.offer(new Job(socket));
    }

    public void shutdown() {
        for (Worker worker : workers) {
            worker.interrupt();
        }
    }
}
