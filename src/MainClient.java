public class MainClient {
    public static void main(String[]args){


        ClientServer.connectTo(8788).run();
    }
}
