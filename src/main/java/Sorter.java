public class Sorter extends Thread {
    private final int[] arrayToSort;

    public Sorter(int[] arrayToSort) {
        this.arrayToSort = arrayToSort;
    }
    
    private static void sort(int[] in, int left, int right) {
        if (left >= right - 1) return;
        int i = partition(in, left, right);
        sort(in, left, i);
        sort(in, i + 1, right);
    }

    private static int partition(int[] inputArray, int left, int right) {
        int idx = left;
        int pivot = inputArray[left];
        for (int j = left + 1; j < right; j++) if (inputArray[j] <= pivot) swap(inputArray, ++idx, j);
        swap(inputArray, idx, left);
        return idx;
    }

    private static void swap(int[] inputArray, int left, int right) {
        int temp = inputArray[left];
        inputArray[left] = inputArray[right];
        inputArray[right] = temp;
    }

    public int[] getArrayToSort() {
        return arrayToSort;
    }

    @Override
    public void run() {
        sort(arrayToSort, 0, arrayToSort.length);
    }
}
