import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    final String WEB_ROOT = ".";
    static final int PORT = 9999;
    final String WEB_ROOT_DIR = "public";
    final static int THREADS_QUANTITY_VALUE = 64;
    public static final List<String> validPaths = List.of(
            "/index.html",
            "/events.html",
            "/forms.html"
    );
    final static ExecutorService pool = Executors.newFixedThreadPool(THREADS_QUANTITY_VALUE);
    public Socket serverSocket;

    public Server(Socket socket) {
        this.serverSocket = socket;
    }

    public static void startServer() {
        try (var serverSocket = new ServerSocket(PORT)) {
            System.out.printf("Server started on %d port\n", PORT);
            startConnection(serverSocket);
        } catch (IOException e) {
            System.err.printf("Server error: %s", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void startConnection(ServerSocket serverSocket) throws IOException {
        while (true) {
            Server server = new Server(serverSocket.accept());
            pool.execute(server);
        }
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
             final var out = new BufferedOutputStream(serverSocket.getOutputStream())) {
                // read only request line for simplicity
                // must be in form GET /path HTTP/1.1
                final var requestLine = in.readLine();
                if (requestLine == null) {
                    return;
                }
                final var parts = requestLine.split(" ");

                if (parts.length != 3) {
                    // just close socket
                    return;
                }

                final var path = parts[1];
                if (!Server.validPaths.contains(path)) {
                    out.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.flush();
                    return;
                }

                final var filePath = Path.of(WEB_ROOT, WEB_ROOT_DIR, path);
                final var mimeType = Files.probeContentType(filePath);

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.printf("Server error: %s", e.getMessage());
        }
    }
}