public class User {
    private final String id;
    private final String nickname;
    private final String email;
    private final String password;
    private final String avatar;

    public static final String ID = "id";
    public static final String NICKNAME = "nickname";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String AVATAR = "avatar";

    public User(String id, String nickname, String email, String password, String avatar) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }

    public User updateNickname(String value) {
        return new User(this.id, value, this.email, this.password, this.avatar);
    }

    public User updateEmail(String value) {
        return new User(this.id, this.nickname, value, this.password, this.avatar);
    }

    public User updateAvatar(String value) {
        return new User(this.id, this.nickname, this.email, this.password, value);
    }

    public User updateForValueName(String value, String valueName) {
        switch (valueName) {
            case NICKNAME:
                return new User(this.id, value, this.email, this.password, this.avatar);

            case EMAIL:
                return new User(this.id, this.nickname, value, this.password, this.avatar);

            case PASSWORD: // for later src update
                return null;

            case AVATAR:
                return new User(this.id, this.nickname, this.email, this.password, value);

            default:
                return null;
        }
    }

    public String getId() { return id; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getAvatar() { return avatar; }

    public String getValueForName(String valueName) {
        switch (valueName) {
            case ID:
                return this.id;

            case NICKNAME:
                return this.nickname;

            case EMAIL:
                return this.email;

            case PASSWORD:
                return this.password;

            case AVATAR:
                return this.avatar;

            default:
                return null;
        }
    }
}
