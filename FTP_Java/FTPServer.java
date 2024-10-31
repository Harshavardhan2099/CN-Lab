import java.io.*;
import java.net.*; 

public class FTPServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(5000);
        // waiting for client
        Socket socket = serverSocket.accept();
        // File output Stream to write output to the file 
        FileOutputStream fs = new FileOutputStream("Downloaded.txt");
        // input stream to read input from the server 
        InputStream is = socket.getInputStream();

        // bytes for decryption
        byte[] buffer = new byte[1024];
        int br; // bytes read
        while ((br = is.read(buffer)) != -1) {
	    for (int i=0; i<br; i++) {
	    	buffer[i] = (byte) ((buffer[i]-3) % 256);
	    }
            fs.write(buffer, 0, br);
        }
        fs.close();
        is.close();
        socket.close();
        serverSocket.close();
    }
}
