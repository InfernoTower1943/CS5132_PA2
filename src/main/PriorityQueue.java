package main;

public class PriorityQueue<T, S extends Comparable<S>> {
    HeapTree<T, S> tree = new HeapTree<T, S>();

    void enqueue(T item, S priority) {
        tree.addElement(new PriorityNode<T, S>(item, priority));
    }

    public T dequeue() {
        return tree.removeMax().getItem();
    }

    public S peek(){
        if (tree.isEmpty()) return null;
        return tree.root.priority;
    }

    public PriorityNode top(){
        if (tree.isEmpty()) return null;
        return tree.root;
    }

    public boolean isEmpty() {
        return tree.isEmpty();
    }
}