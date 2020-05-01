package ie.davidloftus.huffman;

import ie.davidloftus.huffman.tree.HuffmanTree;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * HuffmanOutputStream acts as a way to write data to an OutputStream but in an huffman encoded format.
 * Written will be buffered in order to create the optimal huffman tree.
 * Buffer is flushed either when the stream is closed, or the flush() method is called, after which subsequent calls to
 * write will be directly written.
 * This method is inspired from the GZIPOutputStream from the Java standard library.
 */
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
                bitOutputStream.write(huffmanTree.getBitsForWord(word & 0xff));
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
