package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;
import ie.davidloftus.huffman.BitOutputStream;
import ie.davidloftus.huffman.BitString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
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
        BitOutputStream bitOutputStream = new BitOutputStream(outputStream);
        writeToFile(bitOutputStream);
        bitOutputStream.flush();
    }

    public void writeToFile(BitOutputStream bitOutputStream) throws IOException {
        rootNode.writeToFile(bitOutputStream);
    }

    public static HuffmanTree generateFromInput(byte[] inputData) {
        class FrequencyTreePair implements Comparable<FrequencyTreePair> {
            TreeNode node;
            int frequency = 0;

            public FrequencyTreePair(TreeNode node) {
                this.node = node;
            }

            @Override
            public int compareTo(FrequencyTreePair frequencyPair) {
                return Integer.compare(this.frequency, frequencyPair.frequency);
            }
        }

        FrequencyTreePair[] nodes = new FrequencyTreePair[256];
        for (int i = 0; i < 256; i++) {
            nodes[i] = new FrequencyTreePair(new LeafNode(i));
        }

        for (byte inputByte : inputData) {
            nodes[inputByte & 0xff].frequency++;
        }

        PriorityQueue<FrequencyTreePair> priorityQueue = new PriorityQueue<>(Arrays.asList(nodes));

        while (priorityQueue.size() >= 2) {
            FrequencyTreePair rightPair = priorityQueue.poll();
            FrequencyTreePair leftPair = priorityQueue.poll();
            assert leftPair != null;

            leftPair.frequency += rightPair.frequency;
            leftPair.node = new InternalNode(leftPair.node, rightPair.node);

            priorityQueue.add(leftPair);
        }

        assert !priorityQueue.isEmpty();
        return new HuffmanTree(priorityQueue.poll().node);
    }

    public int getNextWord(BitInputStream inputStream) throws IOException {
        return rootNode.getNextWord(inputStream);
    }

    public BitString getBitsForWord(int b) {
        return codeBook[b];
    }
}
