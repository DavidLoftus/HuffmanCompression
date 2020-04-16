package ie.davidloftus.huffman;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class BitInputStream {

    private InputStream inputStream;

    private int currentBit = -1;
    private int byteBuffer = 0;
    private int tailByte;

    public BitInputStream(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.tailByte = inputStream.read();
    }

    private int nextByte() throws IOException {
        if (tailByte == -1) {
            return -1;
        }
        byteBuffer = tailByte;
        currentBit = 7;
        tailByte = inputStream.read();
        if (tailByte == -1) {
            // byteBuffer is last byte in stream
            // this byte includes the number of bits in the byte
            for (currentBit = 6; currentBit >= 0 && (byteBuffer & 1) == 0; --currentBit) {
                byteBuffer >>>= 1;
            }
            byteBuffer >>>= 1;
            if (currentBit == -1) {
                return -1;
            }
        }
        return byteBuffer;
    }

    public int readBit() throws IOException {
        if (currentBit == -1) {
            if (nextByte() == -1) {
                return -1;
            }
        }
        return (byte) (byteBuffer >> currentBit-- & 1);
    }

    public boolean read() throws IOException {
        int bit = readBit();
        if (bit == -1) {
            throw new EOFException();
        }
        return bit != 0;
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
            int nextByte = nextByte();
            if (nextByte == -1) {
                throw new EOFException();
            }
            ret = ret << 8 | nextByte;
            bits -= 8;
        }

        flush();

        if (bits > 0) {
            if (nextByte() == -1) {
                throw new EOFException();
            }
            ret = (ret << bits) | readFromBuffer(bits);
        }

        return ret;
    }
}
