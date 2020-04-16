package ie.davidloftus.huffman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
@ExtendWith(MockitoExtension.class)
class BitInputStreamTest {

    @Mock
    InputStream inputStream;

    @Test
    void read() throws IOException {
        int[] bytes = {0xFF, 0x55, 0x96, 0x00, 0x80};
        int[] bits = {1,1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,0,0,1,0,1,1,0,0,0,0,0,0,0,0,0};

        doReturn(bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], -1).when(inputStream).read();

        BitInputStream bitInputStream = new BitInputStream(inputStream);

        for (int i = 0; i < 32; ++i) {
            assertEquals(bits[i]==1, bitInputStream.read());
        }
        verify(inputStream, times(bytes.length)).read();
    }

    @Test
    void readBit() throws IOException {
        int[] bytes = {0xFF, 0x55, 0x96, 0x00, 0x80};
        int[] bits = {1,1,1,1,1,1,1,1,0,1,0,1,0,1,0,1,1,0,0,1,0,1,1,0,0,0,0,0,0,0,0,0};

        doReturn(bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], -1).when(inputStream).read();

        BitInputStream bitInputStream = new BitInputStream(inputStream);

        for (int i = 0; i < 32; ++i) {
            assertEquals(bits[i], bitInputStream.readBit());
        }
        verify(inputStream, times(bytes.length)).read();
    }

    @Test
    void flush() throws IOException {
    }

    @Test
    void close() throws IOException {
    }

    @Test
    void readWord() throws IOException {
        int[] bytes = {0xFF, 0x60};

        doReturn(bytes[0], bytes[1], -1).when(inputStream).read();

        BitInputStream bitInputStream = new BitInputStream(inputStream);

        assertEquals(31, bitInputStream.readWord(5));
        verify(inputStream, times(2)).read();

        assertEquals(29, bitInputStream.readWord(5));
        verify(inputStream, times(3)).read();

    }
}