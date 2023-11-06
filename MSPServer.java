import java.io.*;
import java.net.*;
import java.util.*;

public class MSPServer {
    private static final int PORT = 18;
    private static Map<String, String> users = new HashMap<>();
    private static List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) {
        // Agrega usuarios con contraseñas
        users.put("Juan", "password1");
        users.put("Maria", "password2");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("MSP Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.startsWith("CONNECT ")) {
                        String[] parts = inputLine.split(" ");
                        if (parts.length == 3) {
                            username = parts[1];
                            String password = parts[2];
                            if (authenticateUser(username, password)) {
                                System.out.println(username + " connected");
                                out.println("Connected as " + username);
                            } else {
                                out.println("Authentication failed");
                            }
                        } else {
                            out.println("Invalid CONNECT format");
                        }
                    } else if (inputLine.equals("DISCONNECT")) {
                        clients.remove(socket);
                        System.out.println(username + " disconnected");
                        socket.close();
                        break;
                    } else if (inputLine.equals("LIST")) {
                        out.println("Connected Users: " + getConnectedUsers());
                    } else if (inputLine.startsWith("SEND #")) {
                        String message = inputLine.substring(5);
                        if (message.length() <= 140) {
                            sendToAllClients(username + ": " + message);
                        } else {
                            out.println("Message too long (140 characters max)");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean authenticateUser(String username, String password) {
            return users.containsKey(username) && users.get(username).equals(password);
        }

        private String getConnectedUsers() {
            StringBuilder users = new StringBuilder();
            for (Socket client : clients) {
                if (client != socket) {
                    users.append(username).append(", ");
                }
            }
            return users.toString();
        }

        private void sendToAllClients(String message) {
            for (Socket client : clients) {
                if (client != socket) {
                    try {
                        PrintWriter clientOut = new PrintWriter(client.getOutputStream(), true);
                        clientOut.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
