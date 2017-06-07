package commonUtils;

import java.nio.ByteBuffer;

/**
 * Created by Виктория on 07.06.2017.
 */
public class IntToByte {
    public static byte[] convert(int num){
        byte b[] = new byte[4];
        return ByteBuffer.wrap(b).putInt(num).array();
    }
}
