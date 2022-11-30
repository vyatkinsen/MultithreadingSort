import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int[] sizesOfArrays = {1000, 100_000, 1000_000, 10_000_000};
        for (int size: sizesOfArrays) {
            int[] arrayOfRandomNumbers = new int[size];
            Random random = new Random();
            for (int i = 0; i < size; i++) arrayOfRandomNumbers[i] = random.nextInt(size);
            singleThreadSorting(arrayOfRandomNumbers.clone());
            multiThreadSorting(arrayOfRandomNumbers.clone());
        }
    }

    public static void singleThreadSorting(int[] testingArray) throws InterruptedException {
        long startOfSortingTime = System.currentTimeMillis();

        Sorter oneThreadSort = new Sorter(testingArray);
        oneThreadSort.start();
        oneThreadSort.join();

        long sortingTime = System.currentTimeMillis() - startOfSortingTime;
        System.out.println("Sorting time for " + testingArray.length +
                " elements with " + 1 + " thread is " + (double) sortingTime / 1000);
    }

    public static int[] multiThreadSorting(int[] inputArray) throws InterruptedException {
        int sizeOfArray = inputArray.length;
        int[] arrayToSort = inputArray;
        long startOfSortingTime;
        int[] lastSubArr = new int[0];
        for (int threadPoolSize = 2; threadPoolSize <= 16; threadPoolSize *= 2) {
            startOfSortingTime = System.currentTimeMillis(); // Начало отсчёта времени

            // Этап "Разделения"
            int subSize = sizeOfArray / threadPoolSize;
            int[][] subArrays = new int[threadPoolSize - 1][subSize];
            lastSubArr = new int[sizeOfArray - (threadPoolSize - 1) * subSize];
            Sorter[] threads = new Sorter[threadPoolSize];

            // Этап "Сортировки"
            for (int subArrIndex = 0; subArrIndex < threadPoolSize - 1; subArrIndex++) {
                System.arraycopy(arrayToSort, subSize * subArrIndex, subArrays[subArrIndex],0, subSize);
                threads[subArrIndex] = new Sorter(subArrays[subArrIndex]);
                threads[subArrIndex].start();
            }
            System.arraycopy(arrayToSort, subSize * (threadPoolSize - 1), lastSubArr, 0, sizeOfArray - (threadPoolSize - 1) * subSize);
            threads[threadPoolSize - 1] = new Sorter(lastSubArr);
            threads[threadPoolSize - 1].start();

            for (int i = 0; i < threadPoolSize; i++) threads[i].join();

            for (int i = 0; i < threadPoolSize - 1; i++) {
                System.arraycopy(threads[i].getArrayToSort(), 0, subArrays[i], 0, subSize);
            }
            System.arraycopy(threads[threadPoolSize - 1].getArrayToSort(), 0, lastSubArr, 0, sizeOfArray - (threadPoolSize - 1) * subSize);

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
                lastSubArr = Arrays.copyOf(lastSubArr, sizeOfArray - currentSubSize * (numberOfArraysForCurrentStage - 1));

                for (int i = 0; i < numberOfArraysForCurrentStage - 1; i++) {
                    System.arraycopy(mergers[i].getRes(), 0, subArrays[i], 0, currentSubSize);
                }
                System.arraycopy(mergers[numberOfArraysForCurrentStage - 1].getRes(), 0, lastSubArr, 0, sizeOfArray - currentSubSize * (numberOfArraysForCurrentStage - 1));
                lastSubSize = currentSubSize;
                numberOfArraysForLastStage = numberOfArraysForCurrentStage;
            }

            long sortingTime = System.currentTimeMillis() - startOfSortingTime; // Конец подсчёта времени
            System.out.println("Sorting time for " + sizeOfArray +
                    " elements with " + threadPoolSize + " threads is " + (double) sortingTime / 1000);
        }
    return  lastSubArr;
    }
}
