package org.example.clientsevermsgexample;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerView {

    @FXML
    private AnchorPane ap_main;

    @FXML
    private Button button_send;

    @FXML
    private Button Start_Server;

    @FXML
    private ScrollPane sp_main;

    @FXML
    private TextField tf_message;

    @FXML
    private VBox vbox_messages;

    private Socket clientSocket;
    private DataOutputStream dos;

    // This method is called when the 'Start Server' button is clicked
    @FXML
    private void runServer() {
        // Start the server in a background thread to avoid blocking the UI thread
        new Thread(() -> {
            try {
                // Create ServerSocket and listen for client connections
                ServerSocket serverSocket = new ServerSocket(6666);
                updateServer("Server is running and waiting for a client...");

                // Accept client connections in a loop
                clientSocket = serverSocket.accept();
                updateServer("Client connected!");

                // Enable the send button once the client is connected
                button_send.setDisable(false);

                // Start listening for messages from the client
                listenForMessages();
            } catch (IOException e) {
                updateServer("Error: " + e.getMessage());
            }
        }).start(); // Run the server code in a background thread
    }

    // This method listens for messages from the client
    private void listenForMessages() {
        new Thread(() -> {
            try {
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                while (true) {
                    String message = dis.readUTF();
                    updateTextServer("Client: " + message);
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
            } catch (IOException e) {
                updateTextServer("Error receiving message: " + e.getMessage());
            }
        }).start();
    }

    // This method is used to send messages to the client
    @FXML
    private void sendMessage() {
        String message = tf_message.getText();
        if (clientSocket != null && clientSocket.isConnected()) {
            try {
                dos = new DataOutputStream(clientSocket.getOutputStream());
                dos.writeUTF(message);  // Send the message to the client
                tf_message.clear();  // Clear the text field after sending
                updateTextServer("Server: " + message);
            } catch (IOException e) {
                updateTextServer("Error sending message: " + e.getMessage());
            }
        } else {
            updateTextServer("No client connected!");
        }
    }

    // Method to update the server UI with messages
    private void updateServer(String message) {
        Platform.runLater(() -> {
            Label statusLabel = new Label(message);
            vbox_messages.getChildren().add(statusLabel);  // Add message to VBox
        });
    }

    // Method to update the UI with received server/client messages
    private void updateTextServer(String message) {
        Platform.runLater(() -> {
            Label msgLabel = new Label(message);
            vbox_messages.getChildren().add(msgLabel);  // Add message to VBox
        });
    }
}
