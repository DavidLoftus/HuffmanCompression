package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;

import java.io.IOException;

interface TreeNode {
    int getNextWord(BitInputStream input) throws IOException;

    static TreeNode fromFile(BitInputStream inputStream) throws IOException {
        boolean isInternal = inputStream.read();
        if (isInternal) {
            TreeNode left = fromFile(inputStream);
            TreeNode right = fromFile(inputStream);
            return new InternalNode(left, right);
        } else {
            int val = (int)inputStream.readWord(HuffmanTree.BITS_PER_WORD);
            return new LeafNode(val);
        }
    }

}
