package data_sharing_between_threads;

public class DataSharingAndCriticalSections {
    public static void main(String[] args) throws InterruptedException {
        // Let's perform 2 experiments with 2 different objects
        InventoryCounter obj1 = new InventoryCounter(); // inventory-counter 1

        // Exp1: We first perform increment operations in thread A and make the thread B wait till A finishes
        // and then execute thread B which performs decrement operations

        IncrementingThread threadA = new IncrementingThread(obj1);
        DecrementingThread threadB = new DecrementingThread(obj1);

        // let's first run thread A before running thread B
        threadA.start();
        threadA.join(); // making the thread B wait till thread A finished its execution
        threadB.start();
        threadB.join(); // making the main thread wait till thread B finished its execution

        System.out.println("We currently have " + obj1.getItems() + " items"); // result = 10k increments by thread A + 10k decrements by thread B => 0 result

        // Exp2: We now don't wait for the finishing of thread A and start the execution of thread B concurrently
        InventoryCounter obj2 = new InventoryCounter(); // inventory-counter 2

        threadA = new IncrementingThread(obj2);
        threadB = new DecrementingThread(obj2);

        threadA.start();
        threadB.start(); // both running concurrently
        threadA.join();
        threadB.join(); // the main thread is now waiting for the 2 threads to finish execution

        System.out.println("We currently have " + obj2.getItems() + " items"); // Here, we see the problem in resource-sharing
        // Incrementing/Decrementing the items' member variable is not an atomic task (i.e, doesn't take place in a single op)
        // It first fetches the value (STEP-1), then increment/decrements the value (STEP-2), finally re-assigns the new value to the old variable (STEP-3).

        // hence, due to the above non-atomic task steps, any thread may execute it's new step before or after one of the 3 steps
        // of the other thread, hence leading to a completely different assignment to the variable. This leads to a different value than we expected.
    }

    private static class DecrementingThread extends Thread {
        private final InventoryCounter obj;

        public DecrementingThread(InventoryCounter obj) {
            this.obj = obj;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                obj.decrement();
            }
        }
    }

    private static class IncrementingThread extends Thread {
        private final InventoryCounter obj;

        public IncrementingThread(InventoryCounter obj) {
            this.obj = obj;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                obj.increment();
            }
        }
    }

    private static class InventoryCounter {
        private int items = 0;

        public void increment() {
            items++;
        }

        public void decrement() {
            items--;
        }

        public int getItems() {
            return this.items;
        }
    }
}
