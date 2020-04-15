package ie.davidloftus.huffman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
        for (int i = 0; i < 8; ++i) {
            bitOutputStream.write(true);
        }
        verify(outputStream, times(1)).write(255);
    }

    @Test
    void flush() {
    }

    @Test
    void close() {
    }
}