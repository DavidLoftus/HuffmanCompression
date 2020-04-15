package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;

class LeafNode implements TreeNode {

    private int word;

    public LeafNode(int word) {
        this.word = word;
    }

    @Override
    public int getNextWord(BitInputStream input) {
        return word;
    }

}
