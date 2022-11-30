import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int[] testingSizes = {1000, 100_000, 1000_000, 10_000_000, 100_000_000};
        for (int size: testingSizes) {
            int[] res = new int[size];
            Random random = new Random();
            for (int i = 0; i < size; i++) res[i] = random.nextInt(size);
            singleThreadSorting(res.clone());
            multiThreadSorting(res.clone());
        }
    }

    public static void singleThreadSorting(int[] testingArray) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        Sorter oneThreadSort = new Sorter(testingArray);
        oneThreadSort.start();
        oneThreadSort.join();

        long elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Sorting time for " + testingArray.length +
                " elements with " + 1 + " thread is " + (double) elapsedTime / 1000);
    }

    public static void multiThreadSorting(int[] inputArray) throws InterruptedException {
        int arrSize = inputArray.length;
        int[] testingArray = inputArray.clone();
        long startTime;
        long elapsedTime;

        for (int threadPoolSize = 2; threadPoolSize <= 16; threadPoolSize *= 2) {

            startTime = System.currentTimeMillis(); // Начало отсчёта времени

            // Этап "Разделения"
            int subSize = arrSize / threadPoolSize;
            int[][] subArrays = new int[threadPoolSize - 1][subSize];
            int[] lastSubArr = new int[arrSize - (threadPoolSize - 1) * subSize];
            Sorter[] threads = new Sorter[threadPoolSize];

            // Этап "Сортировки"
            for (int subArrIndex = 0; subArrIndex < threadPoolSize - 1; subArrIndex++) {
                System.arraycopy(testingArray, subSize * subArrIndex, subArrays[subArrIndex],0, subSize);
                threads[subArrIndex] = new Sorter(subArrays[subArrIndex]);
                threads[subArrIndex].start();
            }
            System.arraycopy(testingArray, subSize * (threadPoolSize - 1), lastSubArr, 0, arrSize - (threadPoolSize - 1) * subSize);
            threads[threadPoolSize - 1] = new Sorter(lastSubArr);
            threads[threadPoolSize - 1].start();

            for (int i = 0; i < threadPoolSize; i++) threads[i].join();


            for (int i = 0; i < threadPoolSize - 1; i++) {
                System.arraycopy(threads[i].getSortableArr(), 0, subArrays[i], 0, subSize);
            }
            System.arraycopy(threads[threadPoolSize - 1].getSortableArr(), 0, lastSubArr, 0, arrSize - (threadPoolSize - 1) * subSize);

            // Этап "Слияния"
            int lastSubSize = subSize;
            int numberOfArraysForLastStage = threadPoolSize;

            while (numberOfArraysForLastStage > 1) {
                int numberOfArraysForCurrentStage = numberOfArraysForLastStage / 2;
                Merger[] mergers = new Merger[numberOfArraysForLastStage / 2];

                for (int i = 0; i < numberOfArraysForCurrentStage - 1; i++) {
                    mergers[i] = new Merger(subArrays[2 * i], subArrays[2 * i + 1]);
                    mergers[i].start();
                }

                mergers[numberOfArraysForCurrentStage - 1] = new Merger(subArrays[(numberOfArraysForCurrentStage - 1) * 2], lastSubArr);
                mergers[numberOfArraysForCurrentStage - 1].start();

                for (int i = 0; i < numberOfArraysForCurrentStage; i++) {
                    mergers[i].join();
                }

                int currentSubSize = 2 * lastSubSize;
                subArrays = Arrays.copyOf(subArrays, numberOfArraysForCurrentStage);

                for (int i = 0; i < numberOfArraysForCurrentStage - 1; i++) {
                    subArrays[i] = Arrays.copyOf(subArrays[i], currentSubSize);
                }
                lastSubArr = Arrays.copyOf(lastSubArr, arrSize - currentSubSize * (numberOfArraysForCurrentStage - 1));

                for (int i = 0; i < numberOfArraysForCurrentStage - 1; i++) {
                    System.arraycopy(mergers[i].getRes(), 0, subArrays[i], 0, currentSubSize);
                }
                System.arraycopy(mergers[numberOfArraysForCurrentStage - 1].getRes(), 0, lastSubArr, 0, arrSize - currentSubSize * (numberOfArraysForCurrentStage - 1));
                lastSubSize = currentSubSize;
                numberOfArraysForLastStage = numberOfArraysForCurrentStage;
            }

            elapsedTime = System.currentTimeMillis() - startTime; // Конец подсчёта времени
            System.out.println("Sorting time for " + arrSize +
                    " elements with " + threadPoolSize + " threads is " + (double) elapsedTime / 1000);
        }

    }
}
