package concurrency_solutions;

public class CriticalSectionAndSynchronization2 {
    public static void main(String[] args) throws InterruptedException {
        // We're now solving the concurrency issue using approach 2: declaring only some parts of the method critical
        // and using different lock objects to not prevent parallelism or to prevent thread obstruction.

        InventoryCounterPartOne inventoryCounter = new InventoryCounterPartOne();
        IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
        DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();
        incrementingThread.join();
        decrementingThread.join();
        System.out.println("We currently have " + inventoryCounter.getItems() + " items");

        // Here, thread A executes increment method entirely and thread B executes decrement method entirely
        // but only after the thread A finished its "increment" method!
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

        public void increment() { // non-atomic ops are now synchronized to be only executed fully by one thread at a time!
            synchronized (this) {
                items++;
            }
        }

        public void decrement() {
            synchronized (this) { // critical sections
                items--;
            }
        }

        public int getItems() { // this is an atomic operation, so it doesn't require synchronization!
            return items;
        }
    }
}
