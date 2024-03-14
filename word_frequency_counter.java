import java.io.File;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.Arrays;
import java.io.IOException;

public class word_frequency_counter {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Insufficient command-line arguments.");
            System.exit(1); // Exit with an error code
        }
        System.out.println(args.toString());
        // Read keywords from console
        String[] keywords = Arrays.copyOfRange(args, 3, args.length);

        // Determine number of files in the folder
        String directoryPath = args[0] + "\\";
        File folder = new File(directoryPath);

        // Check if the folder exists
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid directory path.");
            System.exit(1); // Exit with an error code
        }

        String[] files = folder.list();

        // Check if files are found in the directory
        if (files == null || files.length == 0) {
            System.err.println("No files found in the specified directory.");
            System.exit(1); // Exit with an error code
        }

        // Run the program multiple times with different parameters
        for (int p = 2; p <= 16; p *= 2) {
            for (int t = 4; t <= 16; t *= 2) {
                runParallelWordFrequencyCalculator(directoryPath, files, keywords, p, t);
            }
        }
    }

    private static void runParallelWordFrequencyCalculator(String directoryPath, String[] files, String[] keywords,
            int parallelism, int taskSize) {
        // Create ForkJoinPool with specified parallelism
        ForkJoinPool pool = new ForkJoinPool(parallelism);

        // Print number of available cores for the tasks
        System.out.println("Available Cores: " + Runtime.getRuntime().availableProcessors());
        // Get the absolute path of the directory
        String absolutePath = Paths.get(directoryPath).toAbsolutePath().toString();

        System.out.println("Absolute Path: " + directoryPath);

        // Set a clock (start) for time computation for tasks
        long startTime = System.currentTimeMillis();

        // Create a SumTask object
        SumTask task = new SumTask(files, keywords, new int[keywords.length][files.length], 0, files.length - 1,
                taskSize, absolutePath);

        // Submit the SumTask to the ForkJoinPool for parallel execution
        int[][] frequencies = pool.invoke(task);

        // Print number of threads created for the task
        System.out.println("Number of Threads: " + parallelism);

        // Set a clock (end) time computation for tasks
        long endTime = System.currentTimeMillis();

        // Print the frequency of keywords
        for (int i = 0; i < frequencies.length; i++) {
            for (int j = 0; j < frequencies[i].length; j++) {
                System.out.println(
                        "Keyword: " + keywords[i] + ", File: " + files[j] + ", Frequency: " + frequencies[i][j]);
            }
        }

        // Print the total occurrences of keywords
        for (int i = 0; i < frequencies.length; i++) {
            int totalOccurrences = Arrays.stream(frequencies[i]).sum();
            System.out.println("Total Occurrences of '" + keywords[i] + "': " + totalOccurrences);
        }

        // Print the total runtime
        System.out.println("Total Runtime: " + (endTime - startTime) + " ms\n");

        pool.close();
    }
}

class SumTask extends RecursiveTask<int[][]> {
    private String[] files;
    private String[] keywords;
    private int[][] frequencies;
    private int start;
    private int end;
    private int taskSize;
    private String directoryPath;

    public SumTask(String[] files, String[] keywords, int[][] frequencies, int start, int end, int taskSize,
            String directoryPath) {
        this.files = files;
        this.keywords = keywords;
        this.frequencies = frequencies;
        this.start = start;
        this.end = end;
        this.taskSize = taskSize;
        this.directoryPath = directoryPath;
    }

    @Override
    protected int[][] compute() {
        // Base Case: If the number of files to process is less than or equal to
        // taskSize
        if (end - start + 1 <= taskSize) {
            for (int i = start; i <= end; i++) {
                // Read data from the assigned file(s) - files[i]
                Path filePath = Paths.get(directoryPath + "\\" + files[i]);

                if (Files.exists(filePath)) { // Check if the file exists
                    try {
                        String content = Files.readString(filePath);
                        for (int j = 0; j < keywords.length; j++) {
                            int frequency = countWordFrequency(content, keywords[j]);
                            frequencies[j][i] = frequency;
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading file: " + filePath);
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("File not found: " + filePath);
                }
            }
            return frequencies;
        } else {
            // Recursive Case
            int mid = start + (end - start) / 2;

            SumTask left = new SumTask(files, keywords, new int[keywords.length][files.length], start, mid, taskSize,
                    directoryPath);
            SumTask right = new SumTask(files, keywords, new int[keywords.length][files.length], mid + 1, end,
                    taskSize, directoryPath);

            left.fork();
            right.compute();
            left.join();

            // Combine results from left and right subtasks
            for (int i = 0; i < keywords.length; i++) {
                for (int j = 0; j < files.length; j++) {
                    frequencies[i][j] = left.frequencies[i][j] + right.frequencies[i][j];
                }
            }

            // Update frequencies array
            return frequencies;
        }
    }

    private static int countWordFrequency(String text, String word) {
        String[] words = text.split("\\s+");
        return (int) Arrays.stream(words)
                .filter(w -> w.equalsIgnoreCase(word))
                .count();
    }
}
