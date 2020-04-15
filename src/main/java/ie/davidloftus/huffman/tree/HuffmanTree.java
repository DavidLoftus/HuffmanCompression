package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.PriorityQueue;

public class HuffmanTree {

    public static final int BITS_PER_WORD = 8;

    private TreeNode rootNode;

    private HuffmanTree(TreeNode rootNode) {
        this.rootNode = rootNode;
    }

    public static HuffmanTree fromFile(InputStream inputStream) throws IOException {
        BitInputStream bitInputStream = new BitInputStream(inputStream);

        HuffmanTree huffmanTree = new HuffmanTree(TreeNode.fromFile(bitInputStream));

        bitInputStream.flush();
        return huffmanTree;
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

}
