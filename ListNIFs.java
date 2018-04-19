import java.io.*;
import java.net.*;
import java.util.*;
import static java.lang.System.out;

public class ListNIFs
{
    public static void main(String args[]) throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
                                
            for (NetworkInterface netIf : Collections.list(nets)) {
                out.printf("Display name: %s\n", netIf.getDisplayName());
                out.printf("Name: %s\n", netIf.getName());
                out.printf("Mac Adress: %s\n", getMacAddress(netIf));
                // FROM docs.oracle: getInetAddresses() Get a List of all or a subset of the 
                // InterfaceAddresses of this network interface
                displayHostName(netIf.getInetAddresses());
                displayHostAddress(netIf.getInetAddresses());
                displaySubInterfaces(netIf);
                out.printf("\n");                    
            }
    }


    static void displaySubInterfaces(NetworkInterface netIf) throws SocketException {
        Enumeration<NetworkInterface> subIfs = netIf.getSubInterfaces();
                                    
        for (NetworkInterface subIf : Collections.list(subIfs)) {
            out.printf("\tSub Interface Display name: %s\n", subIf.getDisplayName());
            out.printf("\tSub Interface Name: %s\n", subIf.getName());
        }
    }

    static String getMacAddress(NetworkInterface netIf) {
        StringBuilder sb = null;
        byte[] mac;
        try {
            mac = netIf.getHardwareAddress();
            if (mac == null) {
                return "None";
            }
            // StringBuilder is mutable, so it's possible to append each byte
            sb = new StringBuilder();
            for (byte digit : mac) {
                // [02X] convert to uppercase hexadecimal integer
                sb.append(String.format("%02X", digit));
                // formatting
                if (digit != mac[mac.length-1]) {
                    sb.append('-');
                }
            }
        }
        catch (Exception uEE) {
            uEE.printStackTrace();
        }
        return sb.toString();
    }

    static String displayHostName(Enumeration<InetAddress> iNets) {

        for (InetAddress iNet : Collections.list(iNets)) {
            out.printf("Host name: %s\n", iNet.getHostName());
        }
        return "Hostnames\n";
    }
    
    static String displayHostAddress(Enumeration<InetAddress> iNets) {

        for (InetAddress iNet : Collections.list(iNets)) {
            out.printf("Host Address: %s\n", iNet.getHostAddress());
        }
        return "Hostaddress\n";
    }

}  
