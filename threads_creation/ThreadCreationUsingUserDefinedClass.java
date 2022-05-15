package threads_creation;

public class ThreadCreationUsingUserDefinedClass {
    /* The Thread class implements the Runnable interface itself. So we can directly implement the Runnable Interface
     and also create a new Thread by instantiating a new class Object whose class is extending the Thread class.
     TO MAKE IT EASY: Thread implements Runnable, and NEW_CLASS extends Thread => NEW_CLASS object has
     both Thread class methods and also implements the Runnable Interface having/overriding its methods as well! */
    public static void main(String[] args) throws InterruptedException {
        NewThread thread = new NewThread();
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) { // catches uncaught exceptions
                System.out.println("A critical error happened in the thread: " + t.getName() + ". The error is " + e.getMessage());
            }
        });
        thread.setName("Test-Thread"); // setting name of the thread
        thread.setPriority(Thread.MAX_PRIORITY); // setting "static priority" part of the dynamic priority(=static-priority + OS-Bonus)
        System.out.println("we are in the thread: " + Thread.currentThread().getName() + " before starting a new thread");
        thread.start();
        System.out.println("we are in the thread: " + Thread.currentThread().getName() + " after starting a new thread");
        System.out.println(thread instanceof Thread && thread instanceof Runnable);
        Thread.sleep(1000); // thread sleeps for 1 sec or 1000ms
    }

    static class NewThread extends Thread { // this class has properties of Thread class and implements Runnable interface
        int x;

        public NewThread() {
            this.x = 1;
        }

        @Override
        public void run() {
            // System.out.println("we are now in the thread " + Thread.currentThread().getName()); - instead of doing this, we can do the below now
            System.out.println("we are now in the thread " + this.getName());
            System.out.println("current thread priority is " + this.getPriority());
            throw new RuntimeException("Intentional Exception for testing.");
        }
    }

}
