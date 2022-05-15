package thread_coordination;

import java.math.BigInteger;

public class ThreadTerminationAndDaemonThread {
    /* Thread consumes: Memory and Kernel performance_optimisations.resources, CPU cycles and cache memory
     Hence, we would want to clean the thread's performance_optimisations.resources if the application is still running and
     thread finished its execution.
     If a thread is misbehaving, we would want to stop it
     Application will not stop running until all of its threads are done executing their tasks and terminate.

     Methods: Thread termination, daemon threads, Thread.interrupt()

     When can we interrupt threads: when the thread is executing a method that throws an InterruptedException
     and when the thread's code is handling the interrupt signal explicitly. */

    public static void main(String[] args) {
        //part-1
        Thread thread = new Thread(new BlockingTask());
        thread.start();
        thread.interrupt();
        // part-2
        Thread thread2 = new Thread(new LongComputation(new BigInteger("200000"), new BigInteger("1000000000")));
        thread2.setDaemon(true); // if we want the application to terminate after the main thread finished its execution,
        // regardless of the other threads running, we've to make them daemon threads.
        thread2.start();
        thread2.interrupt();
    }

    private static class BlockingTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(500000);
            } catch (InterruptedException e) {
                System.out.println("Exiting blocking task!");
            }
        }
    }

    private static class LongComputation implements Runnable {
        private final BigInteger base;
        private final BigInteger power;

        public LongComputation(BigInteger base, BigInteger power) {
            this.base = base;
            this.power = power;
        }

        @Override
        public void run() {
            System.out.println(base + "^" + power + "=" + pow(base, power));
        }

        private BigInteger pow(BigInteger base, BigInteger power) {
            BigInteger result = BigInteger.ONE;
            for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
                /* if (Thread.currentThread().isInterrupted()) {
                if we want to skip this, we've to use the daemon thread property
                to terminate the application after the main finished its execution.
                System.out.println("Prematurely interrupted computation!");
                return BigInteger.ZERO;
                } */
                result = result.multiply(base);
            }
            return result;
        }
    }

    /* daemon thread:-
     background threads that do not prevent the application from exiting/terminating if the main thread terminates.
     we use daemon threads in the cases like:-
     1.background tasks, that should not block our application from terminating. Ex. file saving thread in a text editor
     2.code in the worker thread is not in our control, and we don't want it to block our application from terminating.
     Ex. Worker thread that uses an external library. */
}
