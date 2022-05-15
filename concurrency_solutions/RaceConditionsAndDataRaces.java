package concurrency_solutions;

public class RaceConditionsAndDataRaces {
    /* Race Condition: 2 or more separate threads trying to modify a shared resource will lead to a race condition.
     The core of the problem is the execution of non-atomic operations on a shared resource.
     Solutions: 1. Identification of the critical section where the race condition is happening.
     2. Protection of the critical section by a synchronized block.

     Data Race: 2 or more separate threads implementing an independent lines of code will cause the CPU/Compiler to
     run the lines of code in a random order to optimize the hardware units to improve performance of the application
     and hence any conditions depending on the order of the execution of the events will collapse/breakdown/malfunction.
     Below is the example for data race: */

    public static void main(String[] args) {
        SharedClass obj = new SharedClass();
        Thread threadA = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                obj.increment();
            }
        });
        Thread threadB = new Thread(() -> {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                obj.checkForDataRace();
            }
        });
        threadA.start();
        threadB.start();
    }


    public static class SharedClass {
        private int x = 0; // use volatile here for the variables to prevent data-race.
        private int y = 0;

        public void increment() {
            x++;
            y++;
        }

        public void checkForDataRace() {
            if (y > x) {
                System.out.println("Y > X - data race detected!");
            }
        }
    }

    /* This happens due to the CPU executing the instructions in an order which uses the hardware units to the fullest
     as well as maintaining the logic of the code instructions! Hence, wherever there's a dependency btw the lines of
     code, there the CPU executes the instructions as per order to not disturb the code logic and if the lines of code
     are independent of each other, then the CPU executes them in an order to optimize the performance.

     To solve this, put "volatile" keyword before the variables where you want the order to be maintained. You can also
     use a "synchronized" keyword to only let a single thread access the method at any given time, but this reduces the
     performance as there's no race condition in the method but still parallelism is being hindered doing this! hence,
     the best solution to this problem would be to use the "volatile" keyword which ensures whatever code comes before
     the variable modification line executes before the mod line and whatever comes after the mod line in the code also
     executes after the mod line's execution!

     there "volatile" keyword helps to atomize the read/write to 64-bit primitives like long/double eliminating
     race condition there and helps in eliminating the data race by maintaining the order of the code execution. */
}
