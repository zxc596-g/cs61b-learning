package deque;

import org.junit.Test;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>{
    public class Node{
        private T item;
        private Node pre;
        private Node next;

        public Node(T i,Node p,Node n){
            item=i;
            pre=p;
            next=n;
        }
    }

    private Node sentinel;
    private int size;
    public LinkedListDeque(){
        size=0;
        //error(sentinel is null now):sentinel=new Node(null,sentinel,sentinel);
        sentinel=new Node(null,null,null);
        sentinel.next=sentinel;
        sentinel.pre=sentinel;
    }

    public void addFirst(T item){
        //assume that item is never null
        Node newnode=new Node(item,sentinel,sentinel.next);
        sentinel.next.pre=newnode;
        sentinel.next=newnode;
        size+=1;
    }

    public void addLast(T item){
        //assume that item is never null
        Node newnode=new Node(item,sentinel.pre,sentinel);
        sentinel.pre.next=newnode;
        sentinel.pre=newnode;
        size+=1;
    }

    public boolean isEmpty(){
        if(size()==0){
            return true;
        }else{
            return false;
        }
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        if(isEmpty()){
            return;
        }else{
            Node current = sentinel.next;
            while(current!=null&&current!=sentinel){
                System.out.print(current.item+" ");
                current=current.next;
            }
            System.out.println();
        }
    }

    public T removeFirst(){
        if(isEmpty()){
            return null;
        }else{
            Node current=sentinel.next;
            T item = current.item;
            sentinel.next=current.next;
            current.next.pre=sentinel;
            current.item=null;
            current.next=null;
            current.pre=null;
            size-=1;
            return item;
        }
    }

    public T removeLast(){
        if(isEmpty()){
            return null;
        }else{
            Node current=sentinel.pre;
            T item= current.item;
            sentinel.pre=current.pre;
            current.pre.next=sentinel;
            current.item=null;
            current.next=null;
            current.pre=null;
            size-=1;
            return item;
        }
    }

    public T get(int index){
        if(index>=size()||index<0){
            return null;
        }else{
            Node current=sentinel.next;
            for(int i=0;i<index;i+=1){
                current=current.next;
            }
            return current.item;
        }
    }

    public T getRecursive(int index){
        if(index>=size()||index<0){
            return null;
        }
        Node current=sentinel.next;
        return recursive(index,current);
    }
    private T recursive(int index,Node current){
        if(index==0){
            return current.item;
        }else{
            return recursive(index-1,current.next);
        }
    }

    private class LinkedListIterator implements Iterator<T>{
        private Node current;
        LinkedListIterator(){
            current=sentinel.next;
        }
        @Override
        public boolean hasNext(){
            return (current!=null&&current!=sentinel);
        }
        @Override
        public T next(){
            if(current==null)
                throw new java.util.NoSuchElementException();
            T returnitem=current.item;
            current=current.next;
            return returnitem;
        }
    }

    @Override
    public Iterator<T> iterator(){
        return new LinkedListIterator();
    }

    @Test
    public void Iteratortest(){
        LinkedListDeque<Integer> ld=new LinkedListDeque<>();
        ld.addLast(2);
        ld.addLast(8);
        ld.addFirst(5);
        for(int a:ld){
            System.out.println(a);
        }
    }
}
