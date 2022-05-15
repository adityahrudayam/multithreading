package data_sharing_between_threads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VolatileKeyword {

    private final static int noOfThreads = 2;

    public static void main(String[] args) throws InterruptedException {
        MissileLauncher launcher = new MissileLauncher();
        Thread countThread = new Thread(launcher::countDown);
        Thread launcherThread = new Thread(launcher::launch);

        long start = System.currentTimeMillis();

        countThread.start();
        launcherThread.start();

        countThread.join();
        launcherThread.join();

        long end = System.currentTimeMillis();
        System.out.println(end - start);

        VolatileData volatileData = new VolatileData();     //object of VolatileData class
        Thread[] threads = new Thread[noOfThreads];     //creating Thread array
        for (int i = 0; i < noOfThreads; ++i)
            threads[i] = new VolatileThread(volatileData);
        for (int i = 0; i < noOfThreads; ++i)
            threads[i].start();                 //starts all reader threads
        for (int i = 0; i < noOfThreads; ++i)
            threads[i].join();                  //wait for all threads
    }

    public static class MissileLauncher {
        private final Lock lock = new ReentrantLock();
        private int count = 10000;
        private boolean flag = false;

        public void countDown() { // write to count => non-atomic ops and requires a lock
            lock.lock();
            try {
                while (count > 0) {
                    System.out.println("CountDown: " + count);
                    count--;
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } finally {
                lock.unlock();
                flag = true;
            }
        }

        public void launch() { // launch the rocket after the count-down
            while (!flag) { // here, don't need a volatile keyword.
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Launching now!");
        }
    }

    public static class VolatileData {
        private volatile int counter = 0; // here, also don't need the volatile keyword

        public int getCounter() {
            return counter;
        }

        public void increaseCounter() {
            ++counter;      //increases the value of counter by 1
        }
    }

    public static class VolatileThread extends Thread {
        private final VolatileData data;

        public VolatileThread(VolatileData data) {
            this.data = data;
        }

        @Override
        public void run() {
            int oldValue = data.getCounter();
            System.out.println("[Thread " + Thread.currentThread().getId() + "]: Old value = " + oldValue);
            data.increaseCounter();
            int newValue = data.getCounter();
            System.out.println("[Thread " + Thread.currentThread().getId() + "]: New value = " + newValue);
        }
    }

}
