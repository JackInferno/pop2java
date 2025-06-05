import java.util.Random;

public class Main {
    static final int ARRAY_SIZE = 10_000_000;
    static int[] array = new int[ARRAY_SIZE];

    static int min = Integer.MAX_VALUE;
    static int minIndex = -1;

    static final Object resultLock = new Object();
    static final Object threadCountLock = new Object();
    static int threadsDone = 0;

    public static void main(String[] args) {
        generateArray();

        int numThreads = 4;
        int chunkSize = ARRAY_SIZE / numThreads;

        for (int i = 0; i < numThreads; i++) {
            int start = i * chunkSize;
            int end = (i == numThreads - 1) ? ARRAY_SIZE : start + chunkSize;

            Thread thread = new Thread(() -> {
                findLocalMin(start, end);

                synchronized (threadCountLock) {
                    threadsDone++;
                    threadCountLock.notify();
                }
            });

            thread.start();
        }


        synchronized (threadCountLock) {
            while (threadsDone < numThreads) {
                try {
                    threadCountLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Min value: " + min + ", at index: " + minIndex);
    }

    static void generateArray() {
        Random rand = new Random();
        for (int i = 0; i < ARRAY_SIZE; i++) {
            array[i] = rand.nextInt(10_000);
        }
        array[rand.nextInt(ARRAY_SIZE)] = -rand.nextInt(1000);
    }

    static void findLocalMin(int start, int end) {
        int localMin = Integer.MAX_VALUE;
        int localIndex = -1;

        for (int i = start; i < end; i++) {
            if (array[i] < localMin) {
                localMin = array[i];
                localIndex = i;
            }
        }

        synchronized (resultLock) {
            if (localMin < min) {
                min = localMin;
                minIndex = localIndex;
            }
        }
    }
}
