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

public class HuffmanTree {

    public static final int BITS_PER_WORD = 8;

    private TreeNode rootNode;

    private BitString[] codeBook = new BitString[256];

    private HuffmanTree(TreeNode rootNode) {
        this.rootNode = rootNode;
        rootNode.fillCodeBook(codeBook);
    }

    public static HuffmanTree readFromFile(InputStream inputStream) throws IOException {
        BitInputStream bitInputStream = new BitInputStream(inputStream);
        HuffmanTree huffmanTree = readFromFile(bitInputStream);

        bitInputStream.flush();

        return huffmanTree;
    }

    public static HuffmanTree readFromFile(BitInputStream bitInputStream) throws IOException {
        return new HuffmanTree(TreeNode.readFromFile(bitInputStream));
    }

    public void writeToFile(OutputStream outputStream) throws IOException {
        try (BitOutputStream bitOutputStream = new BitOutputStream(outputStream)) {
            writeToFile(bitOutputStream);
        }
    }

    public void writeToFile(BitOutputStream bitOutputStream) throws IOException {
        rootNode.writeToFile(bitOutputStream);
    }

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

    public int getNextWord(BitInputStream inputStream) throws IOException {
        return rootNode.getNextWord(inputStream);
    }

    public BitString getBitsForWord(int b) {
        return codeBook[b];
    }
}
