import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Kelas untuk Klien
class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8888); //  port menjadi 8888
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Membuat thread untuk membaca pesan dari server
            new Thread(() -> {
                try {
                    while (true) {
                        String message = reader.readLine();
                        System.out.println("Received from server: " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Membuat thread untuk mengirim pesan ke server
            new Thread(() -> {
                try {
                    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                    while (true) {
                        System.out.print("Enter message: ");
                        String message = consoleReader.readLine();
                        writer.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
