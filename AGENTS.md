# AGENTS.md

## Project

Mathematical expression processor — Java data structures final project. Tokenizes infix expressions, converts to postfix (Shunting Yard), evaluates, builds expression trees, and demonstrates 4 hash table collision strategies.

## Commands

```bash
./compile.sh   # javac all .java files via find
./run.sh       # java Main
./clean.sh     # delete all .class files
```

Order: compile → run. No build tool, linter, test framework, or typecheck.

## Key Constraints

- **No packages** — all `.java` files live in the default package at repo root
- **Java 21+ required** — `Main.java` uses unnamed class (`void main()`), not `public static void main`
- **compile.sh** uses `find . -name "*.java"` — compiles every `.java` file in the tree together; do not split compilation
- **`run.sh` runs `java Main`** — entry point is `Main.java` in the default package

## Architecture

| File | Role |
|------|------|
| `Main.java` | CLI menu (live input, batch from `input.txt`, exit) |
| `ExpressionProcessor.java` | Pipeline + inner `HashTable` class; call `processExpression(expr)` |
| `Stack.java` | Array-based generic stack (default capacity 10) |
| `Queue.java` | Linked-list generic queue; `QNode<T>` is a package-private helper class in the same file |
| `LinkedList.java` | Generic linked list (used only by hash table separate chaining) |
| `Node.java` | Expression tree node (`value`, `left`, `right`) |

## Pipeline (ExpressionProcessor.processExpression)

1. `tokenize()` → `Queue<String>` (handles multi-digit, decimals, unary minus)
2. `infixToPostfix()` → `Queue<String>` (Shunting Yard, `^`=3, `*/%`=2, `+-`=1)
3. `evaluatePostfix()` → `double` (throws `ArithmeticException` on div by zero)
4. `buildTree()` → `Node` + preorder/inorder/postorder string traversals
5. `HashTable` — inserts result into 4 strategies: linear, quadratic, double hash, chaining (table size 10, hash = `abs(round(value) % 10)`)

## Quirks

- `.gitignore` contains `*.sh` but shell scripts are already tracked; new `.sh` files will not be ignored (git ignores only untracked files matching patterns)
- `input.txt` is read from CWD; `output.txt` is written to CWD by batch mode
- Stack uses `Object[]` with unchecked casts; `push` silently does nothing when full
- Queue's `poll()` returns `null` on empty (no exception); callers must check `isEmpty()`
