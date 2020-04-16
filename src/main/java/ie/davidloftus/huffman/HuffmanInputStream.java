package ie.davidloftus.huffman;

import ie.davidloftus.huffman.tree.HuffmanTree;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class HuffmanInputStream extends FilterInputStream {

    private BitInputStream bitInputStream;
    private HuffmanTree huffmanTree;

    protected HuffmanInputStream(InputStream in) throws IOException {
        super(in);

        this.bitInputStream = new BitInputStream(in);
        this.huffmanTree = HuffmanTree.readFromFile(bitInputStream);
    }

    @Override
    public int read() throws IOException {
        return huffmanTree.getNextWord(bitInputStream);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            try {
                b[off + i] = (byte) read();
            } catch(EOFException ignored) {
                return i == 0 ? -1 : i;
            }
        }
        return len;
    }
}
