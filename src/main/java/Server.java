import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private final int PORT = 4035;

    private ServerSocket serverSocket;
    private boolean stopServer = false;

    private static final ArrayList<String> IDs = new ArrayList<>();

    Server() {
        Database.create();

        try {
            serverSocket = new ServerSocket(PORT);
            Log.add(Log.DEBUG, "System", "Start server");

            while (!stopServer) {
                newClient(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newClient(Socket socket) {
        Log.add(Log.DEBUG, "System", "Client connected");
        try {
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            boolean isAuth = false;

            while (!stopServer) {
                String inputStr = input.readUTF();
                Log.add(Log.DEBUG, "GET", inputStr);

                Message inputMsg = new Message(inputStr);

                if (Objects.equals(inputMsg.getRequestType(), "sys")) {
                    if (Objects.equals(inputMsg.getRequestMode(), "msg_is_multiblock")) {
                        StringBuilder result = new StringBuilder();
                        String inputString;
                        while (!(inputString = input.readUTF()).equals("$END$")) {
                            Log.add(Log.DEBUG, "Input", inputString);
                            result.append(inputString);
                        }

                        inputMsg = new Message(result.toString());
                    }
                }



                Message responseMsg = new Message();
                switch (inputMsg.getRequestType()) {
                    case "auth":
                        if (!isAuth) {
                            HashMap<String, String> userData = authUser(inputMsg);
                            JSONObject json = new JSONObject();
                            if (userData == null) {
                                json.put("state", "failed");
                            } else {
                                json.put("state", "success");

                                if (inputMsg.getRequestMode().equals("sign_in")) {
                                    json.put("nickname", userData.get("nickname"));
                                    json.put("avatar", userData.get("avatar"));
                                    json.put("characters", userData.get("characters"));
                                }
                                isAuth = true;
                            }

                            json.put("request", "auth");


                            responseMsg.setData(json);
                            assert userData != null;
                            responseMsg.setUserId(userData.get("id"));

                            Log.add(Log.DEBUG, "POST", responseMsg.toString());
                            if (isBigMessage(responseMsg)) {
                                sendBigMessage(responseMsg, output);
                            } else {
                                output.writeUTF(responseMsg.toString());
                                output.flush();
                            }
                        }

                        break;

                    case "update":
                        if (isAuth) {
                            User user = Database.getUser(inputMsg.getUserId(), Database.ID);
                            JSONObject data = new JSONObject(inputMsg.getData());
                            User updated = user.updateForValueName(data.getString("value"), inputMsg.getRequestMode());

                            Database.updateUser(updated, inputMsg.getRequestMode());

                            JSONObject updateResponseData = new JSONObject();
                            updateResponseData.put("state", "success");
                            updateResponseData.put("request", "update");

                            responseMsg.setData(updateResponseData);
                            responseMsg.setUserId(user.getId());

                            Log.add(Log.DEBUG, "POST", responseMsg.toString());
                            output.writeUTF(responseMsg.toString());
                        }

                        break;
                    case "character":
                        if (inputMsg.getRequestMode().equals("add")) {
                            User user = Database.getUser(inputMsg.getUserId(), Database.ID);

                            Character character = getCharacter(inputMsg);

                            character.setID(generateUniqueID());

                            Database.addCharacter(character);
                            user.addCharacter(character.getID());
                            Database.updateUser(user, Database.CHARACTERS);

                            JSONObject addResponseData = new JSONObject();
                            addResponseData.put("state", "success");
                            addResponseData.put("request", "add_character");

                            responseMsg.setData(addResponseData);
                            responseMsg.setUserId(character.getID());

                            Log.add(Log.DEBUG, "POST", responseMsg.toString());
                            output.writeUTF(responseMsg.toString());
                        }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Character getCharacter(Message inputMsg) {
        Character character = new Character();

        JSONObject data = new JSONObject(inputMsg.getData());

        String arrayString = ((String)data.get("data")).substring(1, ((String)data.get("data")).length() - 1);
        String[] array = arrayString.split(", |,");

        for (int i = 0; i < array.length - 1; i += 2) {
            character.addDataField(array[i], array[i + 1]);
        }

        character.setAvatar((String)data.get("avatar"));

        return character;
    }

    private boolean isBigMessage(Message msg) {
        return msg.toString().length() > 30000;
    }

    private void sendBigMessage(Message msg, DataOutputStream dos) {
        String msgString = msg.toString();

        ArrayList<String> strings = getStrings(msgString);

        Message sysMsg = new Message();
        sysMsg.setRequestMode("msg_is_multiblock");
        sysMsg.setRequestType("sys");

        try {
            dos.writeUTF(sysMsg.toString());
            dos.flush();

            for (int i = 0; i < strings.size(); i++) {
                dos.writeUTF(strings.get(i));
                dos.flush();
            }

            dos.writeUTF("$END$");
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getStrings(String msgString) {
        int len = msgString.length();
        int offset = 0;

        ArrayList<String> strings = new ArrayList<>();

        while (len > 30000) {
            StringBuilder strBuilder = new StringBuilder();

            for (int i = 0; i < 30000; i++) {
                strBuilder.append(msgString.charAt(i + offset));
            }

            strings.add(strBuilder.toString());
            offset += 30000;
            len -= 30000;
        }

        if (len != 0) {
            StringBuilder strBuilder = new StringBuilder();

            for (int i = 0; i < len; i++) {
                strBuilder.append(msgString.charAt(i + offset));
            }

            strings.add(strBuilder.toString());
        }
        return strings;
    }

    private HashMap<String, String> authUser(Message data) {
        JSONObject msgData = new JSONObject(data.getData());

        if (Objects.equals(data.getRequestMode(), "sign_in")) {
            User user = Database.getUser(msgData.getString("email"), Database.EMAIL);
            if (user != null) {
                if (Objects.equals(user.getPassword(), String.valueOf(msgData.getString("password").hashCode()))) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", user.getId());
                    map.put("nickname", user.getNickname());
                    map.put("avatar", user.getAvatar());
                    map.put("characters", user.getValueForName(User.CHARACTERS));

                    return map;
                }
            }
        } else {
            if (Database.getUser(msgData.getString("email"), Database.EMAIL) == null) {
                String id = generateUniqueID();
                User user = new User(id, (String) msgData.get("nickname"), msgData.getString("email"), String.valueOf(msgData.get("password").hashCode()), null, new ArrayList<>());
                IDs.add(id);

                Database.addUser(user);

                HashMap<String, String> map = new HashMap<>();
                map.put("id", user.getId());
                return map;
            }
        }

        return null;
    }

    public static String generateUniqueID() {
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

    public static boolean checkUserID(String id) {
        for (String s : IDs) {
            if (Objects.equals(s, id)) return true;
        }

        return false;
    }
}
