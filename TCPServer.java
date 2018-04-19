/******************************************************************************
 *  Compilation:  javac TCPServer.java
 *  Execution:    java TCPServer
 *  Dependencies: In.java Out.java
 *  
 *  Runs an echo server which listents for connections on port 10251,
 *  connects to port 2051 for checksum and echoes back whatever is sent to it.
 *
 *  Limitations
 *  -----------
 *  The server is not multi-threaded, so at most one client can connect
 *  at a time.
 *
 ******************************************************************************/

import java.net.*;
import java.util.zip.*;
import java.io.*;
public class TCPServer {

    public static void main(String[] args) throws IOException {
        final int commPort = 10251;
        final int errPort = 20251;
        final String tc = "X";
        byte[] buffer;
        ServerSocket serverSocket = null;
        ServerSocket errSocket = null;
        CRC32 crc = null;
        try {
            // create socket
            serverSocket = new ServerSocket(commPort);
            errSocket = new ServerSocket(errPort);
            buffer = new byte[1024];
            crc = new CRC32();
            System.err.println("Started server on port " + commPort);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            System.err.println("Usage: java EchoSever <PortNumber>");
            System.exit(1);
        } catch(IOException ie) {
            ie.printStackTrace();
            System.err.printf("Cannot listen on port %d\n", commPort);
            System.exit(1);
        }


        while (true) {
            
            Socket clientErr = errSocket.accept();// repeatedly wait for connections, and process
            Socket clientSocket = serverSocket.accept();
            // open up IO streams
            Out     stdout = new Out();
            In      in     = new In(clientSocket);
            Out     out    = new Out(clientSocket);
            In        err    = new In(clientErr);
            System.err.println("Accepted connection from client");

            stdout.printf("Local address: %s\n", 
                    clientSocket.getLocalSocketAddress());
            stdout.printf("Port: %s\n", clientSocket.getLocalPort());
            // waits for data and reads it in until connection dies
            // readLine() blocks until the server receives a new line from client
            String s;
            String checksum;
            while ((s = in.readLine()) != null) {
                if (s.equals(tc)) {
                    break;
                }
                // Read calculated checksum from client
                checksum = err.readLine();
                crc.update(s.getBytes());
                stdout.println("[client]: " + s);
                out.println(s);
                stdout.println("[info]: Read checksum: " + checksum +
                               ", Calculated checksum: " + crc.getValue());
                if (Long.parseLong(checksum) == crc.getValue()) {
                    stdout.println("Checksums are the same!");
                }
                else {
                    stdout.println("Checksums aren't equal");
                }
                crc.reset();
            }

            // close IO streams, then socket
            System.err.println("Closing connection with client");
            out.close();
            in.close();
            clientSocket.close();
            break;
        }
    }
}
