import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientServer {
    private final int port;
    private final String host;

    public ClientServer(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public static ClientServer connectTo(int port) {
        String localhost = "127.0.0.1";
        return new ClientServer(port, localhost);
    }

    public void run() {
        System.out.printf("напиши 'bye' чтобы выйти%n%n%n");

        try (var socket = new Socket(host, port)) {
            var consoleScanner = new Scanner(System.in, "UTF-8");
            var serverScanner = new Scanner(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            var output = socket.getOutputStream();
            var writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true);

            try (consoleScanner; serverScanner; writer) {
                while (true) {
                    String message = consoleScanner.nextLine();
                    writer.println(message);

                    if (serverScanner.hasNextLine()) {
                        String response = serverScanner.nextLine();
                        System.out.printf("SERVER RESPONSE: %s%n", response);
                    }

                    if ("bye".equals(message.toLowerCase())) {
                        return;
                    }
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.printf("Connection dropped!%n");
        } catch (IOException e) {
            var msg = "Can't connect to %s:%s!%n";
            System.out.printf(msg, host, port);
            e.printStackTrace();
        }
    }
}