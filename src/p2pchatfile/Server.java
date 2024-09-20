package p2pchatfile;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {
    private static final int PORT = 4000; // Port cho server
    private static final String FILE_DIR = "server_files/"; // Thư mục lưu file

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đang chạy, chờ client kết nối...");

            // Tạo thư mục lưu file nếu chưa có
            File dir = new File(FILE_DIR);
            if (!dir.exists()) {
                dir.mkdir();
            }

            while (true) {
                // Nhận kết nối từ client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client kết nối: " + clientSocket.getInetAddress());

                // Tạo thread để xử lý client
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String clientMessage;

                // Đọc từng dòng từ client
                while ((clientMessage = in.readLine()) != null) {
                    logMessage("Client: " + clientMessage);

                    // Nếu client gửi tin nhắn dạng file:
                    if (clientMessage.startsWith("file:")) {
                        String filePath = clientMessage.substring(5);
                        receiveFile(filePath, in);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void receiveFile(String filePath, BufferedReader in) throws IOException {
            // Nhận file từ client
            String fileName = new File(filePath).getName();
            File file = new File(FILE_DIR + fileName);

            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {
                String line;
                while ((line = in.readLine()) != null) {
                    fileWriter.write(line + "\n");
                }
            }
            logMessage("File nhận: " + fileName);
        }

        private void logMessage(String message) {
            // Log tin nhắn với timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            System.out.println("[" + timestamp + "] " + message);
        }
    }
}
