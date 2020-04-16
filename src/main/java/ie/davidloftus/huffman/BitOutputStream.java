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

    public void redirect(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void writeBit(int bit) throws IOException {
        unfilledByte = (unfilledByte << 1) | bit;
        bitsInUnfilledByte++;
        if (bitsInUnfilledByte == 8) {
            outputStream.write(unfilledByte);
            unfilledByte = bitsInUnfilledByte = 0;
        }
    }

    public void write(boolean bit) throws IOException {
        writeBit(bit ? 1 : 0);
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
    }

    public void writeWord(long word, int bits) throws IOException {
        assert bits <= 64;

        while (bitsInUnfilledByte != 0 && bits > 0) {
            bits--;
            writeBit((int) ((word >> bits) & 1));
        }

        while (bits >= 8) {
            outputStream.write((int) ((word >> (bits - 8)) & 0xff));
            bits -= 8;
        }

        while (bits > 0) {
            bits--;
            writeBit((int) ((word >> bits) & 1));
        }
    }

    public void write(BitString bits) throws IOException {
        int fullWords = bits.size() / BitString.BITS_PER_WORD;
        for (int i = 0; i < fullWords; ++i) {
            writeWord(bits.getWord(i), BitString.BITS_PER_WORD);
        }
        int remainingBits = bits.size() % BitString.BITS_PER_WORD;
        if (remainingBits != 0) {
            writeWord(bits.getWord(fullWords) >> (BitString.BITS_PER_WORD - remainingBits), remainingBits);
        }
    }
}
