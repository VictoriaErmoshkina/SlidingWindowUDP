package server;


import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by Виктория on 05.05.2017.
 */

public class Server {
    //args: port, host, port

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        String host = args[1];
        int port1 = Integer.parseInt(args[2]);
        String pathName = args[3];
        int l = 4;
        while (args.length > 4 && l < args.length) {
            pathName = pathName.concat(" " + args[l]);
            l++;
        }
        try {
            DatagramSocket socket = new DatagramSocket(port);
            InetSocketAddress address = new InetSocketAddress(host, port1);
            FileWriter fileWriter = new FileWriter(socket, address, pathName);
            Thread fileWriterThread = new Thread(fileWriter);
            fileWriterThread.start();

        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

}