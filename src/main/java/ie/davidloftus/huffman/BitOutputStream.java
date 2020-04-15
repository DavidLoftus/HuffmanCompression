package ie.davidloftus.huffman;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream implements Closeable {

    private OutputStream outputStream;

    private int bitsInUnfilledByte = 0;
    private int unfilledByte = 0;

    public BitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeBit(byte bit) throws IOException {
        unfilledByte = (unfilledByte << 1) | bit;
        bitsInUnfilledByte++;
        if (bitsInUnfilledByte == 8) {
            outputStream.write(unfilledByte);
            unfilledByte = bitsInUnfilledByte = 0;
        }
    }

    public void write(boolean bit) throws IOException {
        writeBit((byte) (bit ? 1 : 0));
    }

    public void flush() throws IOException {
        if (bitsInUnfilledByte > 0) {
            outputStream.write(unfilledByte << (8 - bitsInUnfilledByte));
            unfilledByte = bitsInUnfilledByte = 0;
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        outputStream.close();
    }

    public void writeWord(long word, int bitsPerWord) throws IOException {
    }
}
