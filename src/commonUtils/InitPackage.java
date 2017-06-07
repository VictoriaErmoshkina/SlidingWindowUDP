package commonUtils;


import java.io.Serializable;

/**
 * Created by Виктория on 05.05.2017.
 */
public class InitPackage implements Serializable {
    long fileSize;
    String fileName;
    long packageSize;
    public InitPackage(long fileSize, String fileName, long packageSize){
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.packageSize = packageSize;
    }
    public long getFileSize(){
        return this.fileSize;
    }
    public long getPackageSize(){
        return this.packageSize;
    }
    public String getFileName(){
        return this.fileName;
    }

}
