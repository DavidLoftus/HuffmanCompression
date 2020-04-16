package ie.davidloftus.huffman;

import java.util.Arrays;

public class BitString {

    public static final int BITS_PER_WORD = 64;

    private long[] bitWords;
    private int size;

    public BitString() {
        this(new long[1], 0);
    }

    public BitString(BitString other) {
        this(Arrays.copyOf(other.bitWords, (int)Math.ceil(other.size / (float)BITS_PER_WORD)), other.size);
    }

    public BitString(long[] bitWords, int size) {
        this.bitWords = bitWords;
        this.size = size;
    }

    public int size() {
        return size;
    }

    private static int getBitFromWord(long word, int i) {
        return (int) ((word >> i) & 1);
    }

    private static int setBitInWord(long word, int i, int val) {
        long mask = ~(1 << i);
        long bit = (long)val << i;
        return (int) ((word & mask) | bit);
    }

    public int getBit(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException();
        }
        int wordIdx = i / BITS_PER_WORD, bitIdx = i % BITS_PER_WORD;
        return getBitFromWord(bitWords[wordIdx], bitIdx);
    }

    public void setBit(int i, int val) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (val != 0 && val != 1) {
            throw new IllegalArgumentException("Non binary integer " + val + " given.");
        }
        int wordIdx = i / BITS_PER_WORD, bitIdx = i % BITS_PER_WORD;
        bitWords[wordIdx] = setBitInWord(bitWords[wordIdx], bitIdx, val);
    }

    private void growToFit(int length) {
        if (length > bitWords.length * BITS_PER_WORD) {
            bitWords = Arrays.copyOf(bitWords, (int)Math.ceil(length / (float) BITS_PER_WORD));
        }
    }

    public void add(int val) {
        if (val != 0 && val != 1) {
            throw new IllegalArgumentException("Non binary integer " + val + " given.");
        }
        growToFit(size+1);
        size++;
        setBit(size-1, val);
    }

    public void removeLast() {
        setBit(size-1, 0);
        size--;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("b\"");
        for (int i = 0; i < size; ++i) {
            sb.append(getBit(i));
        }
        sb.append('"');
        return sb.toString();
    }

    public long getWord(int i) {
        return bitWords[i];
    }
}
