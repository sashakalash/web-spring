import interfaces.HandlerException;
import interfaces.Request;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    static final int PORT = 9999;
    final static String WEB_ROOT_DIR = "public";
    final static String WEB_ROOT = ".";

    public static void main(String[] args) throws HandlerException, URISyntaxException {
        Server.addHandler("GET", "/index", (Request request, BufferedOutputStream responseStream) -> {
                    try {
                        final var filePath = Path.of(WEB_ROOT, WEB_ROOT_DIR, request.getUri() + ".html");
                        final var mimeType = Files.probeContentType(filePath);
                        final var length = Files.size(filePath);
                        responseStream.write((
                                "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: " + mimeType + "\r\n" +
                                        "Content-Length: " + length + "\r\n" +
                                        "Connection: close\r\n" +
                                        "\r\n"
                        ).getBytes());
                        Files.copy(filePath, responseStream);
                        responseStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        );
        Server.addHandler("POST", "/index", (Request request, BufferedOutputStream responseStream) -> {
                    try {
                        final var filePath = Path.of(WEB_ROOT, WEB_ROOT_DIR, request.getUri());
                        final var mimeType = Files.probeContentType(filePath);
                        final var length = Files.size(filePath);
                        responseStream.write((
                                "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: " + mimeType + "\r\n" +
                                        "Content-Length: " + length + "\r\n" +
                                        "Connection: close\r\n" +
                                        "\r\n"
                        ).getBytes());
                        Files.copy(filePath, responseStream);
                        responseStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        );
        Server.startServer(PORT);
    }
}