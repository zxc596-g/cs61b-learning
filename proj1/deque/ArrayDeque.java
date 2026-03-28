package deque;

import org.junit.Test;

import java.util.Iterator;

public class ArrayDeque<T> /*implements Iterable<T>*/{
    private T[] items;
    private int size;
    private int length;
    private int nextLast;
    private int nextFirst;

    public ArrayDeque(){
        length=8;
        items=(T[]) new Object [length];
        size=0;
        nextLast=0;
        nextFirst=length-1;
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        if(size==0){
            return true;
        }else{
            return false;
        }
    }

    public void resize(){
        int start=(nextFirst+1)%length;
        if(size==length){
            T[] newitems=(T[]) new Object[length*2];
            System.arraycopy(items,start,newitems,0,length-start);
            if(length-start!=size){
                System.arraycopy(items,0,newitems,length-start,start);
            }
            length*=2;
            nextLast=size;
            nextFirst=length-1;
            items=newitems;
        }
        else if(size<=length/4&&length>16){
            T[]newitems=(T[])new Object[length/2];
            if(length-start>=size){
                System.arraycopy(items,start,newitems,0,size);
            }else{
                System.arraycopy(items,start,newitems,0,length-start);
                System.arraycopy(items,0,newitems,length-start,size-(length-start));
            }
            length/=2;
            nextLast=size;
            nextFirst=length-1;
            items=newitems;
        }else{
            return;
        }
    }

    public void addFirst(T item){
        if(item==null)
            return;
        this.resize();
        items[nextFirst]=item;
        nextFirst=(nextFirst-1+length)%length;
        size++;
    }

    public void addLast(T item){
        if(item==null)
            return;
        this.resize();;
        items[nextLast]=item;
        nextLast=(nextLast+1)%length;
        size++;
    }

    public void printDeque(){
        for(int i=0;i<size;i++){
            System.out.print(items[(nextFirst+1+i)%length]+" ");
        }
        System.out.println();
    }

    public T removeFirst(){
        if(size==0)
            return null;
        int start=(nextFirst+1)%length;
        T item=items[start];
        items[start]=null;
        nextFirst=start;
        size--;
        resize();
        return item;
    }

    public T removeLast(){
        if(size==0)
            return null;
        nextLast=(nextLast-1+length)%length;
        T item=items[nextLast];
        items[nextLast]=null;
        size--;
        resize();
        return item;
    }

    public T get(int index){
        if(index>=size||index<0)
            return null;
        return items[(nextFirst+1+index)%length];
    }

//    private class ArrayIterator implements Iterator<T> {
//        private int wizPos;
//        public ArrayIterator(){
//            wizPos=0;
//        }
//        @Override
//        public boolean hasNext(){
//            return wizPos<size;
//        }
//        @Override
//        public T next(){
//            T returnItem =get(wizPos);
//            wizPos+=1;
//            return returnItem;
//        }
//    }
//    @Override
//    public Iterator<T> iterator(){
//        return new ArrayIterator();
//    }
//
//    @Test
//    public void test(){
//        ArrayDeque<Integer> ad =new ArrayDeque<>();
//        ad.addLast(4);
//        ad.addLast(8);
//        ad.addLast(6);
//        for(int x: ad){
//            System.out.println(x);
//        }
//    }

}

