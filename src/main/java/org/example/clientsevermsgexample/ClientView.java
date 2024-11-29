package org.example.clientsevermsgexample;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientView {

        @FXML
        private AnchorPane ap_main;

        @FXML
        private Button button_send;

        @FXML
        private ScrollPane sp_main;

        @FXML
        private TextField tf_message;

        @FXML
        private VBox vbox_messages;

        private Socket socket1;
        private DataInputStream dis;
        private DataOutputStream dos;

        public AnchorPane getApMain() {
                return ap_main;
        }

        // Connect to the server
        public void connectToServer() {
                try {
                        socket1 = new Socket("localhost", 6666);
                        listenForMessages(); // Start listening for messages from server
                        updateTextClient("Connected to server!");
                        button_send.setDisable(false); // Enable send button
                } catch (IOException e) {
                        updateTextClient("Error connecting to server: " + e.getMessage());
                }
        }

        // This method listens for incoming messages from the server
        private void listenForMessages() {
                new Thread(() -> {
                        try {
                                dis = new DataInputStream(socket1.getInputStream());
                                while (true) {
                                        String message = dis.readUTF();
                                        updateTextClient("Server: " + message);
                                }
                        } catch (IOException e) {
                                updateTextClient("Error receiving message: " + e.getMessage());
                                closeSocket(); // Close socket if there's an error
                        }
                }).start();
        }

        // Method to send a message to the server
        public void sendMessage() {
                String message = tf_message.getText();
                if (socket1 != null && socket1.isConnected()) {
                        try {
                                dos = new DataOutputStream(socket1.getOutputStream());
                                dos.writeUTF(message);
                                updateTextClient("Client: " + message);
                                tf_message.clear(); // Clear the text field after sending the message
                        } catch (IOException e) {
                                updateTextClient("Error sending message: " + e.getMessage());
                        }
                } else {
                        updateTextClient("Socket is not connected or is closed.");
                }
        }

        // Update the client UI with messages
        private void updateTextClient(String message) {
                Platform.runLater(() -> {
                        Label msgLabel = new Label(message);
                        vbox_messages.getChildren().add(msgLabel);
                });
        }

        // Gracefully close the socket
        private void closeSocket() {
                try {
                        if (socket1 != null && !socket1.isClosed()) {
                                socket1.close();
                                updateTextClient("Disconnected from server.");
                        }
                } catch (IOException e) {
                        updateTextClient("Error closing socket: " + e.getMessage());
                }
        }

        // Optional: Add a method to disconnect the client
        public void disconnectFromServer() {
                closeSocket();
        }
}
