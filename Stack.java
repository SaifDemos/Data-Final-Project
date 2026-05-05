public class Stack<T> {

    private Object[] ar;
    private int size;
    private int top;

    public Stack(int s) {
        size = s;
        ar = new Object[size];
        top = 0;
    }

    public Stack() {
        size = 10;
        ar = new Object[size];
        top = 0;
    }

    public void push(T x) {
        if (top < size) {
            ar[top] = x;
            top++;
        }
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("stack is empty");
        }
        top--;
        T x = (T) ar[top];
        ar[top] = null;
        return x;
    }

    public boolean isEmpty() {
        return top == 0;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("stack is empty");
        }
        return (T) ar[top - 1];
    }

    public boolean isFull() {
        return top == size;
    }
}
