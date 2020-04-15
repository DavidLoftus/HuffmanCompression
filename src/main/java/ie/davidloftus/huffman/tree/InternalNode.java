package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;
import ie.davidloftus.huffman.BitOutputStream;

import java.io.IOException;

class InternalNode implements TreeNode {
    private TreeNode left;
    private TreeNode right;

    public InternalNode(TreeNode left, TreeNode right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public int getNextWord(BitInputStream input) throws IOException {
        return input.read() ? left.getNextWord(input) : right.getNextWord(input);
    }

    @Override
    public void writeToFile(BitOutputStream outputStream) throws IOException {
        outputStream.write(true);
        left.writeToFile(outputStream);
        right.writeToFile(outputStream);
    }
}
