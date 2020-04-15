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

@ExtendWith(MockitoExtension.class)
class BitInputStreamTest {

    @Mock
    InputStream inputStream;

    private BitInputStream bitInputStream;

    @BeforeEach
    void setup() {
        this.bitInputStream = new BitInputStream(inputStream);
    }

    @Test
    void read() throws IOException {
        doReturn(255).when(inputStream).read();
        when(inputStream.read()).thenReturn(255);
        for (int i = 0; i < 8; ++i) {
            assertTrue(bitInputStream.read());
        }
        verify(inputStream, times(1)).read();
    }

    @Test
    void flush() throws IOException {
    }

    @Test
    void close() throws IOException {
    }
}