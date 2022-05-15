package advanced_locking;

import java.util.Random;

public class LockStrategiesAndDeadLock {
    /* We can either use a coarse-grain-locking or a fine-grain-locking. Using a coarse-grain-locking locks the entire
     shared resources to a single thread and therefore eliminates any race conditions. But this comes with the cost of
     performance, as only a single thread keeps executing at any given time and blocks parallelism therefore proving
     to be worse than a single threaded application which doesn't need any context switching or concurrency issues.
     Therefore, using fine-grain-locking provides more freedom and allows high parallelism and makes use of the
     multiple core processor. But this strategy introduces a new problem called "deadlock".

     deadlock: When there are multiple locks and the threads to execute the synchronized/critical sections get
     blocked due to a circular dependency, it's called a deadlock, where the threads wait in an infinite loop for
     each other to release the lock on the shared resources but due to the circular dependency, they keep waiting
     and stop performing any useful task.

     Solutions to deadlock:
     1. Avoid Circular Dependency (enforce a strict order in lock acquisitions).
     Ex: ThreadA: lock(A){ lock(B){ ...code } } and ThreadB: lock(A){ lock(B){ ...code } } - same order of locks
     However, in case of a large application, maintaining the order of lock acquisitions is tough and hence can use
     other techniques mentioned below:
     2. Using a Watchdog for deadlock detection
     3. Implementing the Watchdog on a separate thread to interrupt the threads where there's a
     deadlock (not possible with synchronized keyword)
     4. tryLock operations (checking if a thread is already acquired by another thread before actually trying to
     acquire a lock and possibly getting suspended. This operation is called try-lock). This is not possible with the
     synchronized keyword as it doesn't allow a suspended thread to be interrupted, nor does it have the tri-lock op!
     Hence, use of advanced locking techniques come into play like re-entrant locks, etc. */

    // Demonstration with a railroad traffic control system.

    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        Thread trainAThread = new Thread(new TrainA(intersection));
        Thread trainBThread = new Thread(new TrainB(intersection));
        trainAThread.start();
        trainBThread.start();
    }

    public static class TrainB implements Runnable {
        private final Intersection intersection;
        private final Random random = new Random();

        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intersection.takeRoadB();
            }
        }
    }

    public static class TrainA implements Runnable {
        private final Intersection intersection;
        private final Random random = new Random();

        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepingTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                intersection.takeRoadA();
            }
        }
    }

    public static class Intersection {
        private final Object roadA = new Object();
        private final Object roadB = new Object();

        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through road A");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void takeRoadB() {
            synchronized (roadB) { // must be solved by maintaining strict order of the lock acquisitions.
                System.out.println("Road B is locked by thread " + Thread.currentThread().getName());

                synchronized (roadA) {
                    System.out.println("Train is passing through road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
