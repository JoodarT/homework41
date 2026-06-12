import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class EhcoServer {

    private final int port;

    public EhcoServer(int port) {
        this.port = port;
    }

    public static EhcoServer bindToPort(int port){
        return new EhcoServer(port);
    }

    public void run(){
        try (ServerSocket server = new ServerSocket(port)){
            try (var clientSoket = server.accept()){

            }
        }catch (IOException e){
            System.out.printf("Порт %s занят. %n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException{
        var input = socket.getInputStream();

        InputStreamReader isr = new InputStreamReader(input, "UTF-8");
        try (Scanner scanner = new Scanner(isr)){
            while (true){
                String message = scanner.nextLine().strip();
                System.out.printf("GOT %s%n",message);
                if (message.toLowerCase().equals("bye")){
                    System.out.println("bye bye");
                    return;
                }
            }
        }
    }
}
