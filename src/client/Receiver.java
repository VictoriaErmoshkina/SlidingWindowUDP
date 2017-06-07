package client;

import commonUtils.ByteToInt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Виктория on 05.05.2017.
 */
public class Receiver implements Runnable {
    DatagramSocket socket;
    int byteLengthInt = 4;
    boolean deliveryCheckList[];
    CallBackSender callBackSender;
    Object lock;

    public Receiver(DatagramSocket socket, boolean deliveryCheckList[], CallBackSender callBackSender) {
        this.socket = socket;
        this.deliveryCheckList = deliveryCheckList;
        this.callBackSender = callBackSender;

    }

    @Override
    public void run() {
        byte bytes[] = new byte[byteLengthInt];
        boolean isSent;
        DatagramPacket response = new DatagramPacket(bytes, byteLengthInt);
        try {
            while (!callBackSender.isConfirmed()) {
                socket.receive(response);
                int id = ByteToInt.convert(response.getData());
                if (id <= deliveryCheckList.length && id >= 0)
                    deliveryCheckList[id] = true;
                isSent = true;
                for (boolean i : deliveryCheckList) {
                    if (!i){
                        isSent = false;
                        break;
                    }
                }
                if (isSent)
                    callBackSender.onConfirm();
            }
            System.out.println("File is received.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
