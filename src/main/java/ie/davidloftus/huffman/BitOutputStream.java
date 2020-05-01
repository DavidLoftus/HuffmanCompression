package ie.davidloftus.huffman;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

/**
 * BitOutputStream allows you to write to any OutputStream one bit at a time.
 *
 * In order to allow for a variable number of bits at the end (not a multiple of 8),
 * the final byte encodes the number of remaining bits.
 * This encoding is done by padding byte with a 1 bit and then the rest with 0 bits.
 * As a consequence if the number of bits in a message is a multiple of 8, then we actually have to add an extra
 * sentinel byte in order to indicate no more bits remain.
 *
 * Note that this class implements Closeable.
 * Failing to close this object could result in the last few bits not being written.
 *
 */
public class BitOutputStream implements Closeable {

    private OutputStream outputStream;

    private int bitsInUnfilledByte = 0;
    private int unfilledByte = 0;

    public BitOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    /**
     * Writes a bit to the outputStream
     * @param bit the bit to write, can be 0 or 1
     * @throws IOException if any errors occur while writing to outputStream
     */
    public void writeBit(int bit) throws IOException {
        unfilledByte = (unfilledByte << 1) | bit;
        bitsInUnfilledByte++;
        if (bitsInUnfilledByte == 8) {
            outputStream.write(unfilledByte);
            unfilledByte = bitsInUnfilledByte = 0;
        }
    }

    /**
     * Writes a bit given a boolean value
     * @param bit true for 1, false for 0
     * @throws IOException if any errors occur while writing to outputStream
     */
    public void write(boolean bit) throws IOException {
        writeBit(bit ? 1 : 0);
    }

    /**
     * Flushes the input stream and performs the tail encoding described above.
     * @throws IOException if any errors occur while writing to outputStream
     */
    protected void flush() throws IOException {
        writeBit(1);
        while (bitsInUnfilledByte != 0) {
            writeBit(0);
        }
    }

    /**
     * Closes the BitInputStream by flushing remaining bits
     * Does not close the underlying InputStream
     * @throws IOException if any errors occur while writing to outputStream
     */
    @Override
    public void close() throws IOException {
        flush();
    }

    /**
     * Writes an n-bit word to the outputStream.
     * @param word the word to write
     * @param bits the number of bits in word
     * @throws IOException if any errors occur while writing to outputStream
     */
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

    /**
     * Writes an entire BitString to the outputStream.
     * @param bits the bitstring to write.
     * @throws IOException if any errors occur while writing to outputStream
     */
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
