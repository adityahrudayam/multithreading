package executors_service;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class AdvancedCustomThreadPooling {
    // 1. Fixed Thread Pool 2. Cached Thread Pool 3. Scheduled Thread Pool 4. Single Threaded Executor

    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService service1 = new ThreadPoolExecutor(cores, cores, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), new ThreadPoolExecutor.AbortPolicy());
        IntStream.of(1000).forEach(itr -> service1.execute(new Task()));
        ExecutorService service2 = new ThreadPoolExecutor(0, cores, 30, TimeUnit.SECONDS, new SynchronousQueue<>(), new ThreadPoolExecutor.CallerRunsPolicy());
        IntStream.of(1000).forEach(itr -> service2.execute(new Task()));
        ScheduledExecutorService service3 = Executors.newScheduledThreadPool(cores);
        IntStream.of(3).forEach(itr -> service3.scheduleWithFixedDelay(new Task(), 10, 5, TimeUnit.SECONDS));
        ExecutorService service4 = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
        IntStream.of(1000).forEach(itr -> service4.execute(new Task()));
    }

    private static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println("Thread running currently: " + Thread.currentThread().getName());
        }
    }
}
