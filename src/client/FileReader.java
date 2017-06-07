package client;

import commonUtils.Channel;
import commonUtils.PartOfFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Виктория on 05.05.2017.
 */
public class FileReader implements Runnable {
    private Channel<PartOfFile> channel;
    private int packageSize;
    private FileInputStream fileInputStream;
    private CallBack callBackReader;

    public FileReader(FileInputStream fileInputStream, int packageSize, Channel<PartOfFile> channel, CallBack callBackReader) {
        this.packageSize = packageSize;
        this.channel = channel;
        this.fileInputStream = fileInputStream;
        this.callBackReader = callBackReader;
    }

    public Channel<PartOfFile> getChannel() {
        return this.channel;
    }

    @Override
    public void run() {
        int packageNumber = -1;
        byte bytes[] = new byte[packageSize];
        int bytesLength = 0;
        try {
            bytesLength = this.fileInputStream.read(bytes);
            while (bytesLength != -1) {
                packageNumber++;
                PartOfFile partOfFile;
                if (bytesLength == packageSize)
                    partOfFile = new PartOfFile(packageNumber, bytes);
                else
                    partOfFile = new PartOfFile(packageNumber, Arrays.copyOfRange(bytes, 0, bytesLength));
                channel.put(partOfFile);
                bytesLength = this.fileInputStream.read(bytes);
                System.out.println("Package #" + packageNumber + " is read. Channel size is " + channel.getSize());
            }
            System.out.println("File is read.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.callBackReader.onConfirm();
    }
}
