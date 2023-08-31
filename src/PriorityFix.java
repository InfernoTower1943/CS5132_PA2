import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class PriorityFix {
    public static int[] videoSequence(String filename) {
        // Fill in your code here
        // You may create other classes if required
        int[] result = new int[0];
        int output_count = 0;
        int numCommands = 0;
        PriorityQueue<Integer, PriorityDate> pq = new PriorityQueue<>();
        try{
            File myObj = new File(filename);
            Scanner scanner = new Scanner(myObj);
            String init = scanner.nextLine();

            // initialise variables based on first line of input
            numCommands=Integer.parseInt(init);
            System.out.println("commands: "+numCommands);
            result = new int[numCommands];

            for (int i=0; i<numCommands; i++){
                String data = scanner.nextLine();
                String[] videoData = data.split(" ");
                System.out.println("command "+i+" "+videoData[0]);
                if (Objects.equals(videoData[0], "add")){
                    System.out.println("hi ");
                    int id=Integer.parseInt(videoData[1]);
                    int year=Integer.parseInt(videoData[2]);
                    int month=Integer.parseInt(videoData[3]);
                    int day=Integer.parseInt(videoData[4]);
                    System.out.printf("%d %d %d %d\n", id, year, month, day);
                    PriorityDate pd = new PriorityDate(year, month, day);
                    pq.enqueue(id, pd);
                }else if (Objects.equals(videoData[0], "get")){
                    result[i] = pq.dequeue();
                    System.out.println("bye "+result[i]);
                    output_count++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        int[] final_result = new int[output_count];
        int idx=0;
        for (int i=0; i<numCommands; i++){
            if (result[i]!=0){
                final_result[idx] = result[i];
                idx++;
            }
        }
        return final_result;
    }

    public static void main(String[] args) {
        // Write any testing code here, if required.
        System.out.println(Arrays.toString(videoSequence("clown.in")));
    }
}
class PriorityQueue<T, S extends Comparable<S>>{
    HeapTree<T, S> tree = new HeapTree<>();
    void enqueue(T item, S priority){
        tree.addElement(new PriorityNode<T, S>(item, priority));
    }

    T dequeue(){
        return tree.removeMax().getItem();
    }
}

class HeapTree<T, S extends Comparable<S>>{
    PriorityNode<T, S> root;
    int count;

    public HeapTree(){
        root = null;
        count = 0;
    }

    public HeapTree(PriorityNode<T, S> node){
        root = node;
        count = 1;
    }

    // Important stuff
    PriorityNode<T, S> top(){
        return root;
    }

    void addElement(PriorityNode<T, S> node){
        System.out.printf("%d %d %d\n", getHeight(), (int)(Math.pow(2, getHeight()))-1, count);
        if (count==0 || root==null){
            root = node;
        }else if ((int)(Math.pow(2, getHeight()))-1 == count){
            PriorityNode<T, S> curr = root;
            while (curr.getLeft()!=null){
                curr = curr.getLeft();
                //System.out.println("a "+ curr.item);
            }
            curr.addInOrder(node);
            shiftUp(node);
        }else{
            if (last().getParent().addInOrder(node) != -1){
                shiftUp(node);
            }else{
                System.out.println("ERROR");
            }
        }
        count++;
        System.out.printf("%d %d %d\n", getHeight(), (int)(Math.pow(2, getHeight()))-1, count);
    }

    PriorityNode<T, S> removeMax(){
        PriorityNode<T, S> out = root;
        if (root != null){
            if (count==1){
                root=null;
            }else {
                PriorityNode<T, S> last = last();
                System.out.println("removeMax last(): "+last.item);
                if (root.getLeft() != null) {
                    last.setLeft(root.getLeft());
                    root.getLeft().setParent(last);
                    last.cutFromParent();
                }
                if (root.getRight() != null) {
                    last.setRight(root.getRight());
                    root.getRight().setParent(last);
                    System.out.println("last " + last.item);
                    last.cutFromParent();
                }
                this.root = last;

                shiftDown(root);
            }
            count--;
        }
        return out;
    }

    // Helper functions
    int getHeight(){
        return (int)(Math.log(count) / Math.log(2))+1;
    }

    PriorityNode<T, S> last(){
        String directions = Integer.toBinaryString(count);
        PriorityNode<T, S> curr = root;
        System.out.println("Binary string last: "+directions);
        for (int i=1; i<directions.length(); i++){
            switch (directions.charAt(i)) {
                case '0' -> curr = curr.getLeft();
                case '1' -> curr = curr.getRight();
            }
        }
        return curr;
    }

    void shiftUp(PriorityNode<T, S> node){
        PriorityNode<T, S> curr = node;
        while (curr.getParent() != null && curr.getParent().getPriority().compareTo(curr.getPriority()) < 0){
            // swap curr and curr parent
            curr = curr.swapWithParent();
        }
    }

    void shiftDown(PriorityNode<T, S> node){
        String directions = Integer.toBinaryString(count+1);
        PriorityNode<T, S> curr = root;
        for (int i=1; i<directions.length(); i++){
            switch(directions.charAt(i)){
                case '0':
                    if (curr.left==null) return;
                    curr = curr.swapWithLeft();
                    break;
                case '1':
                    if (curr.right==null) return;
                    curr = curr.swapWithRight();
                    break;
            }
        }
    }
}

class PriorityNode<T, S extends Comparable<S>>{
    T item;
    S priority;
    PriorityNode<T,S> parent, left, right;

    public PriorityNode(T item, S priority) {
        this.item = item;
        this.priority = priority;
    }

    public T getItem(){
        return item;
    }

    public S getPriority(){
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

    public int addInOrder(PriorityNode<T, S> node){
        if (left==null){
            this.setLeft(node);
            node.setParent(this);
            return 0;
        }else if (right==null){
            this.setRight(node);
            node.setParent(this);
            return 1;
        }else{
            return -1;
        }
    }

    public void set(PriorityNode<T, S> node){
        this.item = node.item;
        this.priority = node.priority;
    }

    public PriorityNode<T, S> swapWithParent(){
        T tempItem = item;
        S tempPriority = priority;
        this.item = parent.item; this.priority = parent.priority;
        parent.setItem(tempItem); parent.setPriority(priority);
        return parent;
    }

    public PriorityNode<T, S> swapWithLeft(){
        T tempItem = item;
        S tempPriority = priority;
        this.item = left.item; this.priority = left.priority;
        left.setItem(tempItem); left.setPriority(priority);
        return left;
    }

    public PriorityNode<T, S> swapWithRight(){
        T tempItem = item;
        S tempPriority = priority;
        this.item = right.item; this.priority = right.priority;
        right.setItem(tempItem); right.setPriority(priority);
        return right;
    }

    public void cutFromParent(){
        if (parent==null) return;
        if (parent.left==this){
            parent.setLeft(null);
            if (parent.right != null){
                parent.left = parent.right;
                parent.right = null;
            }
        }if (parent.right==this){
            parent.setRight(null);
        }
        this.parent=null;
    }
}

class PriorityDate implements Comparable<PriorityDate>{
    int year, month, day;

    public PriorityDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    public int compareTo(PriorityDate o) {
        if (year>o.year) return 1;
        else if (year<o.year) return -1;
        else{
            if (month>o.month) return 1;
            else if (month<o.month) return -1;
            else{
                if (day>o.day) return 1;
                else if (day<o.day) return -1;
                else{
                    return 0;
                }
            }
        }
    }
}