package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class DequeTest {

    @Test
    /** 测试基本的添加和删除逻辑 */
    public void testAddRemove() {
        Deque<Integer> dq = new ArrayDeque<>();
        assertTrue(dq.isEmpty());

        dq.addFirst(10); // [10]
        dq.addLast(20);  // [10, 20]
        dq.addFirst(5);  // [5, 10, 20]

        assertEquals(3, dq.size());
        assertEquals(Integer.valueOf(5), dq.removeFirst()); // [10, 20]
        assertEquals(Integer.valueOf(20), dq.removeLast());  // [10]
        assertEquals(1, dq.size());
    }

    @Test
    /** 测试循环索引：连续 addFirst 触发指针回绕 */
    public void testCircularIndex() {
        Deque<Integer> dq = new ArrayDeque<>();
        // 假设初始容量是 8，我们添加 10 个元素触发扩容和回绕
        for (int i = 0; i < 10; i++) {
            dq.addFirst(i);
        }
        assertEquals(10, dq.size());
        assertEquals(Integer.valueOf(9), dq.get(0));
        assertEquals(Integer.valueOf(0), dq.get(9));
    }

    @Test
    /** 边界情况：对空队列进行操作 */
    public void testEmptyDeque() {
        Deque<String> dq = new ArrayDeque<>();
        assertNull("空队列 removeFirst 应返回 null", dq.removeFirst());
        assertNull("空队列 removeLast 应返回 null", dq.removeLast());
        assertNull("空队列 get 应返回 null", dq.get(0));
        assertEquals(0, dq.size());
    }

    @Test
    /** 测试迭代器是否能正常工作 */
    public void testIterator() {
        Deque<Integer> dq = new ArrayDeque<>();
        dq.addLast(1);
        dq.addLast(2);
        dq.addLast(3);

        int count = 0;
        int sum = 0;
        for (int i : dq) {
            sum += i;
            count++;
        }
        assertEquals(3, count);
        assertEquals(6, sum);
    }
}