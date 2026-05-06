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

    // Extract all numbers from tokenized expression
    public static Queue<Double> extractNumbers(Queue<String> tokens) {
        Queue<Double> numbers = new Queue<>();
        Queue<String> copy = new Queue<>(tokens);
        while (!copy.isEmpty()) {
            String token = copy.poll();
            if (isNumeric(token)) {
                numbers.add(Double.parseDouble(token));
            }
        }
        return numbers;
    }

    // Validate expression before processing
    public static boolean isValidExpression(String expr) {
        try {
            Queue<String> tokens = tokenize(expr);
            if (tokens.isEmpty()) return false;
            Queue<String> copy = new Queue<>(tokens);
            infixToPostfix(copy);
            copy = new Queue<>(tokens);
            Queue<String> postfix = infixToPostfix(copy);
            Queue<String> evalCopy = new Queue<>(postfix);
            evaluatePostfix(evalCopy);
            return true;
        } catch (ArithmeticException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Process a single expression and return formatted output string
    public static String processExpression(String expr, int tableSize) {
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

        Queue<Double> numbers = extractNumbers(tokens);
        sb.append("\nExtracted Numbers: ").append(numbers).append("\n");

        HashTable hashTable = new HashTable(tableSize);
        boolean linearOk = true, quadraticOk = true, doubleOk = true;

        Queue<Double> numCopy = new Queue<>(numbers);
        while (!numCopy.isEmpty()) {
            double val = numCopy.poll();
            if (!hashTable.insertLinear(val)) linearOk = false;
            if (!hashTable.insertQuadratic(val)) quadraticOk = false;
            if (!hashTable.insertDoubleHash(val)) doubleOk = false;
            hashTable.insertChaining(val);
        }

        sb.append("\nHash Tables (size=").append(tableSize).append("):\n");
        String linearDisplay = hashTable.displayLinear();
        if (!linearOk) linearDisplay += "  NOT ENOUGH TABLE SPACE\n";
        sb.append(linearDisplay);
        String quadraticDisplay = hashTable.displayQuadratic();
        if (!quadraticOk) quadraticDisplay += "  NOT ENOUGH TABLE SPACE\n";
        sb.append(quadraticDisplay);
        String doubleDisplay = hashTable.displayDoubleHash();
        if (!doubleOk) doubleDisplay += "  NOT ENOUGH TABLE SPACE\n";
        sb.append(doubleDisplay);
        sb.append(hashTable.displayChaining());

        return sb.toString();
    }

    // STEP 5: Hash Table
    static class HashTable {
        int size;
        Double[] tableLinear;
        Double[] tableQuadratic;
        Double[] tableDouble;
        LinkedList<Double>[] tableChaining;

        public HashTable(int size) {
            this.size = size;
            tableLinear = new Double[size];
            tableQuadratic = new Double[size];
            tableDouble = new Double[size];
            tableChaining = new LinkedList[size];
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
            return 7 - Math.abs((int) Math.round(value) % size);
        }

        // Linear Probing
        public boolean insertLinear(double value) {
            int index = hash(value);
            int i = 0;
            while (tableLinear[(index + i) % size] != null && i < size) {
                i++;
            }
            if (i < size) {
                tableLinear[(index + i) % size] = value;
                return true;
            }
            return false;
        }

        // Quadratic Probing
        public boolean insertQuadratic(double value) {
            int index = hash(value);
            int i = 0;
            while (tableQuadratic[(index + i * i) % size] != null && i < size) {
                i++;
            }
            if (i < size) {
                tableQuadratic[(index + i * i) % size] = value;
                return true;
            }
            return false;
        }

        // Double Hashing
        public boolean insertDoubleHash(double value) {
            int index = hash(value);
            int step = hash2(value);
            int i = 0;
            while (tableDouble[(index + i * step) % size] != null && i < size) {
                i++;
            }
            if (i < size) {
                tableDouble[(index + i * step) % size] = value;
                return true;
            }
            return false;
        }

        // Separate Chaining
        public void insertChaining(double value) {
            int index = hash(value);
            tableChaining[index].add(value);
        }

        public String displayLinear() {
            return displayOpenAddressing(tableLinear, "Linear Probing");
        }

        public String displayQuadratic() {
            return displayOpenAddressing(tableQuadratic, "Quadratic Probing");
        }

        public String displayDoubleHash() {
            return displayOpenAddressing(tableDouble, "Double Hashing");
        }

        public String displayChaining() {
            return displayChainTable("Separate Chaining");
        }

        private String displayOpenAddressing(Double[] table, String label) {
            StringBuilder sb = new StringBuilder();
            sb.append(label).append(":\n  ");
            for (int i = 0; i < size; i++) {
                String val = table[i] == null ? "null" : table[i].toString();
                if (i == size - 1) {
                    sb.append(String.format("[%d]: %s\n", i, val));
                } else {
                    sb.append(String.format("[%d]: %s  ", i, val));
                }
            }
            return sb.toString();
        }

        private String displayChainTable(String label) {
            StringBuilder sb = new StringBuilder();
            sb.append(label).append(":\n  ");
            for (int i = 0; i < size; i++) {
                String val = tableChaining[i].count() == 0 ? "empty" : tableChaining[i].toString();
                if (i == size - 1) {
                    sb.append(String.format("[%d]: %s\n", i, val));
                } else {
                    sb.append(String.format("[%d]: %s  ", i, val));
                }
            }
            return sb.toString();
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
