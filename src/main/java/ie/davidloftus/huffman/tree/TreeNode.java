package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;
import ie.davidloftus.huffman.BitOutputStream;
import ie.davidloftus.huffman.BitString;

import java.io.IOException;

interface TreeNode {
    int getNextWord(BitInputStream input) throws IOException;

    void writeToFile(BitOutputStream outputStream) throws IOException;

    void fillCodeBook(BitString[] codeBook, BitString prefix);
    default void fillCodeBook(BitString[] codeBook) {
        fillCodeBook(codeBook, new BitString());
    }

    static TreeNode readFromFile(BitInputStream inputStream) throws IOException {
        boolean isInternal = inputStream.read();
        if (isInternal) {
            TreeNode left = readFromFile(inputStream);
            TreeNode right = readFromFile(inputStream);
            return new InternalNode(left, right);
        } else {
            int val = (int)inputStream.readWord(HuffmanTree.BITS_PER_WORD);
            return new LeafNode(val);
        }
    }

}
