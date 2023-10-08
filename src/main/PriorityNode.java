package main;

public class PriorityNode<T, S extends Comparable<S>> {
    T item;
    S priority;
    PriorityNode<T, S> parent, left, right;

    public PriorityNode(T item, S priority) {
        this.item = item;
        this.priority = priority;
    }

    public T getItem() {
        return item;
    }

    public S getPriority() {
        return priority;
    }


    public void setItem(T item) {
        this.item = item;
    }

    public void setPriority(S priority) {
        this.priority = priority;
    }

    public PriorityNode<T, S> getParent() {
        return parent;
    }

    public void setParent(PriorityNode<T, S> parent) {
        this.parent = parent;
    }

    public PriorityNode<T, S> getLeft() {
        return left;
    }

    public void setLeft(PriorityNode<T, S> left) {
        this.left = left;
    }

    public PriorityNode<T, S> getRight() {
        return right;
    }

    public void setRight(PriorityNode<T, S> right) {
        this.right = right;
    }

    public int addInOrder(PriorityNode<T, S> node) {
        if (left == null) {
            this.setLeft(node);
            node.setParent(this);
            return 0;
        } else if (right == null) {
            this.setRight(node);
            node.setParent(this);
            return 1;
        } else {
            return -1;
        }
    }

    public void set(PriorityNode<T, S> node) {
        this.item = node.item;
        this.priority = node.priority;
    }

    public PriorityNode<T, S> swapWithParent() {
        T tempItem = item;
        S tempPriority = priority;
        this.item = parent.item;
        this.priority = parent.priority;
        parent.setItem(tempItem);
        parent.setPriority(priority);
        return parent;
    }

    public PriorityNode<T, S> swapWithLeft() {
        T tempItem = item;
        S tempPriority = priority;
        this.item = left.item;
        this.priority = left.priority;
        left.setItem(tempItem);
        left.setPriority(priority);
        return left;
    }

    public PriorityNode<T, S> swapWithRight() {
        T tempItem = item;
        S tempPriority = priority;
        this.item = right.item;
        this.priority = right.priority;
        right.setItem(tempItem);
        right.setPriority(priority);
        return right;
    }

    public void cutFromParent() {
        if (parent == null) return;
        if (parent.left == this) {
            parent.setLeft(null);
            if (parent.right != null) {
                parent.left = parent.right;
                parent.right = null;
            }
        }
        if (parent.right == this) {
            parent.setRight(null);
        }
        this.parent = null;
    }
}
