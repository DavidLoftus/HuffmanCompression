package ie.davidloftus.huffman;

import ie.davidloftus.huffman.tree.HuffmanTree;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class HuffmanOutputStream extends FilterOutputStream {

    private ByteArrayOutputStream byteBufferStream = new ByteArrayOutputStream();
    private BitOutputStream bitOutputStream;

    HuffmanTree huffmanTree = null;

    public HuffmanOutputStream(OutputStream out) {
        super(out);
        this.bitOutputStream = new BitOutputStream(out);
    }

    @Override
    public void write(int b) throws IOException {
        if (huffmanTree != null) {
            BitString bits = huffmanTree.getBitsForWord(b);
            bitOutputStream.write(bits);
        } else {
            byteBufferStream.write(b);
        }
    }

    @Override
    public void flush() throws IOException {
        if (huffmanTree == null) {
            // Generate huffman tree and flush all bytes
            byte[] data = byteBufferStream.toByteArray();

            this.huffmanTree = HuffmanTree.generateFromInput(data);
            huffmanTree.writeToFile(bitOutputStream);

            for (byte word : data) {
                bitOutputStream.write(huffmanTree.getBitsForWord(word));
            }

            byteBufferStream = null;
        }
        super.flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        bitOutputStream.close();
        super.close();
    }
}
