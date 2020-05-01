package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;
import ie.davidloftus.huffman.BitOutputStream;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TreeNodeTest {

    @Test
    void getNextWord() throws IOException {
        byte[] bytes = {-71}; // 10111001
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BitInputStream bitInputStream = new BitInputStream(byteArrayInputStream);

        TreeNode leafNodeA = new LeafNode('A');
        TreeNode leafNodeB = new LeafNode('B');

        assertEquals('A', leafNodeA.getNextWord(bitInputStream));
        assertEquals('B', leafNodeB.getNextWord(bitInputStream));

        TreeNode internalNodeAB = new InternalNode(leafNodeA, leafNodeB);

        assertEquals('A', internalNodeAB.getNextWord(bitInputStream));
        assertEquals('B', internalNodeAB.getNextWord(bitInputStream));

        TreeNode leafNodeC = new LeafNode('C');
        TreeNode internalNodeABC = new InternalNode(internalNodeAB, leafNodeC);

        assertEquals('A', internalNodeABC.getNextWord(bitInputStream));
        assertEquals('B', internalNodeABC.getNextWord(bitInputStream));
        assertEquals('C', internalNodeABC.getNextWord(bitInputStream));
    }

    @Test
    void writeToFile() throws IOException {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TreeNode leafNodeA = new LeafNode('A');

        try (BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream)) {
            leafNodeA.writeToFile(bitOutputStream);
        }

        assertArrayEquals(new byte[]{0x20, -64}, byteArrayOutputStream.toByteArray());

        TreeNode leafNodeB = new LeafNode('B');
        TreeNode internalNodeAB = new InternalNode(leafNodeA, leafNodeB);

        byteArrayOutputStream.reset();
        try (BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream)) {
            internalNodeAB.writeToFile(bitOutputStream);
        }

        assertArrayEquals(new byte[]{-112, 72, 80}, byteArrayOutputStream.toByteArray());

        TreeNode leafNodeC = new LeafNode('C');
        TreeNode internalNodeABC = new InternalNode(internalNodeAB, leafNodeC);

        byteArrayOutputStream.reset();
        try (BitOutputStream bitOutputStream = new BitOutputStream(byteArrayOutputStream)) {
            internalNodeABC.writeToFile(bitOutputStream);
        }

        assertArrayEquals(new byte[]{-56, 36, 34, 28}, byteArrayOutputStream.toByteArray());
    }

    @Test
    void readFromFile() throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{-56, 36, 34, 28});
        BitInputStream bitInputStream = new BitInputStream(inputStream);

        TreeNode leafNodeA = new LeafNode('A');
        TreeNode leafNodeB = new LeafNode('B');
        TreeNode internalNodeAB = new InternalNode(leafNodeA, leafNodeB);
        TreeNode leafNodeC = new LeafNode('C');
        TreeNode internalNodeABC = new InternalNode(internalNodeAB, leafNodeC);

        TreeNode nodeFromFile = TreeNode.readFromFile(bitInputStream);

        assertEquals(internalNodeABC, nodeFromFile);
    }
}