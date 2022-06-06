package server;

import client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    final int PORT = 9999;
    final int THREADS_QUANTITY_VALUE = 64;
    public static final List<String> validPaths = List.of("/index.html");
    final ExecutorService pool = Executors.newFixedThreadPool(THREADS_QUANTITY_VALUE);

    public void startServer() {
        try (final var serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                addConnection(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addConnection(Socket socket) throws IOException {
        var clientThread = new ClientHandler(socket);
        pool.execute(clientThread);
    }
}