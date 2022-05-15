package executors_service;

import java.util.concurrent.*;

public class ForkThreadPoolFibonacci {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Fibonacci fib = new Fibonacci(40);
        ForkJoinPool pool = new ForkJoinPool();
        Future<Integer> output = pool.submit(fib);
        System.out.println(output.get());
        System.out.println(fib.compute());
    }

    public static class Fibonacci extends RecursiveTask<Integer> {
        final int n;

        public Fibonacci(int x) {
            n = x;
        }

        @Override
        protected Integer compute() {
            if (n <= 1) return n;
            Fibonacci f1 = new Fibonacci(n - 1);
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);
            f2.fork();
            return f1.join() + f2.join();
        }
    }
}
