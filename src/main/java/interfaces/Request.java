package interfaces;

import org.apache.http.NameValuePair;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private String method;
    private String uri;
    private String body;
    private Map<String, String> params = new HashMap<>();

    public Map<String, String> getQueryParams() {
        return params;
    }

    public String getQueryParam(String name) {
        return params.get(name);
    }

    public void setParams(List<NameValuePair> params) {
        params.stream().forEach(param -> this.params.put(param.getName(), param.getValue()));
    }

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

    public static String getUrlWithoutParameters(URI uri) throws URISyntaxException {
        return new URI(uri.getScheme(),
                uri.getAuthority(),
                uri.getPath(),
                null,
                uri.getFragment()).toString();
    }
}