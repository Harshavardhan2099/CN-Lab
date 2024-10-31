import java.io.*;
import java.net.*; 

public class FTPClient {
    public static void main(String[] args) throws Exception {
        // create a socket to connect to the server
        Socket socket = new Socket("localhost", 5000);
        // file input stream to read input from the file
        FileInputStream fs = new FileInputStream("D:\\dirsample.txt");
        // Output stream to write output to the socket
        OutputStream os = socket.getOutputStream();

        // sending data in bytes for encryption
        byte[] buffer = new byte[1024];
        int br; // bytes read
        while ((br = fs.read(buffer)) != -1) {
            // encryption
            for (int i=0; i<br; i++) {
                buffer[i] = (byte) ((buffer[i] + 3) % 256);
            }
            os.write(buffer, 0, br);
        }
        fs.close();
        os.close();
        socket.close();
    }
}
