package interfaces;

public class Request {
    private String method;
    private String uri;
    private String body;

    public Request(String method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public String getBody() {
        return body;
    }


    public Request(String method, String uri, String body) {
        this.method = method;
        this.uri = uri;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }
}