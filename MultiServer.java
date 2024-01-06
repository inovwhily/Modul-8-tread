import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Kelas utama untuk Server
class MultiServer {
    private static final int PORT = 8888; // Ubah port menjadi 8888
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            // Menggunakan ExecutorService untuk mengelola thread
            ExecutorService executorService = Executors.newFixedThreadPool(10);

            while (true) {
                // Menerima koneksi dari klien
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Membuat handler baru untuk klien dan menjalankan di thread terpisah
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast pesan ke semua klien
    static void broadcastMessage(String message, ClientHandler sender) {
        System.out.println("Received from client " + sender.getClientSocket() + ": " + message);
        for (ClientHandler client : clients) {
            // Mengirim pesan ke semua klien kecuali pengirim asli
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Remove disconnected client
    static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client disconnected: " + clientHandler.getClientSocket());
    }
}

// Kelas untuk menangani koneksi dari setiap klien
class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            // Inisialisasi reader dan writer untuk koneksi
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Membaca pesan dari klien
                String message = reader.readLine();

                // Mengirim pesan ke semua klien
                MultiServer.broadcastMessage(message, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Menutup koneksi jika terjadi kesalahan
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Remove client from the server
            MultiServer.removeClient(this);
        }
    }

    // Mengirim pesan ke klien
    void sendMessage(String message) {
        writer.println(message);
    }

    // Getter untuk informasi socket klien
    Socket getClientSocket() {
        return clientSocket;
    }
}
