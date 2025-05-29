import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Main {
    static final int ARRAY_SIZE = 10000000;
    static int[] array = new int[ARRAY_SIZE];

    static int min = Integer.MAX_VALUE;
    static int minIndex = -1;

    static final Object lock = new Object();

    public static void main(String[] args) {
        generateArray();

        int numThreads = 4;
        int chunkSize = ARRAY_SIZE / numThreads;

        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? ARRAY_SIZE : start + chunkSize;

            Thread thread = new Thread(() -> {
                int localMin = Integer.MAX_VALUE;
                int localIndex = -1;

                for (int j = start; j < end; j++) {
                    if (array[j] < localMin) {
                        localMin = array[j];
                        localIndex = j;
                    }
                }

                synchronized (lock) {
                    if (localMin < min) {
                        min = localMin;
                        minIndex = localIndex;
                    }
                }

                latch.countDown();
            });

            thread.start();
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Min value: " + min + ", at index: " + minIndex);
    }

    static void generateArray() {
        Random rand = new Random();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = rand.nextInt(10_000); //
        }
        int negIndex = rand.nextInt(ARRAY_SIZE);
        array[negIndex] = -rand.nextInt(1000);
    }
}
