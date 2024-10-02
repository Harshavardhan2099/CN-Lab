import java.io.*; 
import java.net.*; 
import java.awt.*; 
import java.awt.event.*;
import javax.swing.*; 

public class UDPServer extends JFrame {
    private JTextArea textArea; 
    private JTextField textField; 
    private JButton sendButton; 
    private DatagramSocket socket; 
    private InetAddress clientAddress; 
    private int clientPort; 

    public UDPServer() {
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

        // send msg to the client 
        sendButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    String message = textField.getText();
                    textArea.append("Server: " + message + "\n");
                    byte[] sendData = message.getBytes();
                    if (clientAddress != null && clientPort != -1){
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                        socket.send(sendPacket);
                        textField.setText("");
                        if(message.equalsIgnoreCase("bye")){
                            System.exit(0);
                        }
                    }
                } catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        });
        setVisible(true);
    }

    // receive msg from the client
    public void startServer() {
        try {
            socket = new DatagramSocket(5555);
            textArea.append("Server is running and waiting for a connection...\n");
            byte[] receiveData = new byte[1024]; 
            while(true) { 
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                clientAddress = receivePacket.getAddress();
                clientPort = receivePacket.getPort();
                String clientSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
                if(clientSentence.equalsIgnoreCase("bye")) {
                    textArea.append("Client disconnected\n");
                    break; 
                }
                textArea.append("Client: " + clientSentence + "\n");
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   

    public static void main(String[] args) {
        UDPServer server = new UDPServer();
        server.startServer();
    }
}