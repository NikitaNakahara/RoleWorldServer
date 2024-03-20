import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Log {
    public static final String DEBUG = "DEBUG";

    public static void add(String mode, String title, String data){
        try {
            FileWriter logFile = new FileWriter("/home/nakaharadev/dev/java/RoleWorldServer/src/main/resources/server.log", true);

            String time = LocalDateTime.now().toString().replace('T', ' ');


            logFile.write(time + "[" + mode + "]" + title + ": " + data + "\n");
            logFile.flush();

            logFile.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
