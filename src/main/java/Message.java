import org.json.JSONObject;

public class Message {
    public static String AUTH_REQ_TYPE = "auth";

    public static String SIGN_IN_MODE = "sign_in";
    public static String SIGN_UP_MODE = "sign_up";

    private String requestType = "";
    private String requestMode = "";
    private String data        = "";
    private String userId      = "";

    Message() {}

    Message(String requestType, String requestMode, String data, String userId) {
        this.requestType = requestType;
        this.requestMode = requestMode;
        this.data = data;
        this.userId = userId;
    }

    Message(String requestType, String requestMode, JSONObject data, String userId) {
        this.requestType = requestType;
        this.requestMode = requestMode;
        this.data = data.toString();
        this.userId = userId;
    }

    Message(String msg) {
        this(new JSONObject(msg));
    }

    Message(JSONObject msg) {
        this.requestType = msg.getString("type");
        this.requestMode = msg.getString("mode");
        if (!msg.getString("data").isEmpty()) {
            this.data = new JSONObject(msg.getString("data")).toString();
        }
        this.userId = msg.getString("id");
    }


    public String getRequestType() {
        return requestType;
    }

    public String getRequestMode() {
        return requestMode;
    }

    public String getData() {
        return data;
    }

    public String getUserId() {
        return userId;
    }

    public void setRequestType(String value) {
        requestType = value;
    }

    public void setRequestMode(String value) {
        requestMode = value;
    }

    public void setData(String value) {
        data = value;
    }

    public void setData(JSONObject value) {
        data = value.toString();
    }


    public void setUserId(String value) {
        userId = value;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("type", requestType);
        json.put("mode", requestMode);
        json.put("data", data);
        json.put("id", userId);

        return json.toString();
    }
}
