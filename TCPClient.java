

/******************************************************************************
 *  Compilation:  javac TCPClient.java
 *  Execution:    java TCPClient
 *
 *  Connects to host server on port 10251, sends text, and prints out
 *  checksum is sent on port 20251
 *  whatever the server sends back.
 *
 *  
 ******************************************************************************/

import java.net.*;
import java.io.*;
import java.util.zip.*;
public class TCPClient {
    public static void main(String[] args) throws IOException {
        final String tc = "X";
        String screenName = "client";
        String host       = "127.0.0.1";
        int commPort      = 10251;
        int errPort          = 20251;
        CRC32 crc = new CRC32();

        // connect to server and open up IO streams
        // Socket socket = new Socket(host, port);
        Socket socket = new Socket(host, commPort);
        Socket errSocket = new Socket(host, errPort);
        In     stdin  = new In();
        In     in     = new In(socket);
        Out    out    = new Out(socket);
        Out    err       = new Out(errSocket);
        System.err.println("Socket bound to " + socket.getLocalSocketAddress());

        // read in a line from stdin, send to server, and print back reply
        byte[] b;
        System.out.print("[client]: ");
        while ((b = stdin.readLine().getBytes()) != null) {
            String s = new String(b);
            if (s.equals(tc)) {
                break;
            }
            crc.update(b);
            // send over socket to server
            out.println(s);
            err.println(crc.getValue());

            // get reply from server and print it out
            System.err.println("[info]: Calculated checksum: " + crc.getValue());
            System.out.printf("[echo]: %s\n[client]: ", in.readLine());
            crc.reset();
        }

        // close IO streams, then socket
        System.err.println("Closing connection to " + host);
        out.close();
        in.close();
        socket.close();
    }
}
