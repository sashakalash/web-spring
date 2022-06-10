import interfaces.Handler;
import interfaces.HandlerException;
import interfaces.HandlersMap;
import interfaces.Request;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    final static int THREADS_QUANTITY_VALUE = 64;
    final static ExecutorService pool = Executors.newFixedThreadPool(THREADS_QUANTITY_VALUE);
    public static Socket socket;
    public static final List<String> validPaths = List.of("/index.html");

    public Server(Socket socket) {
        this.socket = socket;
    }

    public static void startServer(int port) {
        try (var serverSocket = new ServerSocket(port)) {
            System.out.printf("Server started on %d port\n", port);
            while (true) {
                Server server = new Server(serverSocket.accept());
                pool.execute(server);
            }
        } catch (IOException e) {
            System.err.printf("Server error: %s", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addHandler(String method, String uri, Handler handler) throws HandlerException {
        HandlersMap.addHandler(new Request(method, uri), handler);
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
            while (true) {
                final var requestLine = in.readLine();
                if (requestLine == null) {
                    continue;
                }
                final var parts = requestLine.split(" ");
                if (parts.length != 3) {
                    continue;
                }
                for (String str : parts) {
                    System.out.println(str);
                }
                final var method = parts[0];
                final var path = parts[1];
                final var body = parts[2];
                final Request req;
                if (body != null) {
                    req = new Request(method, path, body);
                } else {
                    req = new Request(method, path);
                }
                if (!Server.validPaths.contains(req.getUri())) {
                    out.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.flush();
                    continue;
                }
                HandlersMap.getHandler(req).handle(req, out);
            }
        } catch (IOException | HandlerException e) {
            e.printStackTrace();
            System.err.printf("Server error: %s", e.getMessage());
        }
    }
}