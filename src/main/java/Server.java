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
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    final static int THREADS_QUANTITY_VALUE = 64;
    final static ExecutorService pool = Executors.newFixedThreadPool(THREADS_QUANTITY_VALUE);
    final Socket socket;

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

    public static void addHandler(String method, String uri, Handler handler) throws HandlerException, URISyntaxException {
        HandlersMap.addHandler(new Request(method, uri), handler);
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {
            final var requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }
            final var parts = requestLine.split(" ");
            if (parts.length != 3) {
                return;
            }
            final var method = parts[0];
            final var path = parts[1];
            final var req = new Request(method, path);
            final var handler = HandlersMap.getHandler(req);
            if (handler == null) {
                notFound(out);
                return;
            }
            handler.handle(req, out);
        } catch (IOException | URISyntaxException | HandlerException e) {
            e.printStackTrace();
            System.err.printf("Server error: %s", e.getMessage());
        }
    }

    private void notFound(BufferedOutputStream out) throws IOException {
        out.write((
                """
                        HTTP/1.1 404 Not Found\r
                        Content-Length: 0\r
                        Connection: close\r
                        \r
                        """
        ).getBytes());
        out.flush();
    }
}