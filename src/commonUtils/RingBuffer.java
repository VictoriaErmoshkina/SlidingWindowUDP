package commonUtils;

/**
 * Created by Виктория on 04.06.2017.
 */
public class RingBuffer<T> {
    private final Object lock = new Object();
    private T[] buffer;
    private int head;
    private int tail;

    private int next_index(int index) {
        int nextInd = -1;
        if (index >= 0 && index < buffer.length) {
            if (index == (buffer.length - 1))
                nextInd = 0;
            else
                nextInd = index + 1;
        }
        return nextInd;
    }

    public RingBuffer(T[] elements) {
        this.head = 0;
        this.tail = 0;
        this.buffer = elements;
    }
    public T take() throws InterruptedException {
        synchronized (lock) {
            while (head == tail) {
                lock.wait();
            }
            T element = buffer[head];
            head = next_index(head);
            lock.notifyAll();
            return element;
        }
    }

    public T getHead() throws InterruptedException {
        synchronized (lock) {
            if (head != tail)
                return buffer[head];
            else
                return null;
        }
    }

    public int capacity() {
        synchronized (lock) {
            return buffer.length - 1;
        }
    }

    public void put(T element) throws InterruptedException {
        synchronized (lock) {
            while (head == next_index(tail)) {
                lock.wait();
            }
            buffer[tail] = element;
            tail = next_index(tail);
            lock.notifyAll();
        }
    }

    public int size() {
        synchronized (lock) {
            int size = tail - head;
            if (size >= 0)
                return size;
            else
                return size + buffer.length;
        }
    }

}
