package main;

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
        PriorityQueue<Integer, PriorityDate> pq = new PriorityQueue<Integer, PriorityDate>();
        try {
            File myObj = new File(filename);
            Scanner scanner = new Scanner(myObj);
            String init = scanner.nextLine();

            // initialise variables based on first line of input
            numCommands = Integer.parseInt(init);
            result = new int[numCommands];

            for (int i = 0; i < numCommands; i++) {
                String data = scanner.nextLine();
                String[] videoData = data.split(" ");
                if (Objects.equals(videoData[0], "add")) {
                    int id = Integer.parseInt(videoData[1]);
                    int year = Integer.parseInt(videoData[2]);
                    int month = Integer.parseInt(videoData[3]);
                    int day = Integer.parseInt(videoData[4]);
                    PriorityDate pd = new PriorityDate(year, month, day);
                    pq.enqueue(id, pd);
                } else if (Objects.equals(videoData[0], "get")) {
                    result[i] = pq.dequeue();
                    output_count++;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        int[] final_result = new int[output_count];
        int idx = 0;
        for (int i = 0; i < numCommands; i++) {
            if (result[i] != 0) {
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
        if (count==0 || root==null){
            root = node;
        }else if ((int)(Math.pow(2, getHeight()))-1 == count){
            PriorityNode<T, S> curr = root;
            while (curr.getLeft()!=null){
                curr = curr.getLeft();
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
    }

    PriorityNode<T, S> removeMax(){
        PriorityNode<T, S> out = root;
        if (root != null){
            if (count==1){
                root=null;
            }else {
                PriorityNode<T, S> last = last();
                if (root.getLeft() != null) {
                    last.setLeft(root.getLeft());
                    root.getLeft().setParent(last);
                    last.cutFromParent();
                }
                if (root.getRight() != null) {
                    last.setRight(root.getRight());
                    root.getRight().setParent(last);
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

    public boolean isEmpty(){
        return count==0;
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