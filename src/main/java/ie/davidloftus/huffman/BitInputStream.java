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

    public int readBit() throws IOException {
        if (currentBit == -1) {
            nextByte();
        }
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

        long ret = (byteBuffer >> (currentBit-bits+1)) & ((1 << bits) - 1);
        currentBit -= bits;
        return ret;
    }

    public long readWord(int bits) throws IOException {
        assert bits <= 64;

        if (bits <= currentBit+1) {
            return readFromBuffer(bits);
        }

        bits -= currentBit+1;
        long ret = readFromBuffer(currentBit+1);

        while (bits >= 8) {
            ret = ret << 8 | nextByte();
            bits -= 8;
        }

        flush();

        if (bits > 0) {
            nextByte();
            ret = (ret << bits) | readFromBuffer(bits);
        }

        return ret;
    }
}
