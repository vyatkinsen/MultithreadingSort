import java.util.Arrays;
import java.util.Random;

public class SortingAlgorithmTest {
    public static void main(String[] args) throws InterruptedException {
        int countOfElements = 10;
        int[] arrayToSort = new int[countOfElements];
        Random random = new Random();
        for (int i = 0; i < countOfElements; i++) arrayToSort[i] = random.nextInt(10);
        System.out.println("Массив до сортировки: " + Arrays.toString(arrayToSort));
        Main.singleThreadSorting(arrayToSort);
        System.out.println("Массив после сортировки: " + Arrays.toString(arrayToSort));
        System.out.println("\n");
    }
}
