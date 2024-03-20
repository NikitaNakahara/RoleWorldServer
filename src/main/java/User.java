import java.util.ArrayList;

public class User {
    private final String id;
    private final String nickname;
    private final String email;
    private final String password;
    private final String avatar;

    private ArrayList<String> characters = new ArrayList<>();

    public static final String ID = "id";
    public static final String NICKNAME = "nickname";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String AVATAR = "avatar";
    public static final String CHARACTERS = "characters";

    public User(String id, String nickname, String email, String password, String avatar, ArrayList<String> characters) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.characters = characters;
    }

    public User updateNickname(String value) {
        return new User(this.id, value, this.email, this.password, this.avatar, this.characters);
    }

    public User updateEmail(String value) {
        return new User(this.id, this.nickname, value, this.password, this.avatar, this.characters);
    }

    public User updateAvatar(String value) {
        return new User(this.id, this.nickname, this.email, this.password, value, this.characters);
    }

    public void addCharacter(String id) { characters.add(id); }

    public User updateForValueName(String value, String valueName) {
        switch (valueName) {
            case NICKNAME:
                return new User(this.id, value, this.email, this.password, this.avatar, this.characters);

            case EMAIL:
                return new User(this.id, this.nickname, value, this.password, this.avatar, this.characters);

            case PASSWORD: // for later src update
                return null;

            case AVATAR:
                return new User(this.id, this.nickname, this.email, this.password, value, this.characters);

            default:
                return null;
        }
    }

    public String getId() { return id; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getAvatar() { return avatar; }
    public ArrayList<String> getCharacters() { return characters; }

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

            case CHARACTERS:
                if (characters.size() == 0) return "[]";

                StringBuilder str = new StringBuilder("[");
                for (String id : characters) {
                    str.append(id).append(", ");
                }
                Log.add(Log.DEBUG, "Character", str.toString());
                str = new StringBuilder(str.substring(0, str.length() - 2));
                str.append("]");

                return str.toString();

            default:
                return null;
        }
    }
}
