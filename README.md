# Java Word Frequency Counter

This is a Java program that calculates the frequency of specified keywords in a set of text files. It utilizes parallel processing with the ForkJoin framework to improve performance by distributing the workload across multiple threads.

## Features

- **Parallel Processing**: Utilizes ForkJoin framework to process multiple files concurrently, improving performance.
- **Customizable Parallelism**: Allows adjusting the number of threads and task sizes to optimize performance based on available system resources.
- **Keyword Frequency Calculation**: Counts the occurrences of specified keywords in text files.
- **Command-Line Interface**: Accepts input parameters via command-line arguments.

## Usage

1. **Compilation**: Compile the Java source file (`word_frequency_counter.java`) using the `javac` command:

    ```
    javac word_frequency_counter.java
    ```

2. **Create JAR File**: Create a JAR file containing the compiled classes:

    ```
    jar cvfm word_frequency_counter.jar Manifest.txt *.class
    ```

3. **Run the Program**: Execute the JAR file with the appropriate command-line arguments:

    ```
    java -jar word_frequency_counter.jar <directory_path> <keyword1> <keyword2> ...
    ```

    Replace `<directory_path>` with the path to the directory containing the text files, and `<keyword1>`, `<keyword2>`, etc., with the keywords you want to search for.

## Example

Suppose you have a directory `texts` containing several text files (`file1.txt`, `file2.txt`, etc.). To count the frequency of the words "java" and "programming" in these files, you would run:

```
java -jar word_frequency_counter.jar Dataset2 4 4 the and
```

## Contributing

Contributions are welcome! If you find any bugs or have suggestions for improvements, feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
