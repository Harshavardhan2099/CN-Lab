import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TCPClient extends JFrame {
    private JTextArea textArea;
    private JTextField textField;
    private JButton sendButton;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;

    public TCPClient() {
        setTitle("Client Chat");
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
                    textArea.append("Client: " + message + "\n");
                    outToServer.writeBytes(message + "\n");
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

    public void startClient() {
        try {
            Socket clientSocket = new Socket("localhost", 5555);
            textArea.append("Connected to server\n");

            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer = new DataOutputStream(clientSocket.getOutputStream());

            String serverResponse;
            while (true) {
                serverResponse = inFromServer.readLine();
                if (serverResponse == null || serverResponse.equalsIgnoreCase("bye")) {
                    textArea.append("Server disconnected\n");
                    break;
                }
                textArea.append("Server: " + serverResponse + "\n");
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) {
        TCPClient client = new TCPClient();
        client.startClient();
    }
}
