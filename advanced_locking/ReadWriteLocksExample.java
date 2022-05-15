package advanced_locking;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLocksExample {
    // Re-entrant locks require an explicit locking and unlocking unlike the synchronized keyword.
    // Aim: implementing an inventory database using read-write lock(s).

    public static final int HIGHEST_PRICE = 1000;

    public static void main(String[] args) throws InterruptedException {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();

        Random random = new Random();
        for (int i = 0; i < 100000; i++) {
            inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        }

        Thread writer = new Thread(() -> {
            while (true) {
                inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
                inventoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        writer.setDaemon(true);
        writer.start();

        int numOfReaderThreads = 7;
        List<Thread> readers = new ArrayList<>();
        for (int readerIdx = 0; readerIdx < numOfReaderThreads; readerIdx++) {
            Thread reader = new Thread(() -> {
                for (int i = 0; i < 100000; i++) {
                    int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
                    int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
                    inventoryDatabase.getNumberOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
                }
            });

            reader.setDaemon(true);
            readers.add(reader);
        }

        long startReadingTime = System.currentTimeMillis();
        for (Thread reader : readers) {
            reader.start();
        }

        for (Thread reader : readers) {
            reader.join();
        }
        long endReadingTime = System.currentTimeMillis();
        System.out.printf("Reading took %d ms%n", endReadingTime - startReadingTime);
    }

    public static class InventoryDatabase {
        private final TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
        private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        private final Lock readLock = reentrantReadWriteLock.readLock();
        private final Lock writeLock = reentrantReadWriteLock.writeLock();

        public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
            readLock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                Integer toKey = priceToCountMap.floorKey(upperBound);
                if (fromKey == null || toKey == null) {
                    return 0;
                }
                NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);
                int sum = 0;
                for (int numOfItemsForPrice : rangeOfPrices.values()) {
                    sum += numOfItemsForPrice;
                }
                return sum;
            } finally {
                readLock.unlock();
            }
        }

        public void addItem(int price) {
            writeLock.lock();
            try {
                priceToCountMap.merge(price, 1, Integer::sum);
                /*
                    Integer numOfItemsForPrice = priceToCountMap.get(price);
                    if (numOfItemsForPrice == null) {
                        priceToCountMap.put(price, 1);
                    } else {
                        priceToCountMap.put(price, numOfItemsForPrice + 1);
                    }
                */
            } finally {
                writeLock.unlock();
            }
        }

        public void removeItem(int price) {
            writeLock.lock();
            try {
                Integer numOfItemsForPrice = priceToCountMap.get(price);
                if (numOfItemsForPrice == null || numOfItemsForPrice == 1) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, numOfItemsForPrice - 1);
                }
            } finally {
                writeLock.unlock();
            }
        }

        /* Code below is using re-entrant lock for both read & write operations - slow
        private final ReentrantLock lock = new ReentrantLock();

        public int getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
            lock.lock();
            try {
                Integer fromKey = priceToCountMap.ceilingKey(lowerBound);
                Integer toKey = priceToCountMap.floorKey(upperBound);
                if (fromKey == null || toKey == null) {
                    return 0;
                }
                NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey, true);
                int sum = 0;
                for (int numOfItemsForPrice : rangeOfPrices.values()) {
                    sum += numOfItemsForPrice;
                }
                return sum;
            } finally {
                lock.unlock();
            }
        }

        public void addItem(int price) {
            lock.lock();
            try {
                priceToCountMap.merge(price, 1, Integer::sum);
            } finally {
                lock.unlock();
            }
        }

        public void removeItem(int price) {
            lock.lock();
            try {
                Integer numOfItemsForPrice = priceToCountMap.get(price);
                if (numOfItemsForPrice == null || numOfItemsForPrice == 1) {
                    priceToCountMap.remove(price);
                } else {
                    priceToCountMap.put(price, numOfItemsForPrice - 1);
                }
            } finally {
                lock.unlock();
            }
        }
        */
    }

}
