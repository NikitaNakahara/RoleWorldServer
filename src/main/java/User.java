public class User {
    private final String id;
    private final String nickname;
    private final String email;
    private final String password;

    public User(String id, String nickname, String email, String password) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    public String getId() { return id; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
