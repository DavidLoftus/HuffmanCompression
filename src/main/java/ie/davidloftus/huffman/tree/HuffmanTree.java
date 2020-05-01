package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;
import ie.davidloftus.huffman.BitOutputStream;
import ie.davidloftus.huffman.BitString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * HuffmanTree holds the tree and codebook for encoding/decoding characters.
 *
 * Can be generated from data using generateFromInput() or read from a previously serialized file using readFromFile().
 */
public class HuffmanTree {

    public static final int BITS_PER_WORD = 8;

    private TreeNode rootNode;

    private BitString[] codeBook = new BitString[256];

    private HuffmanTree(TreeNode rootNode) {
        this.rootNode = rootNode;
        rootNode.fillCodeBook(codeBook);
    }

    /**
     * Reads a serialized HuffmanTree from a BitInputStream.
     * @param bitInputStream the bits to read from
     * @return a HuffmanTree object
     * @throws IOException if any IO error occurs
     */
    public static HuffmanTree readFromFile(BitInputStream bitInputStream) throws IOException {
        return new HuffmanTree(TreeNode.readFromFile(bitInputStream));
    }

    /**
     * Writes a serialized HuffmanTree to a BitOutputStream.
     * @param bitOutputStream the output stream to write the bits to
     * @throws IOException if any IO error occurs
     */
    public void writeToFile(BitOutputStream bitOutputStream) throws IOException {
        rootNode.writeToFile(bitOutputStream);
    }

    /**
     * Generates a new HuffmanTree that is optimal for a given input.
     * @param inputData the input to optimize the tree for
     * @return a HuffmanTree that will minimally encoded inputData
     */
    public static HuffmanTree generateFromInput(byte[] inputData) {
        if (inputData.length == 0) {
            return new HuffmanTree(new LeafNode(0));
        }

        class FrequencyTreePair implements Comparable<FrequencyTreePair> {
            TreeNode node;
            int frequency;

            public FrequencyTreePair(TreeNode node, int frequency) {
                this.node = node;
                this.frequency = frequency;
            }

            @Override
            public int compareTo(FrequencyTreePair frequencyPair) {
                return Integer.compare(this.frequency, frequencyPair.frequency);
            }
        }

        int[] frequencies = new int[256];
        for (byte inputByte : inputData) {
            frequencies[inputByte & 0xff]++;
        }


        List<FrequencyTreePair> nodes = new ArrayList<>();
        for (int i = 0; i < frequencies.length; ++i) {
            if (frequencies[i] > 0) {
                nodes.add(new FrequencyTreePair(new LeafNode(i), frequencies[i]));
            }
        }

        PriorityQueue<FrequencyTreePair> priorityQueue = new PriorityQueue<>(nodes);

        while (priorityQueue.size() >= 2) {
            FrequencyTreePair rightPair = priorityQueue.poll();
            FrequencyTreePair leftPair = priorityQueue.poll();
            assert leftPair != null;

            leftPair.frequency += rightPair.frequency;
            leftPair.node = new InternalNode(leftPair.node, rightPair.node);

            priorityQueue.add(leftPair);
        }

        assert !priorityQueue.isEmpty();
        TreeNode node = priorityQueue.poll().node;

        if (node instanceof LeafNode) {
            // Can't have LeafNode as root, no zero length encoding allowed.
            node = new InternalNode(node, node);
        }

        return new HuffmanTree(node);
    }

    /**
     * Get next whole character from bitstream.
     * @param inputStream bitstream to read from
     * @return a single byte as an int
     * @throws IOException if an io error occurs while reading
     * @throws java.io.EOFException if there aren't enough bits
     */
    public int getNextWord(BitInputStream inputStream) throws IOException {
        return rootNode.getNextWord(inputStream);
    }

    /**
     * Get the bitstring that represents a given byte.
     * @param b the byte to encode
     * @return a bitstring that would decode to b
     */
    public BitString getBitsForWord(int b) {
        return codeBook[b];
    }
}
