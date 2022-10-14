package nl.han.ica.datastructures.implementations;

import nl.han.ica.datastructures.IHANQueue;
import nl.han.ica.datastructures.IHANLinkedList;

public class HANQueue<T> implements IHANQueue<T> {
    private IHANLinkedList<T> queue = new HANLinkedList<>();

    @Override
    public void clear() {
        this.queue.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.queue.getFirst() == null;
    }

    @Override
    public void enqueue(T value) {
        this.queue.addFirst(value);
    }

    @Override
    public T dequeue() {
		var temp = queue.get(0);
		queue.removeFirst();
        return temp;
    }

    @Override
    public T peek() {
        return this.queue.get(0);
    }

    @Override
    public int getSize() {
        return this.queue.getSize();
    }
}
