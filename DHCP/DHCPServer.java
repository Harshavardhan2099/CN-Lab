import java.net.*;
import java.util.HashMap;

public class DHCPServer {
    private static HashMap<String, String> ipPool = new HashMap<>();  // Maps MAC addresses to IP addresses
    private static String subnet = "192.168.1.";
    private static int nextIP = 100;  // Start assigning IPs from 192.168.1.100

    public static void main(String[] args) throws Exception {
        @SuppressWarnings("resource")
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];
        byte[] sendData;

        System.out.println("DHCP Server is running...");

        while (true) {
            // Receive client request
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Received: " + clientMessage);

            InetAddress clientIPAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            String[] parts = clientMessage.split(":");

            String messageType = parts[0].trim();
            String clientMAC = parts[1].trim();

            String assignedIP = ipPool.get(clientMAC);

            // Handle DHCP message types
            switch (messageType) {
                case "DHCPDISCOVER":
                    // DHCP DISCOVER: Offer a new IP if the client doesn't already have one
                    if (assignedIP == null) {
                        assignedIP = assignIP(clientMAC);
                    }
                    String offerMessage = "DHCPOFFER: " + assignedIP;
                    sendData = offerMessage.getBytes();
                    DatagramPacket offerPacket = new DatagramPacket(sendData, sendData.length, clientIPAddress, clientPort);
                    serverSocket.send(offerPacket);
                    System.out.println("Offered IP: " + assignedIP + " to client with MAC: " + clientMAC);
                    break;

                case "DHCPREQUEST":
                    // DHCP REQUEST: Confirm the offered IP and send an acknowledgment
                    String requestedIP = parts[2].trim();
                    if (requestedIP.equals(assignedIP)) {
                        String ackMessage = "DHCPACK: " + requestedIP;
                        sendData = ackMessage.getBytes();
                        DatagramPacket ackPacket = new DatagramPacket(sendData, sendData.length, clientIPAddress, clientPort);
                        serverSocket.send(ackPacket);
                        System.out.println("Assigned IP: " + requestedIP + " to client with MAC: " + clientMAC);
                    }
                    break;

                default:
                    System.out.println("Unknown DHCP message type.");
            }
        }
    }

    // Method to assign a new IP
    private static String assignIP(String macAddress) {
        String newIP = subnet + nextIP;
        nextIP++;
        ipPool.put(macAddress, newIP);
        return newIP;
    }
}
