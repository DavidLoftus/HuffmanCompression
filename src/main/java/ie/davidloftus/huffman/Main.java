package ie.davidloftus.huffman;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Spliterator;

public class Main {

    public static void printUsage(PrintStream printStream) {
        printStream.println("Usage: <command> [arguments]");
        printStream.println("Commands:");
        printStream.println("\tcompress <INPUT_FILE> [OUTPUT_FILE]");
        printStream.println("\tdecompress <INPUT_FILE> [OUTPUT_FILE]");
    }

    public static void compressFile(String inputPath, String outputPath) {
        long before = System.nanoTime();

        try (OutputStream outputStream = new HuffmanOutputStream(new FileOutputStream(outputPath))) {
            Files.copy(Paths.get(inputPath), outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        long nanos = System.nanoTime() - before;

        System.out.printf("compressFile(\"%s\", \"%s\") took %.3fms\n", inputPath, outputPath, nanos / 1e6);
    }

    public static void compressFile(String inputPath) {
        compressFile(inputPath, inputPath + ".hf");
    }

    public static void decompressFile(String inputPath, String outputPath) {
        long before = System.nanoTime();

        try (InputStream inputStream = new HuffmanInputStream(new FileInputStream(inputPath))) {
            Files.copy(inputStream, Paths.get(outputPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        long nanos = System.nanoTime() - before;

        System.out.printf("decompressFile(\"%s\", \"%s\") took %.3fms\n", inputPath, outputPath, nanos / 1e6);
    }

    public static void decompressFile(String inputPath) {
        if (inputPath.endsWith(".hf")) {
            decompressFile(inputPath, inputPath.substring(0, inputPath.length() - 3));
        } else {
            System.err.println("Error: can't deduce output path from non standard input: " + inputPath);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Spliterator<String> it = Arrays.spliterator(args);

        if (!it.tryAdvance(command -> {
            switch (command.toLowerCase()) {
                case "help":
                    printUsage(System.out);
                    break;
                case "compress":
                    if (!it.tryAdvance(inputPath -> {
                        if (!it.tryAdvance(outputPath -> compressFile(inputPath, outputPath))) {
                            compressFile(inputPath);
                        }
                    })) {
                        System.err.println("Error: no input file provided.");
                        System.exit(1);
                    }
                    break;
                case "decompress":
                    if (!it.tryAdvance(inputPath -> {
                        if (!it.tryAdvance(outputPath -> decompressFile(inputPath, outputPath))) {
                            decompressFile(inputPath);
                        }
                    })) {
                        System.err.println("Error: no input file provided.");
                        System.exit(1);
                    }
                    break;
                default:
                    System.err.println("Error: no command provided.");
                    printUsage(System.err);
                    System.exit(1);
            }
        })) {
            printUsage(System.err);
            System.exit(1);
        }

    }

}
