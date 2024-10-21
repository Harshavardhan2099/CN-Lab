import java.net.*;
import java.util.HashMap;

public class DHCPServer {
    private static HashMap<String, String> ipPool = new HashMap<>();
    private static String subnet = "192.168.1";
    private static int nextIP = 100; 
    
    public static void main(String[] args) throws Exception {
        @SuppressWarnings("resource")
        DatagramSocket serverSocket = new DatagramSocket(9876); 
        byte[] receiveData = new byte[1024];
        byte[] sendData; 

        System.out.println("DHCP Server is running...");
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Received: " + clientMessage);
            InetAddress clientAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort(); 
            String[] parts = clientMessage.split(":");
            String messageType = parts[0].trim();
            String clientMac = parts[1].trim(); 
            String assignedIP = ipPool.get(clientMac);

            switch (messageType) {
                case "DHCPDISCOVER":
                    if (assignedIP == null) {
                        assignedIP = assignIP(clientMac);
                    }
                    String offerMessage = "DHCPOFFER: " + assignedIP; 
                    sendData = offerMessage.getBytes();
                    DatagramPacket offerPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                    serverSocket.send(offerPacket);
                    System.out.println("Offered IP: " + assignedIP + " to client with MAC :" + clientMac);
                    break;
                
                case "DHCPREQUEST":
                    String requestedIP = parts[2].trim();
                    if (requestedIP == assignedIP) {
                        String ackMessage = "DHCPACK: " + requestedIP; 
                        sendData = ackMessage.getBytes(); 
                        DatagramPacket ackPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort); 
                        serverSocket.send(ackPacket);
                        System.out.println("Assigned IP: " + requestedIP + " to client with MAC: " + clientMac);
                    } 
                    break;
                default:
                    System.out.println("Unknown DHCP message type");
                    break;
            }
        }
    }

    private static String assignIP(String macAddress) {
        String newIP = subnet + nextIP;
        nextIP++; 
        ipPool.put(macAddress, newIP);
        return newIP;
    }
}