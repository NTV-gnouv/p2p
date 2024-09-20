package p2pchatfile;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 4000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Kết nối tới server thành công!");
            Scanner scanner = new Scanner(System.in);

            // Vòng lặp để gửi tin nhắn hoặc file từ client
            while (true) {
                System.out.println("Nhập tin nhắn hoặc 'file:<đường dẫn tới file>' để gửi file:");
                String message = scanner.nextLine();

                if (message.startsWith("file:")) {
                    String filePath = message.substring(5);
                    sendFile(filePath, out);
                } else {
                    // Gửi tin nhắn
                    out.println(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendFile(String filePath, PrintWriter out) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File không tồn tại.");
            return;
        }

        // Gửi file
        out.println("file:" + filePath);
        try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                out.println(line);
            }
            out.flush();
            System.out.println("File đã được gửi: " + file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
