package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;

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
}
