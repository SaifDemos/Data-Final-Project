import java.util.Scanner;
import java.io.*;
import java.nio.file.*;

void main() {
    Scanner scanner = new Scanner(System.in);

    while (true) {
        System.out.println("\n===== Expression Processor =====");
        System.out.println("1. Enter expression (live input)");
        System.out.println("2. Read from input.txt");
        System.out.println("3. Exit");
        System.out.println("==================================");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        if (choice.equals("3")) {
            System.out.println("Goodbye!");
            break;
        }

        if (choice.equals("1")) {
            System.out.print("\nEnter expression: ");
            String expr = scanner.nextLine().trim();
            if (expr.isEmpty()) {
                System.out.println("Error: Empty expression");
                continue;
            }
            if (!ExpressionProcessor.isValidExpression(expr)) {
                System.out.println("Error: Invalid Expression");
                continue;
            }
            System.out.print("Enter hash table size: ");
            String sizeInput = scanner.nextLine().trim();
            int tableSize;
            try {
                tableSize = Integer.parseInt(sizeInput);
                if (tableSize <= 0) {
                    System.out.println("Error: Table size must be a positive integer");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid table size");
                continue;
            }
            try {
                System.out.println(ExpressionProcessor.processExpression(expr, tableSize));
            } catch (ArithmeticException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else if (choice.equals("2")) {
            System.out.print("Enter hash table size: ");
            String sizeInput = scanner.nextLine().trim();
            int tableSize;
            try {
                tableSize = Integer.parseInt(sizeInput);
                if (tableSize <= 0) {
                    System.out.println("Error: Table size must be a positive integer");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid table size");
                continue;
            }
            try {
                String content = Files.readString(Path.of("input.txt"));
                String[] lines = content.split("\n");
                StringBuilder fileOutput = new StringBuilder();
                boolean hasContent = false;

                for (String line : lines) {
                    String trimmed = line.trim();
                    if (trimmed.isEmpty()) continue;
                    hasContent = true;
                    try {
                        fileOutput.append(ExpressionProcessor.processExpression(trimmed, tableSize)).append("\n");
                    } catch (ArithmeticException e) {
                        fileOutput.append("Expression: ").append(trimmed).append("\n");
                        fileOutput.append("Output:\n");
                        fileOutput.append("Error: ").append(e.getMessage()).append("\n\n");
                    } catch (Exception e) {
                        fileOutput.append("Expression: ").append(trimmed).append("\n");
                        fileOutput.append("Output:\n");
                        fileOutput.append("Error: Invalid Expression\n\n");
                    }
                }

                if (!hasContent) {
                    System.out.println("input.txt is empty or contains no valid expressions.");
                } else {
                    Files.writeString(Path.of("output.txt"), fileOutput.toString().trim());
                    System.out.println("Processed expressions written to output.txt");
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: input.txt not found");
            } catch (IOException e) {
                System.out.println("Error: Could not read/write files");
            }
        } else {
            System.out.println("Invalid option. Try again.");
        }
    }

    scanner.close();
}
