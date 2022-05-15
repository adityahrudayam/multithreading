package lockfree_algos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class LockFreeDSA {
    public static void main(String[] args) throws InterruptedException {
//        StandardStack<Integer> stack = new StandardStack<>();
//        AdvancedBlockingStack<Integer> stack=new AdvancedBlockingStack<>();
        LockFreeStack<Integer> stack = new LockFreeStack<>();
        Random random = new Random();

        for (int i = 0; i < 100000; i++) {
            stack.push(random.nextInt());
        }

        List<Thread> threads = new ArrayList<>();

        int pushingThreads = 2;
        int poppingThreads = 2;

        for (int i = 0; i < pushingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });
            thread.setDaemon(true);
            threads.add(thread);
        }

        for (int i = 0; i < poppingThreads; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });
            thread.setDaemon(true);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(10000);
        System.out.printf("%,d operations were performed in 10 seconds %n", stack.getCounter());
    }

    private static class StandardStack<T> {
        private StackNode<T> head = null;
        private int counter = 0;

        public synchronized void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            newHead.next = head;
            head = newHead;
            counter++;
        }

        public synchronized T pop() {
            if (head == null) {
                counter++;
                return null;
            }

            T value = head.value;
            head = head.next;
            counter++;
            return value;
        }

        public int getCounter() { // atomic op already
            return counter;
        }
    }

    private static class AdvancedBlockingStack<T> {
        private StackNode<T> head = null;
        private int counter = 0;
        private final Lock lock = new ReentrantLock();

        public void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            lock.lock();
            try {
                newHead.next = head;
                head = newHead;
                counter++;
            } finally {
                lock.unlock();
            }
        }

        public T pop() {
            lock.lock();
            try {
                if (head == null) {
                    counter++;
                    return null;
                }

                T value = head.value;
                head = head.next;
                counter++;
                return value;
            } finally {
                lock.unlock();
            }
        }

        public int getCounter() {
            return counter;
        }
    }

    private static class LockFreeStack<T> {
        private final AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private final AtomicInteger counter = new AtomicInteger(0);

        public void push(T value) {
            StackNode<T> newHead = new StackNode<>(value);
            while (true) {
                StackNode<T> currentHead = head.get();
                newHead.next = currentHead;
                if (head.compareAndSet(currentHead, newHead)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                }
            }
            counter.incrementAndGet();
        }

        public T pop() {
            StackNode<T> currentHead = head.get();
            StackNode<T> newHead;
            while (currentHead != null) {
                newHead = currentHead.next;
                if (head.compareAndSet(currentHead, newHead)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                    currentHead = head.get();
                }
            }
            counter.incrementAndGet();
            return currentHead != null ? currentHead.value : null;
        }

        public int getCounter() {
            return counter.get();
        }
    }

    private static class StackNode<T> {
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
        }
    }
}
