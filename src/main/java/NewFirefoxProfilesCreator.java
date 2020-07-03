import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NewFirefoxProfilesCreator {
    public static void main(String[] args) {
        int end =100;
        int start = 1;
        for (int i=start; i<=end; i++) {
            //ProcessBuilder processBuilder = new ProcessBuilder();

// -- Linux --

// Run a shell command
            //processBuilder.command("\"C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe\"", "--no-remote", "-CreateProfile rnn"+i);

// Run a shell script
//processBuilder.command("path/to/hello.sh");

// -- Windows --

// Run a command
//processBuilder.command("cmd.exe", "/c", "dir C:\\Users\\mkyong");

// Run a bat file
//processBuilder.command("C:\\Users\\mkyong\\hello.bat");

            try {

                Process process = Runtime.getRuntime().exec("\"C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe\" -no-remote -CreateProfile rnn"+i);

                StringBuilder output = new StringBuilder();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }

                int exitVal = process.waitFor();
                if (exitVal == 0) {
                    System.out.println("Success!");
                    System.out.println(output);
                } else {
                    //abnormal...
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
