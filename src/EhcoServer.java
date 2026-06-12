import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;

public class EhcoServer {

    private final int port;
    private final Map<String, Function<String, String>> commands = new HashMap<>();

    public EhcoServer(int port) {
        this.port = port;
        initCommands();
    }

    private void initCommands() {
        commands.put("date", msg -> LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        commands.put("time", msg -> LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        commands.put("reverse", msg -> new StringBuilder(msg.substring(7).strip()).reverse().toString());
        commands.put("upper", msg -> msg.substring(5).strip().toUpperCase());
        commands.put("bye", msg -> "bye bye");
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

                String commandKey = commands.keySet().stream()
                        .filter(key -> message.startsWith(key))
                        .findFirst()
                        .orElse("");

                Function<String, String> action = commands.getOrDefault(commandKey, msg -> msg);
                String response = action.apply(message);

                writer.println(response);

                if ("bye".equals(commandKey)) {
                    System.out.println("bye bye");
                    return;
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Client dropped connection");
        }
    }
}