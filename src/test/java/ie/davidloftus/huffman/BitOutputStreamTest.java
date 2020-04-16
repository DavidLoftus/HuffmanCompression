package ie.davidloftus.huffman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BitOutputStreamTest {

    @Mock
    OutputStream outputStream;

    private BitOutputStream bitOutputStream;

    @BeforeEach
    void setup() {
        this.bitOutputStream = new BitOutputStream(outputStream);
    }

    @Test
    void write() throws IOException {
        int[] bytes = {0xFF, 0x55, 0x96, 0x00};
        byte[] bits = {1,1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,0,0,1,0,1,1,0,0,0,0,0,0,0,0,0};

        for (byte bit : bits) {
            bitOutputStream.write(bit == 1);
        }
        verify(outputStream).write(bytes[0]);
        verify(outputStream).write(bytes[1]);
        verify(outputStream).write(bytes[2]);
        verify(outputStream).write(bytes[3]);
    }

    @Test
    void writeBit() throws IOException {
        int[] bytes = {0xFF, 0x55, 0x96, 0x00};
        byte[] bits = {1,1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,0,0,1,0,1,1,0,0,0,0,0,0,0,0,0};

        for (byte bit : bits) {
            bitOutputStream.writeBit(bit);
        }
        verify(outputStream).write(bytes[0]);
        verify(outputStream).write(bytes[1]);
        verify(outputStream).write(bytes[2]);
        verify(outputStream).write(bytes[3]);
    }

    @Test
    void flush() {
    }

    @Test
    void close() {
    }

    @Test
    void writeWord() throws IOException {
        bitOutputStream.writeWord(31, 5);
        bitOutputStream.writeWord(29, 5);
        bitOutputStream.flush();

        verify(outputStream).write(0xFF);
        verify(outputStream).write(0x60);
    }
}