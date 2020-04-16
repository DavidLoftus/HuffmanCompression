package ie.davidloftus.huffman.tree;

import ie.davidloftus.huffman.BitInputStream;
import ie.davidloftus.huffman.BitOutputStream;
import ie.davidloftus.huffman.BitString;

import java.io.IOException;
import java.util.Objects;

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

    @Override
    public void fillCodeBook(BitString[] codeBook, BitString prefix) {
        prefix.add(0);
        right.fillCodeBook(codeBook, prefix);
        prefix.removeLast();

        prefix.add(1);
        left.fillCodeBook(codeBook, prefix);
        prefix.removeLast();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InternalNode that = (InternalNode) o;
        return Objects.equals(left, that.left) &&
                Objects.equals(right, that.right);
    }
}
