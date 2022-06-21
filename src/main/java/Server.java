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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    final static int THREADS_QUANTITY_VALUE = 64;
    final static ExecutorService pool = Executors.newFixedThreadPool(THREADS_QUANTITY_VALUE);
    public static final String GET = "GET";
    public static final String POST = "POST";
    final static List<String> allowedMethods = List.of(GET, POST);
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
                    notFoundReq(out);
                    continue;
                }
                final var parts = requestLine.split(" ");
                if (parts.length != 3) {
                    notFoundReq(out);
                    continue;
                }
                final var method = parts[0];
                if (!allowedMethods.contains(method)) {
                    invalidMethofReq(out);
                    continue;
                }
                final var path = parts[1];
                if (!Server.validPaths.contains(path)) {
                    notFoundReq(out);
                    continue;
                }
                final var body = requestLine.substring(requestLine.indexOf("\\r\\n\\r\\n"));
                final Request req;
                if (!body.equals("")) {
                    req = new Request(method, path, body);
                } else {
                    req = new Request(method, path);
                }
                HandlersMap.getHandler(req).handle(req, out);
            }
        } catch (IOException | HandlerException e) {
            e.printStackTrace();
            System.err.printf("Server error: %s", e.getMessage());

        }
    }

    public static void invalidMethofReq(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    public static void notFoundReq(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 404 Not Found\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }
}