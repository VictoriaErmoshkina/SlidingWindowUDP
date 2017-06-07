package server;


import commonUtils.InitPackage;
import commonUtils.IntToByte;
import commonUtils.PartOfFile;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Виктория on 05.05.2017.
 */
public class FileWriter implements Runnable {
    DatagramSocket socket;
    InetSocketAddress address;
    String path;

    public FileWriter(DatagramSocket socket, InetSocketAddress address, String path) {
        this.socket = socket;
        this.address = address;
        this.path = path;
    }

    @Override
    public void run() {
        byte[] bytes = new byte[1024];
        DatagramPacket initPacket = new DatagramPacket(bytes, 1024);
        try {
            long start = System.currentTimeMillis();
            socket.receive(initPacket);
            ByteArrayInputStream bis = new ByteArrayInputStream(initPacket.getData());
            ObjectInputStream ois = new ObjectInputStream(bis);
            InitPackage initPackage = (InitPackage) ois.readObject();
            long packageSize = initPackage.getPackageSize();
            String fileName = initPackage.getFileName();
            long fileSize = initPackage.getFileSize();
            System.out.println("Package size = " + packageSize + "; filename = " + fileName + "; fileSize = " + fileSize);
            byte[] bytesPacket = new byte[(int) packageSize + 1000];
            DatagramPacket datagramPacket = new DatagramPacket(bytesPacket, (int) packageSize + 1000);
            int id;
            long packageCount = (long) Math.ceil((double) fileSize / packageSize);
            int packageReceived = 0;
            PriorityQueue<PartOfFile> partsBuffer = new PriorityQueue<>(512, Comparator.comparing(PartOfFile::getId));
            System.out.println("Package count " + packageCount);
            System.out.println(this.path + "/" + fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(this.path + "/" + fileName);
            int lastWrittenPackageId = -1;
            while (packageReceived < packageCount) {
                socket.receive(datagramPacket);

                bis = new ByteArrayInputStream(datagramPacket.getData());
                ois = new ObjectInputStream(bis);
                PartOfFile receivedPackage = (PartOfFile) ois.readObject();
                System.out.println("packet #" + receivedPackage.getId() +"is received");

                if (receivedPackage.getId() > lastWrittenPackageId) {
                    partsBuffer.add(receivedPackage);
                    DatagramPacket report = new DatagramPacket(IntToByte.convert(receivedPackage.getId()), 4, address);
                    socket.send(report);
                    packageReceived++;
                    while (!partsBuffer.isEmpty() && partsBuffer.peek().getId() == (lastWrittenPackageId + 1)) {
                        fileOutputStream.write(partsBuffer.remove().getByteData());
                        lastWrittenPackageId++;
                    }
                }
            }
            System.out.println("Received.");
            long finish = System.currentTimeMillis();
            System.out.println("Time went " +  (finish - start)/1000 + " sec");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
