import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EhcoServer {

    private final int port;

    public EhcoServer(int port) {
        this.port = port;
    }

    public static EhcoServer bindToPort(int port) {
        return new EhcoServer(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (true) {
                try (Socket clientSocket = server.accept()) {
                    handle(clientSocket);
                } catch (IOException e) {
                    System.out.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.printf("Порт %s занят. %n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException {
        var input = socket.getInputStream();
        var output = socket.getOutputStream();

        InputStreamReader isr = new InputStreamReader(input, "UTF-8");
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true);

        try (Scanner scanner = new Scanner(isr); writer) {
            while (true) {
                if (!scanner.hasNextLine()) {
                    break;
                }
                String message = scanner.nextLine().strip();
                System.out.printf("GOT %s%n", message);

                if ("bye".equalsIgnoreCase(message)) {
                    writer.println("bye bye");
                    System.out.println("bye bye");
                    return;
                }

                String reversed = new StringBuilder(message).reverse().toString();
                writer.println(reversed);
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Client dropped connection");
        }
    }
}