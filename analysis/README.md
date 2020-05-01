# Algorithm Analysis

### Task 1

Here is a hand drawn huffman tree used to minimize the encoding for the string "There is no place like home"

As you can see I have written the accumulated frequencies at each node in the tree.

![Huffman Tree Drawing](huffman_tree.jpg)

You can see the character mapping in the photo but for clarity I will rewrite it here:

| Character | Bit Encoding |
|-----------|--------------|
| _         | 11           |
| E         | 10           |
| L         | 0111         |
| O         | 0110         |
| I         | 0101         |
| H         | 0100         |
| M         | 00111        |
| K         | 001101       |
| C         | 001100       |
| A         | 00101        |
| P         | 00100        |
| N         | 00011        |
| S         | 00010        |
| R         | 00001        |
| T         | 00000        |

Thus, "There is no place like home" would be encoded as
```
T 00000
H 0100
E 10
R 00001
E 10
  11
I 0101
S 00010
  11
N 00011
O 0110
  11
P 00100
L 0111
A 00101
C 001100
E 10
  11
H 0100
O 0110
M 00111
E 10
```

joined together this is
`000000100100000110110101000101100011011011001000111001010011001011010001100011110`

### Task 2

Task 2 was to write the Huffman encoding algorithm, you can find the code in the
[ie.davidloftus.huffman](../src/main/java/ie/davidloftus/huffman)
package.

I made a HuffmanInputStream and HuffmanOutputStream inspired by the GZIPInputStream and GZIPOutputStream in the Java
standard library. These classes can be used like regular input/output streams however when reading/writing they will
convert to/from the huffman encoding.

One issue with this method is that while one might expect by the interface to be able to stream data through these
data types without incurring large memory usage. This is only true for HuffmanInputStream. HuffmanOutputStream needs to
buffer all the data until the stream is closed so that it can build the optimal huffman tree. To fix this issue I added
the ability to flush the HuffmanOutputStream. While this likely ends up with a non optimal huffman tree, it does
reduce the memory usage when compressing large amounts of data.