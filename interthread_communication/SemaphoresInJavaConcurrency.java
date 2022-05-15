package interthread_communication;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SemaphoresInJavaConcurrency {

    // below is the demonstration of using a semaphore to limit the no of atm withdrawals at a time to 4 though several
    // requests are made concurrently!

    public static void main(String[] args) throws InterruptedException {
        ATM atm = new ATM();
        // money adder thread
        ATMMoneyAdder thread = new ATMMoneyAdder(atm);
        thread.setDaemon(true); // otherwise, the money-adder thread will keep running and will not let the app exit.
        thread.start();
        // withdrawing customers
        Semaphore semaphore = new Semaphore(Runtime.getRuntime().availableProcessors() / 2);
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // core no of concurrent customers
        for (int i = 0; i < 40; i++) {
            service.execute(new AccessService(semaphore, atm));
        }
        service.shutdown();
        service.awaitTermination(5, TimeUnit.SECONDS);
    }

    public static class AccessService implements Runnable {
        private final Semaphore semaphore;
        private final Random random = new Random();
        private final ATM atm;

        public AccessService(Semaphore semaphore, ATM atm) {
            this.semaphore = semaphore;
            this.atm = atm;
        }

        @Override
        public void run() {
            // NOTE: without a semaphore, this service could be called by core no of threads concurrently!
            // hence, semaphore is used to limit this to a specified number of permits at a time.

            // some processing
            try {
                semaphore.acquire();
                // IO calls to the slow service - make a transaction
                int amount = random.nextInt(1000);
                int machine = random.nextInt(4);
                if (atm.canWithdraw(amount, machine)) {
                    atm.withdraw(amount, machine);
                    System.out.println("Withdrawn " + amount + " from machine " + machine);
                    // atm.getBalance(machine);
                } else {
                    System.out.println("Not Enough money in machine " + machine);
                }
                Thread.sleep(1000); // to notice the limiting clearly!
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        public Semaphore getSemaphore() {
            return semaphore;
        }

        public ATM getATM() {
            return atm;
        }
    }

    public static class ATMMoneyAdder extends Thread {
        private final ATM atm;
        private final Random random = new Random();

        public ATMMoneyAdder(ATM atm) {
            this.atm = atm;
        }

        @Override
        public void run() {
            while (true) {
                for (int m = 0; m < 4; m++) {
                    atm.addToMachine(1000 + random.nextInt(1000), m);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class ATM {
        private final List<BigInteger> machines;
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        Lock readLock = rwLock.readLock();
        Lock writeLock = rwLock.writeLock();

        public ATM() {
            machines = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                machines.add(BigInteger.ZERO);
            }
        }

        public void addToMachine(int cash, int machine) {
            writeLock.lock();
            try {
                machines.set(machine, machines.get(machine).add(BigInteger.valueOf(cash)));
            } finally {
                writeLock.unlock();
            }
        }

        public boolean canWithdraw(int amount, int machine) {
            readLock.lock();
            try {
                return this.machines.get(machine).compareTo(BigInteger.valueOf(amount)) >= 0;
            } finally {
                readLock.unlock();
            }
        }

        public int withdraw(int amount, int machine) {
            writeLock.lock();
            try {
                machines.set(machine, machines.get(machine).subtract(BigInteger.valueOf(amount)));
                return amount;
            } finally {
                writeLock.unlock();
            }
        }

        public void getBalance(int machine) {
            readLock.lock();
            try {
                System.out.println("Money remaining in the machine " + machine + " is " + machines.get(machine));
            } finally {
                readLock.unlock();
            }
        }

        public List<BigInteger> getMachines() {
            return machines;
        }
    }

}
