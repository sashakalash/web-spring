package interfaces;
import java.io.BufferedOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlersMap {
    private static final Map<String, Map<String, Handler>> handlers = new ConcurrentHashMap<>();

    public static Handler getHandler(Request req) throws HandlerException {
        if (handlers.containsKey(req.getMethod())) {
            return handlers.get(req.getMethod()).getOrDefault(req.getUri(), null);
        } else {
            return null;
        }
    }

    public static void addHandler(Request req, Handler hand) throws HandlerException {
        if (handlers.containsKey(req.getMethod())) {
            if (!handlers.get(req.getMethod()).containsKey(req.getUri())) {
                handlers.get(req.getMethod()).put(req.getUri(), hand);
            } else {
                throw new HandlerException("Handler is already exist for this URI!");
            }
        } else {
            final var map = new ConcurrentHashMap<String, Handler>();
            map.put(req.getUri(), hand);
            handlers.put(req.getMethod(), map);
        }
    }
}