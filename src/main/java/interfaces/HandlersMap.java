package interfaces;
import java.util.HashMap;
import java.util.Map;

public class HandlersMap {
    private static final Map<String, Map<String, Handler>> handlers = new HashMap<>();

    public static Handler getHandler(Request req) throws HandlerException {
        if (handlers.containsKey(req.getMethod())) {
            if (handlers.get(req.getMethod()).containsKey(req.getUri())) {
                return handlers.get(req.getMethod()).get(req.getUri());
            } else {
                throw new HandlerException("Incorrect URI! No handler for this uri:" + " " + req.getUri() + "!");
            }
        } else {
            throw new HandlerException("Incorrect method! No handler for this request:" + " " + req.getMethod() + "!");
        }
    }

    public static void addHandler(Request req, Handler hand) throws HandlerException {
        final var path = req.getUri() + ".html";
        if (handlers.containsKey(req.getMethod())) {
            if (!handlers.get(req.getMethod()).containsKey(path)) {
                handlers.get(req.getMethod()).put(path, hand);
            } else {
                throw new HandlerException("Handler is already exist for this URI!");
            }
        } else {
            final var map = new HashMap<String, Handler>();
            map.put(path, hand);
            handlers.put(req.getMethod(), map);
        }
    }
}