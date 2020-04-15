package ie.davidloftus.huffman;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream {

    private InputStream inputStream;

    private int bitsInByteBuffer = 0;
    private int byteBuffer = 0;

    public BitInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public boolean read() throws IOException {
        if (bitsInByteBuffer == 0) {
            byteBuffer = inputStream.read();
            bitsInByteBuffer = 8;
        }
        boolean bit = (byteBuffer & 1) != 0;
        byteBuffer >>= 1;
        return bit;
    }

    public void flush() throws IOException {
        bitsInByteBuffer = byteBuffer = 0;
    }

}
