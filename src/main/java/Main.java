import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int[] sizesOfArrays = {1000, 100_000, 1000_000, 10_000_000, 100_000_000};
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
        System.out.println("Массив из " + testingArray.length + " элементов сортировался " + 1 + " потоком " + (double) sortingTime / 1000 + " секунд");
    }

    public static void multiThreadSorting(int[] inputArray) throws InterruptedException {
        int sizeOfArray = inputArray.length;
        long startOfSortingTime;

        for (int countOfThreads = 2; countOfThreads <= 16; countOfThreads *= 2) {
            startOfSortingTime = System.currentTimeMillis();

            int lengthOfSubArrays = sizeOfArray / countOfThreads;
            int[][] subArrays = new int[countOfThreads - 1][lengthOfSubArrays];
            int[] lastSubArray = new int[sizeOfArray - (countOfThreads - 1) * lengthOfSubArrays];
            Sorter[] threads = new Sorter[countOfThreads];

            for (int indexOfSubArray = 0; indexOfSubArray < countOfThreads - 1; indexOfSubArray++) {
                System.arraycopy(inputArray, lengthOfSubArrays * indexOfSubArray, subArrays[indexOfSubArray],0, lengthOfSubArrays);
                threads[indexOfSubArray] = new Sorter(subArrays[indexOfSubArray]);
                threads[indexOfSubArray].start();
            }
            System.arraycopy(inputArray, lengthOfSubArrays * (countOfThreads - 1), lastSubArray, 0, sizeOfArray - (countOfThreads - 1) * lengthOfSubArrays);
            threads[countOfThreads - 1] = new Sorter(lastSubArray);
            threads[countOfThreads - 1].start();

            for (int i = 0; i < countOfThreads; i++) threads[i].join();
            for (int i = 0; i < countOfThreads - 1; i++) System.arraycopy(threads[i].getArrayToSort(), 0, subArrays[i], 0, lengthOfSubArrays);

            System.arraycopy(threads[countOfThreads - 1].getArrayToSort(), 0, lastSubArray, 0, sizeOfArray - (countOfThreads - 1) * lengthOfSubArrays);

            int tempLengthOfSubArrays = lengthOfSubArrays;
            int countOfSubArrays = countOfThreads;

            while (countOfSubArrays > 1) {
                int currentCountOfSubArrays = countOfSubArrays / 2;
                Merger[] mergers = new Merger[countOfSubArrays / 2];

                for (int i = 0; i < currentCountOfSubArrays - 1; i++) {
                    mergers[i] = new Merger(subArrays[2 * i], subArrays[2 * i + 1]);
                    mergers[i].start();
                }

                mergers[currentCountOfSubArrays - 1] = new Merger(subArrays[(currentCountOfSubArrays - 1) * 2], lastSubArray);
                mergers[currentCountOfSubArrays - 1].start();

                for (int i = 0; i < currentCountOfSubArrays; i++) mergers[i].join();

                int currentSubSize = 2 * tempLengthOfSubArrays;
                subArrays = Arrays.copyOf(subArrays, currentCountOfSubArrays);

                for (int i = 0; i < currentCountOfSubArrays - 1; i++) subArrays[i] = Arrays.copyOf(subArrays[i], currentSubSize);
                lastSubArray = Arrays.copyOf(lastSubArray, sizeOfArray - currentSubSize * (currentCountOfSubArrays - 1));

                for (int i = 0; i < currentCountOfSubArrays - 1; i++) System.arraycopy(mergers[i].getRes(), 0, subArrays[i], 0, currentSubSize);
                System.arraycopy(mergers[currentCountOfSubArrays - 1].getRes(), 0, lastSubArray, 0, sizeOfArray - currentSubSize * (currentCountOfSubArrays - 1));
                tempLengthOfSubArrays = currentSubSize;
                countOfSubArrays = currentCountOfSubArrays;
            }
            long sortingTime = System.currentTimeMillis() - startOfSortingTime; // Конец подсчёта времени
            System.out.println("Массив из " + sizeOfArray + " элементов сортировался " + countOfThreads + " потоками " + (double) sortingTime / 1000 + " секунд");
        }
    }
}