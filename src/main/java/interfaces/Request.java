package interfaces;
import java.util.List;

public class Request {
    private String method;
    private String uri;

    public Request(String method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public static Request parse(String[] parts) {
        String method = parts[0];
        String uri = parts[1];
        return new Request(method, uri);
    }
}