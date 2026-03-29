package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Comparator;

public class MaxArrayDequeTest {

    @Test
    public void testMaxBasic() {
        // 创建一个比较 Integer 大小的比较器
        Comparator<Integer> intComparator = Integer::compare;
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(intComparator);

        mad.addLast(1);
        mad.addLast(5);
        mad.addLast(3);

        assertEquals(Integer.valueOf(5), mad.max());
    }

    @Test
    public void testMaxEmpty() {
        Comparator<Integer> intComparator = Integer::compare;
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(intComparator);

        assertNull("Max of empty deque should be null", mad.max());
    }

    @Test
    public void testMaxWithDifferentComparator() {
        // 自定义比较器：比如我们只想比较数字的绝对值，或者按字符串长度比较
        Comparator<String> lengthComparator = (s1, s2) -> s1.length() - s2.length();
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(lengthComparator);

        mad.addLast("a");
        mad.addLast("abc");
        mad.addLast("ab");

        // 长度最长的是 "abc"
        assertEquals("abc", mad.max());
    }
}