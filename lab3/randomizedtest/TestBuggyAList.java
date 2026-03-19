package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Assert;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */

public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        BuggyAList<Integer>BAL=new BuggyAList<>();
        AListNoResizing<Integer>AL=new AListNoResizing<>();
        for(int i=4;i<=6;i++){
            BAL.addLast(i);
            AL.addLast(i);
        }
        assertEquals(AL.size(),BAL.size());
        for(int j=0;j<3;j++){
            assertEquals(AL.removeLast(),BAL.removeLast());
        }
    }

    @Test
    public  void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer>B=new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                //System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int Bsize=B.size();
                //System.out.println("size: " + size);
                //System.out.println("Bsize: " + Bsize);
            }else if(operationNumber==2){
                if(L.size()==0||B.size()==0){
                    continue;
                }else{
                    int lastnum=L.getLast();
                    int Blastnum=B.getLast();
                    //System.out.println("getLastone("+lastnum+")");
                    //System.out.println("BgetLastone("+Blastnum+")");
                }
            }else if(operationNumber==3){
                if(L.size()==0||B.size()==0){
                    continue;
                }else{
                    int removedone=L.removeLast();
                    int Bremovedone=B.removeLast();
                   //System.out.println("removedone("+removedone+")");
                    //System.out.println("Bremovedone("+Bremovedone+")");
                }
            }
        }
    }
}
