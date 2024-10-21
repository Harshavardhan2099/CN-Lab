import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TCPServer extends JFrame {
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;
    private DataOutputStream outToClient;
    private BufferedReader inFromClient;

    public TCPServer() {
        setTitle("Server Chat");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        textArea = new JTextArea();
        textArea.setEditable(false);
        textField = new JTextField(30);
        sendButton = new JButton("Send");

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String message = textField.getText();
                    textArea.append("Server: " + message + "\n");
                    outToClient.writeBytes(message + "\n");
                    textField.setText("");
                    if (message.equalsIgnoreCase("bye")) {
                        System.exit(0);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        setVisible(true);
    }

    @SuppressWarnings("resource")
    public void startServer() {
        try {
            ServerSocket welcomeSocket = new ServerSocket(5555);
            textArea.append("Server is running and waiting for a connection...\n");

            Socket connectionSocket = welcomeSocket.accept();
            textArea.append("Client connected\n");

            inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            String clientSentence;
            while (true) {
                clientSentence = inFromClient.readLine();
                if (clientSentence == null || clientSentence.equalsIgnoreCase("bye")) {
                    textArea.append("Client disconnected\n");
                    break;
                }
                textArea.append("Client: " + clientSentence + "\n");
		
            }

            connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) {
        TCPServer server = new TCPServer();
        server.startServer();
    }
}
