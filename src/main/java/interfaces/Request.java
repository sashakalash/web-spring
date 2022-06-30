package interfaces;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String uri;
    private final Map<String, String> params = new HashMap<>();

    public Map<String, String> getQueryParams() {
        return params;
    }

    public String getQueryParam(String name) {
        return params.get(name);
    }

    public void setParams(List<NameValuePair> params) {
        params.forEach(param -> this.params.put(param.getName(), param.getValue()));
    }

    public Request(String method, String path) throws URISyntaxException {
        this.method = method;
        final var uri = new URI(path);
        this.uri = Request.getUrlWithoutParameters(uri);
        final var params = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
        setParams(params);
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