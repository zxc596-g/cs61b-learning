package flik;

import org.junit.Test;

import static flik.Flik.isSameNumber;
import static org.junit.Assert.*;


public class FlikTest {
    @Test
    public void test1() {
        int a1 = 1;
        int a2 = 2;
        int a3 = 1;
        boolean t1 = isSameNumber(a1, a2);
        boolean t2 = isSameNumber(a1, a3);
        assertFalse(t1);
        assertTrue(t2);
    }

    @Test
    public void test2() {
        int a = 128;
        int b = 128;
        boolean t = isSameNumber(a, b);
        assertTrue(t);
    }
}
