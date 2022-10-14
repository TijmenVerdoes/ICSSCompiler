package nl.han.ica.datastructures.implementations;

import nl.han.ica.datastructures.IHANLinkedList;

public class HANLinkedList<T> implements IHANLinkedList<T> {
    private class HANNode<N> {
        public N value;
        public HANNode<N> next;

        public HANNode(N value) {
            this.value = value;
            this.next = null;
        }

        @Override
        public String toString()
        {
            return "HANNode [value=" + this.value + ", next=" + this.next + "]";
        }
    }

    private HANNode<T> head;

    public HANLinkedList()
    {
        this.head = null;
    }

    @Override
    public void addFirst(T value) {
        HANNode<T> temp = new HANNode<T>(value);

        if (this.head == null)
        {
            this.head = temp;
            return;
        }

        HANNode<T> current = this.head;

        while (current.next != null) current = current.next;

        current.next = temp;
    }

    @Override
    public void clear()
    {
        this.head = null;
    }

    @Override
    public void insert(int index, T value) {
        HANNode<T> temp = new HANNode<T>(value);

        if (this.head == null) {
            this.head = temp;
            return;
        }

        if (index == 0) {
            temp.next = this.head;
            this.head = temp;
            return;
        }

        HANNode<T> current = this.head;

        while (index-- > 1 && current != null) current = current.next;

        if (current == null || current.next == null)
            return;

        temp.next = current.next;
        current.next = temp;
    }

    @Override
    public void delete(int index) {
        if (index == 0 && this.head != null){
            HANNode<T> temp = this.head;
            this.head = temp.next;

            return;
        }

        HANNode<T> currnet = this.head;

        while (index-- > 1 && currnet != null) {
            currnet = currnet.next;
        }

        if (currnet == null || currnet.next == null) {
            return;
        }

        currnet.next = currnet.next.next;
    }

    @Override
    public T get(int index) {
        HANNode<T> current = this.head;

        while (index-- > 0 && current != null) current = current.next;

        return current == null ? null : current.value;
    }

    @Override
    public void removeFirst() {
        this.delete(0);
    }

    @Override
    public T getFirst() {
        return this.get(0);
    }

    @Override
    public int getSize() {
        int size = 0;
        HANNode<T> current = this.head;

        while (current != null)
        {
            current = current.next;
            ++size;
        }

        return size;
    }

    @Override
    public String toString() {
        return "HANLinkedList [head=" + head + "]";
    }
}

