import java.io.*; 
import java.net.*; 
import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*; 

public class UDPClient extends JFrame {
    private JTextArea textArea; 
    private JTextField textField; 
    private JButton sendButton; 
    private DatagramSocket socket; 
    private InetAddress serverAddress; 
    private int serverPort = 5555; 

    public UDPClient() {
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
        sendButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try {
                    String message = textField.getText(); 
                    textArea.append("Client: " + message + "\n");
                    byte[] sendData = message.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                    socket.send(sendPacket);
                    textField.setText("");
                    if(message.equalsIgnoreCase("bye")) {
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
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName("localhost");
            textArea.append("Client started\n");
            byte[] receiveData = new byte[1024];
            while(true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
                if (serverResponse.equalsIgnoreCase("bye")) {
                    textArea.append("Server disconnected\n");
                    break; 
                }
                textArea.append("Server: " + serverResponse + "\n");
            } 
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        UDPClient client = new UDPClient(); 
        client.startClient();
    }

}
