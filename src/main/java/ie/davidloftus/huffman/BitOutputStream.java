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

    public void write(boolean bit) throws IOException {
        if (bit) {
            unfilledByte |= 1 << bitsInUnfilledByte;
        }
        bitsInUnfilledByte++;
        if (bitsInUnfilledByte == 8) {
            outputStream.write(unfilledByte);
            unfilledByte = bitsInUnfilledByte = 0;
        }
    }

    public void flush() throws IOException {
        if (bitsInUnfilledByte > 0) {
            outputStream.write(unfilledByte);
            unfilledByte = bitsInUnfilledByte = 0;
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        outputStream.close();
    }
}
