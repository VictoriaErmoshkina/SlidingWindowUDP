package commonUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by Виктория on 19.05.2017.
 */
public class PartOfFile implements Serializable {
    private int id;
    private byte[] bytesPackage;
    private int length;

    public PartOfFile(int id, byte[] bytes) {
        this.id = id;
        ByteBuffer buffer = ByteBuffer.allocate(4 + bytes.length);
        buffer.putInt(id);
        buffer.put(bytes);
        this.bytesPackage = buffer.array();
        this.length = this.bytesPackage.length;
    }

    public byte[] getByteData() {
        return Arrays.copyOfRange(this.getBytesPackage(), 4, this.getBytesPackage().length);
    }

    public int getId() {
        return this.id;
    }

    public byte[] getBytesPackage() {
        return bytesPackage;
    }

    public int getLength() {
        return this.length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof PartOfFile))
            return false;
        PartOfFile partOfFile = (PartOfFile) obj;
        return partOfFile.id == this.id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

}
