package threads_creation;

public class PartOneThreadCreationWithRunnableInterface {

     /* https://www.webopedia.com/definitions/cpu-time/ - reference
     The amount of time the CPU is actually executing instructions. During the execution of most programs, the CPU sits
     idle much of the time while the computer fetches data from the keyboard or disk, or sends data to an output device.
     The CPU time of an executing program, therefore, is generally much less than the total execution time of the program.
     Multitasking operating systems take advantage of this by sharing the CPU among several programs. CPU times are used
     for a variety of purposes: to compare the speed of two different processors, to gauge how CPU-intensive a program is,
     and to measure the amount of processing time being allocated to different programs in a multitasking environment.

     https://www.techopedia.com/definition/2858/cpu-time - reference
     CPU Time: CPU time is the exact amount of time that the CPU has spent processing data for a specific program or process.
     Programs and applications normally do not use the processor 100% of the time that they are running; some of that time
     is spent on I/O operations and fetching and storing data on the RAM or storage device. The CPU time is only when the
     program actually uses the CPU to perform tasks such as doing arithmetic and logic operations. CPU time is also known as processing time.

     Wiki def of CPU java.sql.Time: CPU time is the amount of time for which a central processing unit was used for processing instructions
     of a computer program or operating system, as opposed to elapsed time, which includes for example, waiting for input/output
     operations or entering low-power mode. The CPU time is measured in clock ticks or seconds.

     the OS allots the CPU time for a certain thread based on the "dynamic priority" of the thread.
     dynamic priority of a thread = static priority (decided by the programmer for every thread in his app) + bonus (OS specific) */

    public static void main(String[] args) throws InterruptedException {
        /* This is the method-1 for creating threads - using objects/instances of "anonymous class" (No name class for
         one time use or object creation) "implementing" (implements) the "Runnable" interface.
         Thread thread = new Thread(() -> {
            now we can use lambda functions
            code that will run in a new thread
            System.out.println("we are now in the thread " + Thread.currentThread().getName());
            System.out.println("current thread priority is " + Thread.currentThread().getPriority());
            throw new RuntimeException("Intentional Exception for testing.");
        }); */

        Thread thread = new Thread(new Runnable() { // can be replaced with above lambda function as well
            @Override
            public void run() {
                // code that will run in a new thread
                System.out.println("we are now in the thread " + Thread.currentThread().getName());
                System.out.println("current thread priority is " + Thread.currentThread().getPriority());
                throw new RuntimeException("Intentional Exception for testing.");
            }
        });
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { // setting an exception handler for the entire
            // thread at its inception, so that the thread doesn't bring down the entire process of the application.
            @Override
            public void uncaughtException(Thread t, Throwable e) { // catches uncaught exceptions to the thread which were not dealt with anywhere else
                System.out.println("A critical error happened in the thread: " + t.getName() + ". The error is " + e.getMessage());
            }
        });

        thread.setName("New Worker Thread"); // setting name of the thread
        thread.setPriority(Thread.MAX_PRIORITY); // setting "static priority" part of the dynamic priority(=static-priority + OS-Bonus)

        System.out.println("we are in the thread: " + Thread.currentThread().getName() + " before starting a new thread");
        thread.start();
        System.out.println("we are in the thread: " + Thread.currentThread().getName() + " after starting a new thread");
        Thread.sleep(1000);
    }
}
