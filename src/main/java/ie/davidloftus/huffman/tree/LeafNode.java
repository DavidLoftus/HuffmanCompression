package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;
import ie.davidloftus.huffman.BitOutputStream;
import ie.davidloftus.huffman.BitString;

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

    @Override
    public void fillCodeBook(BitString[] codeBook, BitString prefix) {
        codeBook[word] = new BitString(prefix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeafNode leafNode = (LeafNode) o;
        return word == leafNode.word;
    }
}
