import java.sql.*;
import java.util.Objects;

public class Database {
    private static Connection usersConnection = null;

    public static final String ID = "id";
    public static final String NICKNAME = "nickname";
    public static final String EMAIL = "email";
    public static final String AVATAR = "avatar";

    public static void create() {
        try {
            Class.forName("org.sqlite.JDBC");
            usersConnection = DriverManager.getConnection("jdbc:sqlite:/home/nikita/Рабочий стол/dev-java/RoleWorldServer/src/main/resources/db/users.db");

            Statement statement = usersConnection.createStatement();

            statement.execute("CREATE TABLE if not exists users (" +
                    "id TEXT," +
                    "nickname TEXT," +
                    "email TEXT," +
                    "password TEXT" +
                    ");");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateUser(User user, String columnForUpdate) {
        try {
            Statement statement = usersConnection.createStatement();
            String sqlRequest = "UPDATE users SET " + columnForUpdate + " = '" + user.getValueForName(columnForUpdate) + "' WHERE id = '" + user.getId() + "';";
            statement.execute(sqlRequest);
            System.out.println("Updated " + columnForUpdate + ": " + user.getValueForName(columnForUpdate));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addUser(
            String id,
            String nickname,
            String email,
            String password
    ) {
        try {
            Statement statement = usersConnection.createStatement();
            statement.execute("INSERT INTO users (id, nickname, email, password) VALUES (" + id + nickname + email + password + ");");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addUser(User user) {
        try {
            Statement statement = usersConnection.createStatement();
            statement.execute("INSERT INTO users (id, nickname, email, password) VALUES ('" + user.getId() + "', '" + user.getNickname() + "', '" + user.getEmail() + "', '" + user.getPassword() + "');");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User getUser(String id, String type) {
        if (id == null) return null;

        try {
            Statement statement = usersConnection.createStatement();
            ResultSet userData = statement.executeQuery("select * from users;");

            while (userData.next()) {
                if (Objects.equals(userData.getString(type), id)) {
                    return new User(userData.getString("id"), userData.getString("nickname"), userData.getString("email"), userData.getString("password"), userData.getString("avatar"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
