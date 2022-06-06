import server.Server;

public class Main {
    final static Server server = new Server();

    public static void main(String[] args) {
        server.startServer();
    }
}