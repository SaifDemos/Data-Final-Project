class LLNode<T> {
    T data;
    LLNode<T> next;

    public LLNode(T d) {
        data = d;
        next = null;
    }
}

public class LinkedList<T> {

    LLNode<T> head = null;

    public void add(T x) {
        LLNode<T> n = new LLNode<>(x);
        if (head == null) {
            head = n;
        } else {
            LLNode<T> temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = n;
        }
    }

    public int count() {
        int c = 0;
        LLNode<T> temp = head;
        while (temp != null) {
            c++;
            temp = temp.next;
        }
        return c;
    }

    public void print() {
        LLNode<T> temp = head;
        while (temp != null) {
            System.out.print(temp.data + " ");
            temp = temp.next;
        }
        System.out.println();
    }
}
