package client;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import commonUtils.Channel;
import commonUtils.InitPackage;
import commonUtils.PartOfFile;

import java.io.*;
import java.net.*;

/**
 * Created by Виктория on 05.05.2017.
 */
public class Client {
    //args: address, port, filename
    private static int packageSize = 2048;
    private static int maxCountInChannel = getMaxCountInChannel();
    private static long size;

    public static int getMaxCountInChannel(){
        int channelVolume = 32768;
        return channelVolume/packageSize;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        int port = Integer.parseInt(args[0]);
        String address = args[1];
        int port1 = Integer.parseInt(args[2]);
        String filename = args[3];
        int l = 4;
        while (args.length > 5 && l < args.length) {
            filename = filename.concat(" " + args[l]);
            l++;
        }
        File file = new File(filename);
        if (file.exists()) {
            long size = file.length();
            Client.size = size;
            long packageCount = (long) Math.ceil((double) size / packageSize);
            System.out.println(packageCount);
            FileInputStream fileInputStream = new FileInputStream(filename);
            Channel<PartOfFile> partOfFileChannel = new Channel<>(maxCountInChannel);
            CallBackReader callBackReader = new CallBackReader();
            CallBackSender callBackSender = new CallBackSender();
            String actualName = file.getName();
            InitPackage initPackage = new InitPackage(size, actualName, packageSize);
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(initPackage);
            byte[] bytes = byteOutputStream.toByteArray();
            System.out.println("Size of initPacket = " + bytes.length + " bytes");
            FileReader fileReader = new FileReader(fileInputStream, packageSize, partOfFileChannel, callBackReader);
            Thread fileReaderThread = new Thread(fileReader);
            fileReaderThread.start();
            DatagramSocket socket = new DatagramSocket(port);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(address, port1);
            DatagramPacket initPacket = new DatagramPacket(bytes, bytes.length, inetSocketAddress);


            socket.send(initPacket);
            boolean deliveryCheckList[] = new boolean[(int) packageCount];
            for (boolean i : deliveryCheckList) {
                i = false;
            }
            final Object deliveryListLock = new Object();
            Receiver receiver = new Receiver(socket, deliveryCheckList, callBackSender);
            Thread receiverThread = new Thread(receiver);
            receiverThread.start();
            Sender sender = new Sender((int) packageCount, socket, inetSocketAddress, callBackSender, callBackReader, fileReader.getChannel(),
                    deliveryCheckList);
            Thread senderThread = new Thread(sender);
            senderThread.start();

        } else
            System.out.println("This file \"" + filename + "\" does not exist.");
    }
}
