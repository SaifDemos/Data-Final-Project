public class ExpressionProcessor {

    // STEP 1: Tokenization
    public static Queue<String> tokenize(String expr) {
        Queue<String> queue = new Queue<>();
        expr = expr.replaceAll("\\s+", ""); // Remove spaces
        StringBuilder numBuffer = new StringBuilder(); // Temporary multi-digit number
        boolean expectSign = true; // Flag for negative numbers

        // Read the full string
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            // If the number isDigit or decimal add to buffer to be added together
            if (Character.isDigit(c) || c == '.') {
                numBuffer.append(c);
                expectSign = false;

                // If the number is negative
            } else if (c == '-' && expectSign) {
                numBuffer.append(c);
                expectSign = false;

                // If it's an operation
            } else {
                if (!numBuffer.isEmpty()) {
                    queue.add(numBuffer.toString()); // Add the number in the buffer to the queue
                    numBuffer.setLength(0); // Empty the buffer
                }
                queue.add(String.valueOf(c)); // add the operation to the queue
                expectSign = (c == '(' || isOperator(String.valueOf(c))); // Set the expectSign to true if "(" or digit
            }
        }
        if (!numBuffer.isEmpty())
            queue.add(numBuffer.toString()); // Add the last number
        return queue;
    }

    // STEP 2: Infix to Postfix
    public static Queue<String> infixToPostfix(Queue<String> infixQueue) {
        Queue<String> postfixQueue = new Queue<>();
        Stack<String> operatorStack = new Stack<>();

        while (!infixQueue.isEmpty()) {
            String token = infixQueue.poll();

            // If it's a number add it to the queue
            if (isNumeric(token)) {
                postfixQueue.add(token);

                // If it's "(" push it to the stack
            } else if (token.equals("(")) {
                operatorStack.push(token);

                // If it's ")" add all the operator between the "(...)" into the queue
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    postfixQueue.add(operatorStack.pop());
                }
                operatorStack.pop();

                // If it's an operator we compare the operators
            } else if (isOperator(token)) {
                while (!operatorStack.isEmpty() && getPrecedence(operatorStack.peek()) >= getPrecedence(token)) {
                    postfixQueue.add(operatorStack.pop());
                }
                operatorStack.push(token);
            }
        }
        while (!operatorStack.isEmpty())
            postfixQueue.add(operatorStack.pop()); // Empty the stack
        return postfixQueue;
    }

    // STEP 3: Evaluation
    public static double evaluatePostfix(Queue<String> postfixQueue) {
        Stack<Double> stack = new Stack<>();
        while (!postfixQueue.isEmpty()) {
            String token = postfixQueue.poll();

            // If it's number push it to the stack
            if (isNumeric(token)) {
                stack.push(Double.parseDouble(token)); // Convert the string to number

                // If it's an operator pop the last two and applyOperation
            } else {
                double b = stack.pop();
                double a = stack.pop();
                if (token.equals("/") && b == 0)
                    throw new ArithmeticException("Division by zero");
                stack.push(applyOp(a, b, token));
            }
        }
        return stack.pop();
    }

    // STEP 4: Expression Tree & Traversals
    public static Node buildTree(Queue<String> postfixQueue) {
        Stack<Node> stack = new Stack<>();
        while (!postfixQueue.isEmpty()) {
            String token = postfixQueue.poll();
            Node newNode = new Node(token);
            if (!isNumeric(token)) {
                newNode.right = stack.pop();
                newNode.left = stack.pop();
            }
            stack.push(newNode);
        }
        return stack.pop();
    }

    // preorder
    public static void preorder(Node root) {
        if (root == null)
            return;
        System.out.print(root.value + " ");
        preorder(root.left);
        preorder(root.right);
    }

    // inorder
    public static void inorder(Node root) {
        if (root == null)
            return;
        inorder(root.left);
        System.out.print(root.value + " ");
        inorder(root.right);
    }

    // postorder
    public static void postorder(Node root) {
        if (root == null)
            return;
        postorder(root.left);
        postorder(root.right);
        System.out.print(root.value + " ");
    }

    // preorder returning string
    public static String preorderString(Node root) {
        if (root == null)
            return "";
        return root.value + " " + preorderString(root.left) + preorderString(root.right);
    }

    // inorder returning string
    public static String inorderString(Node root) {
        if (root == null)
            return "";
        return inorderString(root.left) + root.value + " " + inorderString(root.right);
    }

    // postorder returning string
    public static String postorderString(Node root) {
        if (root == null)
            return "";
        return postorderString(root.left) + postorderString(root.right) + root.value + " ";
    }

    // Process a single expression and return formatted output string
    public static String processExpression(String expr) {
        Queue<String> tokens = tokenize(expr);
        Queue<String> tokensCopyForPostfix = new Queue<>(tokens);

        Queue<String> postfixQueue = infixToPostfix(tokensCopyForPostfix);

        Queue<String> postfixCopyForEval = new Queue<>(postfixQueue);
        Queue<String> postfixCopyForTree = new Queue<>(postfixQueue);

        double result = evaluatePostfix(postfixCopyForEval);

        StringBuilder sb = new StringBuilder();
        sb.append("Output:\n");
        sb.append("Queue: ").append(tokens).append("\n");
        sb.append("Postfix: ").append(postfixQueue).append("\n");

        if (result == (long) result) {
            sb.append("Result: ").append((long) result).append("\n");
        } else {
            sb.append("Result: ").append(result).append("\n");
        }

        Node root = buildTree(postfixCopyForTree);
        sb.append("\nBST Traversals:\n");
        sb.append("Preorder: ").append(preorderString(root).trim()).append("\n");
        sb.append("Inorder: ").append(inorderString(root).trim()).append("\n");
        sb.append("Postorder: ").append(postorderString(root).trim()).append("\n");

        HashTable hashTable = new HashTable();
        hashTable.insertLinear(result);
        hashTable.insertQuadratic(result);
        hashTable.insertDoubleHash(result);
        hashTable.insertChaining(result);

        return sb.toString();
    }

    // STEP 5: Hash Table
    static class HashTable {
        int size = 10;
        Double[] tableLinear = new Double[size];
        Double[] tableQuadratic = new Double[size];
        Double[] tableDouble = new Double[size];
        LinkedList<Double>[] tableChaining = new LinkedList[size];

        public HashTable() {
            for (int i = 0; i < size; i++) {
                tableChaining[i] = new LinkedList<>();
            }
        }

        // Simple Hash Function
        private int hash(double value) {
            return Math.abs((int) Math.round(value) % size);
        }

        // Secondary Hash Function for Double Hashing
        private int hash2(double value) {
            return 7 - Math.abs((int) Math.round(value) % 7);
        }

        // Linear Probing
        public void insertLinear(double value) {
            int index = hash(value);
            int i = 0;
            while (tableLinear[(index + i) % size] != null && i < size) {
                i++;
            }
            if (i < size)
                tableLinear[(index + i) % size] = value;
        }

        // Quadratic Probing
        public void insertQuadratic(double value) {
            int index = hash(value);
            int i = 0;
            while (tableQuadratic[(index + i * i) % size] != null && i < size) {
                i++;
            }
            if (i < size)
                tableQuadratic[(index + i * i) % size] = value;
        }

        // Double Hashing
        public void insertDoubleHash(double value) {
            int index = hash(value);
            int step = hash2(value);
            int i = 0;
            while (tableDouble[(index + i * step) % size] != null && i < size) {
                i++;
            }
            if (i < size)
                tableDouble[(index + i * step) % size] = value;
        }

        // Separate Chaining
        public void insertChaining(double value) {
            int index = hash(value);
            tableChaining[index].add(value);
        }
    }

    // Helper methods
    private static boolean isOperator(String s) {
        return s.matches("[+\\-*/%^]");
    }

    private static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static int getPrecedence(String op) {
        return switch (op) {
            case "^" -> 3;
            case "*", "/", "%" -> 2;
            case "+", "-" -> 1;
            default -> 0;
        };
    }

    private static double applyOp(double a, double b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            case "%" -> a % b;
            case "^" -> Math.pow(a, b);
            default -> 0;
        };
    }
}
