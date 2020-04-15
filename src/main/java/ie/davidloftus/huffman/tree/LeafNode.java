package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;
import ie.davidloftus.huffman.BitOutputStream;

import java.io.IOException;

class LeafNode implements TreeNode {

    private int word;

    public LeafNode(int word) {
        this.word = word;
    }

    @Override
    public int getNextWord(BitInputStream input) {
        return word;
    }

    @Override
    public void writeToFile(BitOutputStream outputStream) throws IOException {
        outputStream.write(false);
        outputStream.writeWord(word, HuffmanTree.BITS_PER_WORD);
    }

}
