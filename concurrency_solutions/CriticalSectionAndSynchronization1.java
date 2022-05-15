package concurrency_solutions;

public class CriticalSectionAndSynchronization1 {
    public static void main(String[] args) throws InterruptedException {
        /* We're now solving the concurrency issue that we saw previously in the data-sharing module.
         The code which needs to be treated as atomic process is called critical-section, and it's only executed by
         a single thread at a time. This can be achieved in code by using "synchronized" keyword either before the
         method or separately before the section beginning. Let's see the 2 implementations in the 2 classes/sections. */

        InventoryCounterPartOne inventoryCounter = new InventoryCounterPartOne();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();
        incrementingThread.join();
        decrementingThread.join();
        System.out.println("We currently have " + inventoryCounter.getItems() + " items");

        /* this is the first implementation: using "synchronized" keyword before the member method of a class. Doing this
         will stop the thread B to enter into any of the synchronized methods while the other is implementing those.
         Doing this has a drawback: It will not let the thread B to execute the other method which could've been executed
         simultaneously along with thread A and hence decreases the performance of the application. This is the reason
         we need the approach 2 shown in the next part, where only a certain part of the member method is locked for
         a single thread and the other thread can execute the other remaining method's critical section without a problem! */
    }

    private static class DecrementingThread extends Thread {
        private final InventoryCounterPartOne inventoryCounter;

        public DecrementingThread(InventoryCounterPartOne inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }

    private static class IncrementingThread extends Thread {
        private final InventoryCounterPartOne inventoryCounter;

        public IncrementingThread(InventoryCounterPartOne inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.increment();
            }
        }
    }

    public static class InventoryCounterPartOne {
        private int items = 0;

        // Here the locking object is "this" object instance itself.
        public synchronized void increment() { // this is just like doing: synchronized(this){ items++; } and synchronized(this){ items--; }
            items++;
        }

        public synchronized void decrement() {
            items--;
        }

        public int getItems() { // this is an atomic operation, so it doesn't require synchronization!
            return items;
        }
    }
}
