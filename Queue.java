class QNode<T> {
    T data;
    QNode<T> next;

    public QNode(T d) {
        data = d;
        next = null;
    }
}

public class Queue<T> {

    private QNode<T> front, rear;

    public Queue() {
        front = rear = null;
    }

    public Queue(Queue<T> other) {
        front = rear = null;
        if (other == null || other.isEmpty())
            return;
        QNode<T> current = other.front;
        while (current != null) {
            add(current.data);
            current = current.next;
        }
    }

    public void add(T x) {
        QNode<T> n = new QNode<>(x);
        if (rear == null) {
            rear = front = n;
        } else {
            rear.next = n;
            rear = n;
        }
    }

    public T poll() {
        if (front != null) {
            T value = front.data;
            front = front.next;
            if (front == null) {
                rear = null;
            }
            return value;
        }
        return null;
    }

    public boolean isEmpty() {
        return front == null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        QNode<T> temp = front;
        while (temp != null) {
            sb.append(temp.data);
            if (temp.next != null)
                sb.append(", ");
            temp = temp.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
