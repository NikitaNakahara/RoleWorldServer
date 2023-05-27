import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final int PORT = 3045;

    private ServerSocket serverSocket;
    private boolean stopServer = false;

    Server() {
        try {
            serverSocket = new ServerSocket(PORT);

            while (!stopServer) {
                newClient(serverSocket.accept());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void newClient(Socket socket) {
        System.out.println("Client connected");
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());

            while (!stopServer) {
                String inputMsg = input.readUTF();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
