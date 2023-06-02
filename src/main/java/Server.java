import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Server {
    private final int PORT = 3045;

    private ServerSocket serverSocket;
    private boolean stopServer = false;

    ArrayList<String> IDs = new ArrayList<>();

    Server() {
        Database.create();

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Start server");

            while (!stopServer) {
                newClient(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newClient(Socket socket) {
        System.out.println("Client connected");
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            while (!stopServer) {
                JSONObject inputJson = new JSONObject(input.readUTF());
                System.out.println("GET: " + inputJson.toString());

                switch ((String) inputJson.get("type")) {
                    case "auth":
                        ArrayList<String> userData = authUser(inputJson);
                        JSONObject json = new JSONObject();
                        if (userData == null) {
                            json.put("state", "failed");
                        } else {
                            json.put("state", "success");
                            json.put("id", userData.get(0));
                            if (inputJson.getString("mode").equals("sign_in")) {
                                json.put("nickname", userData.get(1));
                            }
                        }

                        System.out.println("POST: " + json.toString());
                        output.writeUTF(json.toString());
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> authUser(JSONObject data) {
        if (Objects.equals(data.getString("mode"), "sign_in")) {
            User user = Database.getUser(data.getString("email"), Database.EMAIL);
            if (user != null) {
                if (Objects.equals(user.getPassword(), String.valueOf(data.getString("password").hashCode()))) {
                    ArrayList<String> arr = new ArrayList<>();
                    arr.add(user.getId());
                    arr.add(user.getNickname());
                    return arr;
                }
            }
        } else {
            if (Database.getUser(data.getString("email"), Database.EMAIL) == null) {
                String id = generateUniqueID();
                User user = new User(id, (String) data.get("nickname"), data.getString("email"), String.valueOf(data.get("password").hashCode()));
                IDs.add(id);

                Database.addUser(user);

                ArrayList<String> arr = new ArrayList<>();
                arr.add(user.getId());
                return arr;
            }
        }

        return null;
    }

    private String generateUniqueID() {
        Random random = new Random();
        StringBuilder result;
        do {
            result = new StringBuilder();

            for (int i = 0; i < 10; i++) {
                int num = random.nextInt() % 10;
                result.append(Math.abs(num));
            }

        } while (checkUserID(result.toString()));

        return result.toString();
    }

    private boolean checkUserID(String id) {
        for (String s : IDs) {
            if (Objects.equals(s, id)) return true;
        }

        return false;
    }
}
