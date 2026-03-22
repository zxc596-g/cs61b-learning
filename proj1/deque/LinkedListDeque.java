package deque;

public class LinkedListDeque<T> {
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
            T item = sentinel.next.item;
            sentinel.next=sentinel.next.next;
            sentinel.next.pre=sentinel;
            size-=1;
            return item;
        }
    }

    public T removeLast(){
        if(isEmpty()){
            return null;
        }else{
            T item = sentinel.pre.item;
            sentinel.pre=sentinel.pre.pre;
            sentinel.pre.next=sentinel;
            size-=1;
            return item;
        }
    }

    public T get(int index){
        if(index>size()||index<0){
            return null;
        }else{
            Node current=sentinel.next;
            for(int i=0;i<index;i+=1){
                current=current.next;
            }
            return current.item;
        }
    }
}
