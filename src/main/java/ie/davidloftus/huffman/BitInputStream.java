package ie.davidloftus.huffman;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream {

    private InputStream inputStream;

    private int currentBit = -1;
    private int byteBuffer = 0;

    public BitInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private int nextByte() throws IOException {
        byteBuffer = inputStream.read();
        if (byteBuffer == -1) {
            throw new EOFException();
        }
        currentBit = 7;
        return byteBuffer;
    }

    public byte readBit() throws IOException {
        if (currentBit == -1) {
            nextByte();
        }
        currentBit--;
        return (byte) (byteBuffer >> currentBit-- & 1);
    }

    public boolean read() throws IOException {
        return readBit() != 0;
    }

    public void flush() {
        currentBit = -1;
        byteBuffer = 0;
    }

    private long readFromBuffer(int bits) {
        assert bits <= currentBit+1;

        long ret = byteBuffer & (1 << bits - 1);
        currentBit -= bits;
        return ret;
    }

}
