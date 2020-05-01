package ie.davidloftus.huffman;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * BitInputStream allows you to read from any InputStream one bit at a time.
 *
 * In order to allow for a variable number of bits at the end (not a multiple of 8),
 * the final byte encodes the number of remaining bits.
 * This encoding is done by padding byte with a 1 bit and then the rest with 0 bits.
 * As a consequence if the number of bits in a message is a multiple of 8, then we actually have to add an extra
 * sentinel byte in order to indicate no more bits remain.
 */
public class BitInputStream {

    private InputStream inputStream;

    private int currentBit = -1;
    private int byteBuffer = 0;
    private int tailByte;

    public BitInputStream(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        this.tailByte = inputStream.read();
    }

    /**
     * nextByte is a private helper function that retries the next byte of bits from the input stream.
     * This function handles all the logic with the variable length tail byte, that was described above.
     * @return byteBuffer if there are any bits remaining otherwise -1
     * @throws IOException if any error occurs while reading from the input stream
     */
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

    /**
     * Reads a single bit from the input stream.
     * @return 1 or 0 if there is a bit remaining, otherwise -1
     * @throws IOException if any error occurs while reading from the input stream
     */
    public int readBit() throws IOException {
        if (currentBit == -1) {
            if (nextByte() == -1) {
                return -1;
            }
        }
        return (byte) (byteBuffer >> currentBit-- & 1);
    }

    /**
     * Reads a single
     * @return 1 or 0 if there is a bit remaining
     * @throws EOFException if there is no bit remaining
     * @throws IOException
     */
    public boolean read() throws IOException {
        int bit = readBit();
        if (bit == -1) {
            throw new EOFException();
        }
        return bit != 0;
    }

    /**
     * Flushes the bit input stream, by clearing the bits currently in the buffer.
     * This behaviour mirrors the effects of BitOutputStream::flush
     */
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

    /**
     * Reads an entire n-bit word from the byte byffer.
     * @param bits can be any integer from 0 to 64 inclusive. Do note that reading 64 bits might result in a negative number.
     * @return the word read if enough bits are remaining
     * @throws EOFException if not enough bits remain
     * @throws IOException if any error occurs while reading from the input stream
     */
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
