package commonUtils;

import client.Sender;

import java.io.IOException;

/**
 * Created by Виктория on 06.06.2017.
 */
public class TimeOut {
    private int timeOut;
    Sender sender;
    PartOfFile partOfFile;
    boolean deliveryCheckList[];

    public TimeOut(int timeOut, Sender sender, boolean deliveryCheckList[], PartOfFile partOfFile) {
        this.timeOut = timeOut;
        this.sender = sender;
        this.deliveryCheckList = deliveryCheckList;
        this.partOfFile = partOfFile;
        new Thread(() -> {
            while (!this.deliveryCheckList[this.partOfFile.getId()]) {
                try {
                    Thread.sleep(this.timeOut);
                    if (!this.deliveryCheckList[this.partOfFile.getId()])
                        this.sender.send(this.partOfFile);
                    //System.out.println("Packet #"+this.partOfFile.getId()+" is resent.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                }
            }
           //System.out.println("Packet #" + partOfFile.getId() + " is delivered.");
        }).start();
    }


}
