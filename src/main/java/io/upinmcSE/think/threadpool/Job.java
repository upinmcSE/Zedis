package io.upinmcSE.think.threadpool;

import java.net.Socket;

class Job {
    private final Socket socket;

    public Job(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return this.socket;
    }
}
