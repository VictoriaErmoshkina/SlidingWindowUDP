package commonUtils;

import java.nio.ByteBuffer;

/**
 * Created by Виктория on 05.05.2017.
 */
public class ByteToInt {
    public static int convert(byte[] bytes){
        return ByteBuffer.wrap(bytes).getInt();
    }
}
