import java.net.*;
import java.util.Scanner;


public class DHCPClient {
    public static void main(String[] args) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress serverAddress = InetAddress.getByName("localhost");
        byte[] sendData; 
        byte[] receiveData = new byte[1024];

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter client MAC Address: ");
        String clientMac = sc.nextLine(); 

        // DHCP DISCOVER 
        String discoverMessage = "DHCPDISCOVER: " + clientMac; 
        sendData = discoverMessage.getBytes(); 
        DatagramPacket discoverPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 9876);
        clientSocket.send(discoverPacket);
        System.out.println("Sent DHCPDISCOVER");

        // DHCP OFFER 
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String offerMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Received: " + offerMessage);
        String[] offerParts = offerMessage.split(":");
        String assignedIP = offerParts[1].trim();
        
        // DHCP REQUEST
        String requestMessage = "DHCPREQUEST: " + clientMac + ": " + assignedIP; 
        sendData = requestMessage.getBytes(); 
        DatagramPacket requestPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 9876);
        clientSocket.send(requestPacket);
        System.out.println("Sent DHCPREQUEST for IP: " + assignedIP);

        // DHCP ACK 
        receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String ackMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("Received: " + ackMessage);
        if(ackMessage.startsWith("DHCPACK")) {
            System.out.println("IP Address assigned successfully: " + assignedIP);
        } else {
            System.out.println("Failed to assign IP address");
        }
        sc.close();
        clientSocket.close(); 
    }
}
