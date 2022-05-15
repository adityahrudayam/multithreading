package thread_coordination;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadCoordinationJoiningThreads {

    /* Thread Coordination is important because, different threads run independently and the order of execution is out of control.
     if thread B is dependent on the other thread A, we shouldn't keep the thread B running a loop and keep checking
     everytime if thread A's result is ready or not, as this will slow down the thread B's performance. */
    public static void main(String[] args) throws InterruptedException {
        // we are calculating factorials of the given numbers
        List<Long> inputNumbers = Arrays.asList(0L, 3435L, 35435L, 2589L, 7877L, 24L, 2340L, 5644L);
        List<FactorialThread> threads = new ArrayList<>(inputNumbers.size());
        for (long i : inputNumbers) {
            threads.add(new FactorialThread(i));
        }

        for (FactorialThread thread : threads) {
            thread.start();
        }

        // the lines between the above "start" phase and the below "isFinished" phase are under race condition
        // i.e, to race towards their individual goals to finish, return the result and terminate.

        // we have to use thread.join() here to wait until all the threads finish their execution
        for (Thread thread : threads) {
            thread.join(2000);
        }

        for (int i = 0; i < inputNumbers.size(); i++) {
            FactorialThread factorialThread = threads.get(i);
            if (factorialThread.isFinished()) {
                System.out.println("Factorial of " + inputNumbers.get(i) + " is " + factorialThread.getResult());
            } else {
                System.out.println("The calculation for " + inputNumbers.get(i) + " is still in progress");
            }
        }
    }

    public static class FactorialThread extends Thread {
        private final long inputNumber;
        private boolean isFinished = false;
        private BigInteger result = BigInteger.ZERO;

        public FactorialThread(long inputNumber) {
            this.inputNumber = inputNumber;
        }

        @Override
        public void run() {
            result = factorial(inputNumber);
            isFinished = true;
        }

        public BigInteger factorial(long n) {
            BigInteger temp = BigInteger.ONE;
            for (long i = 2; i <= n; i++) {
                temp = temp.multiply(new BigInteger(Long.toString(i)));
            }
            return temp;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }
    }
}
