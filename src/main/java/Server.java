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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class Server implements Runnable {
    final static int THREADS_QUANTITY_VALUE = 64;
    final static ExecutorService pool = Executors.newFixedThreadPool(THREADS_QUANTITY_VALUE);
    public static Socket socket;
    public static final List<String> validPaths = List.of("/index.html");
    final static HttpClient httpClient = HttpClient.newHttpClient();


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
                final var method = parts[0];
                final var path = parts[1];
                final var uri = new URI(path);
                final var clearedUri = Request.getUrlWithoutParameters(uri);
                final var params = URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
                final var req = new Request(method, clearedUri);
                req.setParams(params);
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
        } catch (IOException | URISyntaxException | HandlerException e) {
            e.printStackTrace();
            System.err.printf("Server error: %s", e.getMessage());
        }
    }
}