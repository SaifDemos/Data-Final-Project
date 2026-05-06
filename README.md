# Mathematical Expression Processor

A Data Structures class final project that reads infix expressions, converts them to postfix, evaluates them, builds expression trees, and demonstrates hash table collision strategies.

---

## Program Flow (The Pipeline)

When you enter an expression like `3 + 4 * 2 / (1 - 5) ^ 2`, it goes through **5 steps**:

### Step 1: Tokenization (uses Queue)
Splits the expression string into individual tokens (numbers, operators, parentheses).

**Example:**
```
Input:  3 + 4 * 2 / ( 1 - 5 ) ^ 2
Output: [3, +, 4, *, 2, /, (, 1, -, 5, ), ^, 2]
```

**How it works:**
- Scans the string character by character
- Multi-digit numbers and decimals are buffered (e.g., `100` is kept together)
- Negative signs are detected when a `-` appears before a number
- Each token is added to a `Queue<String>` using `add()`

### Step 2: Infix to Postfix (uses Stack)
Converts the infix expression (human-readable) to postfix notation (computer-friendly) using the **Shunting Yard algorithm**.

**Example:**
```
Infix:   3 + 4 * 2 / ( 1 - 5 ) ^ 2
Postfix: 3 4 2 * 1 5 - 2 ^ / +
```

**How it works:**
- Numbers go directly to the output queue
- Operators are pushed onto a stack
- Operators with lower or equal precedence are popped from the stack to the output before pushing the current operator
- Parentheses: `(` is pushed to stack, when `)` is found everything until `(` is popped to output

**Operator Precedence:**
| Operator | Precedence |
|----------|------------|
| `^`      | 3 (highest) |
| `* / %`  | 2 |
| `+ -`    | 1 (lowest) |

### Step 3: Postfix Evaluation (uses Stack)
Evaluates the postfix expression to get the numerical result.

**Example:**
```
Postfix: 3 4 2 * 1 5 - 2 ^ / +
Result:  3.5
```

**How it works:**
- Numbers are pushed onto the stack
- When an operator is found, the top two numbers are popped, the operation is applied, and the result is pushed back
- Division by zero throws an `ArithmeticException`

### Step 4: Expression Tree + Traversals
Builds a binary expression tree from the postfix notation, then shows three traversal orders.

**Tree structure for `3 + 4 * 2 / (1 - 5) ^ 2`:**
```
          +
        /   \
       3     /
           /   \
          *     ^
         / \   / \
        4   2 -   2
             / \
            1   5
```

**Traversals:**
- **Preorder** (Root → Left → Right): `+ 3 / * 4 2 ^ - 1 5 2`
- **Inorder** (Left → Root → Right): `3 + 4 * 2 / 1 - 5 ^ 2` (the original expression without parentheses)
- **Postorder** (Left → Right → Root): `3 4 2 * 1 5 - 2 ^ / +` (same as postfix)

**How the tree is built:**
- Numbers create leaf nodes and are pushed onto a stack
- Operators create parent nodes, popping two children from the stack (right child first, then left)

### Step 5: Hash Table with 4 Collision Strategies
Demonstrates four ways to handle hash table collisions. The hash function is `Math.abs(round(value) % 10)`.

**Example with result = 25:**
| Strategy | Hash | Index | How it works |
|----------|------|-------|-------------|
| Linear Probing | 25 % 10 = 5 | 5 | Try index 5, if occupied try 6, 7, 8... |
| Quadratic Probing | 25 % 10 = 5 | 5 | Try index 5, then 5+1, 5+4, 5+9... (i²) |
| Double Hashing | 25 % 10 = 5 | 5 | Primary hash + secondary hash (7 - value%7) |
| Separate Chaining | 25 % 10 = 5 | 5 | Each bucket holds a linked list of values |

---

## Data Structures Implemented from Scratch

### 1. `Queue.java` — Generic Queue (Linked List Implementation)
- **Type:** `Queue<T>` using linked list (`QNode<T>`)
- **Methods:**
  - `add(T x)` — enqueue: adds to the rear
  - `T poll()` — dequeue: removes and returns from the front
  - `boolean isEmpty()` — checks if queue is empty
  - `Queue(Queue<T> other)` — copy constructor to clone a queue
- **How it works:** Maintains `front` and `rear` pointers. Adding creates a new node at the rear. Polling removes the front node and moves `front` forward.

### 2. `Stack.java` — Generic Stack (Array Implementation)
- **Type:** `Stack<T>` using fixed-size array (`Object[]`)
- **Methods:**
  - `push(T x)` — adds element to the top
  - `T pop()` — removes and returns the top element
  - `T peek()` — returns the top element without removing it
  - `boolean isEmpty()` — checks if stack is empty
  - `boolean isFull()` — checks if stack is at capacity
- **How it works:** Uses an array with a `top` index. Pushing increments `top`, popping decrements it. Throws `RuntimeException` if popping from an empty stack.

### 3. `LinkedList.java` — Generic Linked List
- **Type:** `LinkedList<T>` using nodes (`LLNode<T>`)
- **Methods:**
  - `add(T x)` — adds a node to the end of the list
  - `int count()` — returns the number of nodes
  - `void print()` — prints all node values
- **How it works:** Maintains a `head` pointer. Adding traverses to the last node and links a new node to it. Used in the hash table's separate chaining strategy.

### 4. `Node.java` — Expression Tree Node
- **Fields:** `String value`, `Node left`, `Node right`
- **Purpose:** Represents nodes in the expression tree where leaf nodes hold numbers and internal nodes hold operators

---

## File Structure

| File | Purpose |
|------|---------|
| `Main.java` | Entry point with menu system (live input, file input, exit) |
| `ExpressionProcessor.java` | All processing logic + inner `HashTable` class |
| `Stack.java` | Custom generic stack (array-based) |
| `Queue.java` | Custom generic queue (linked list-based) |
| `LinkedList.java` | Custom generic linked list |
| `Node.java` | Expression tree node |
| `compile.sh` | Compiles all Java files |
| `run.sh` | Runs the program |
| `clean.sh` | Deletes compiled .class files |
| `input.txt` | Input file for batch processing (bonus) |
| `output.txt` | Generated output file (bonus) |

---

## Key Concepts to Remember

### Why Stack for Infix-to-Postfix?
Stacks are LIFO (Last In, First Out). This is perfect because operators that appear later may need to be processed first (higher precedence). The stack temporarily holds operators until their operands are ready.

### Why Queue for Tokens?
Queues are FIFO (First In, First Out). Tokens are read in order from the expression and must be processed in the same order during conversion and evaluation.

### Shunting Yard Algorithm (Step 2)
Developed by Edsger Dijkstra. It uses a stack to handle operator precedence and parentheses. The key insight: push operators to stack, pop them to output when a lower-precedence operator arrives or when a closing parenthesis is found.

### Expression Trees
Every binary expression can be represented as a tree where:
- **Leaf nodes** = operands (numbers)
- **Internal nodes** = operators
- The tree naturally encodes the order of operations
- Inorder traversal gives the infix expression
- Postorder traversal gives the postfix expression

### Hash Table Collisions
When two values hash to the same index, we need a strategy:
- **Linear Probing**: Check next slot sequentially — simple but causes clustering
- **Quadratic Probing**: Check slots at i² intervals — reduces clustering
- **Double Hashing**: Use a second hash function for the step size — best distribution
- **Separate Chaining**: Each bucket holds a linked list — simple, handles any number of collisions
