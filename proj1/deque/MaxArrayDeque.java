package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private final Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c){
        comparator=c;
    }

    public T max(){
        if(isEmpty())
            return null;
        T maxitem=get(0);
        for(T item:this){
            if(comparator.compare(maxitem,item)<0)
                maxitem=item;
        }
        return maxitem;
    }

    public T max(Comparator<T> c){
        if(isEmpty())
            return null;
        T maxitem=get(0);
        for(T item:this){
            if(c.compare(maxitem,item)<0)
                maxitem=item;
        }
        return maxitem;
    }
}
