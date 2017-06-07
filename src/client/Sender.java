package client;

import commonUtils.Channel;
import commonUtils.PartOfFile;
import commonUtils.RingBuffer;
import commonUtils.TimeOut;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by Виктория on 05.05.2017.
 */
public class Sender implements Runnable {
    private Channel<PartOfFile> channel;
    private RingBuffer<PartOfFile> slidingWindow;
    private int windowSize = Client.getMaxCountInChannel() / 3;
    private CallBack callBackReader;
    private CallBack callBackSender;
    private DatagramSocket socket;
    private InetSocketAddress address;
    private int packageCount;
    private boolean deliveryCheckList[];
    private int timeOut = 5000;

    public Sender(int packageCount, DatagramSocket socket, InetSocketAddress address, CallBack callBackSender, CallBack callBackReader, Channel<PartOfFile> channel,
                  boolean deliveryCheckList[]) {
        this.socket = socket;
        this.address = address;
        this.callBackReader = callBackReader;
        this.callBackSender = callBackSender;
        this.channel = channel;
        PartOfFile[] packets = new PartOfFile[windowSize + 1];
        this.slidingWindow = new RingBuffer<PartOfFile>(packets);
        this.packageCount = packageCount;
        this.deliveryCheckList = deliveryCheckList;
    }

    public void send(PartOfFile packet) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
        objectOutputStream.writeObject(packet);
        byte[] bytes = byteOutputStream.toByteArray();
        DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, this.address);
        this.socket.send(datagramPacket);
    }


    @Override
    public void run() {
        PartOfFile partOfFile;
        int sentPackages = 0;
        try {
            while (sentPackages < this.packageCount) {
                if (slidingWindow.size() != 0) {
                    partOfFile = slidingWindow.getHead();
                    while (partOfFile != null && deliveryCheckList[partOfFile.getId()]) {
                        slidingWindow.take();
                        //System.out.println("part #" + partOfFile.getId() + "is taken from sliding window");
                        partOfFile = slidingWindow.getHead();

                    }
                }
                while (slidingWindow.size() < slidingWindow.capacity()) {
                    if (this.channel.getSize() != 0 && !callBackSender.isConfirmed()) {
                        PartOfFile pack = this.channel.take();
                        this.send(pack);
                        sentPackages++;
                        TimeOut timeOut = new TimeOut(this.timeOut, this, deliveryCheckList, pack);
                        //System.out.println("Try to put pack #" + pack.getId() + " into sliding window");
                        slidingWindow.put(pack);
                        //System.out.println("packet #" + pack.getId() + " is sent.");
                    } else
                        break;
                }
            }
            System.out.println("All parts are sent");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
