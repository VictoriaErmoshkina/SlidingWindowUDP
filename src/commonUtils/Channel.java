package commonUtils;

import java.util.LinkedList;

/**
 * Created by Виктория on 04.06.2017.
 */
public class Channel<T> {
    private final LinkedList<T> queue = new LinkedList<>();
    private final int maxCount;
    private final Object lock = new Object();

    public Channel(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getSize() {
        synchronized (lock) {
            return queue.size();
        }
    }

    public void put(T x) {
        synchronized (lock) {
            while (queue.size() >= maxCount)
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            queue.addLast(x);
            lock.notifyAll();
        }
    }

    public T take() {
        synchronized (lock) {
            while (queue.isEmpty())
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            lock.notifyAll();
            return queue.removeFirst();
        }
    }

    public LinkedList<T> getList() {
        return queue;
    }
}
