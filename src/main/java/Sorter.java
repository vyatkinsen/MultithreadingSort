public class Sorter extends Thread {
    private final int[] sortableArr;

    public Sorter(int[] sortableArr) {
        this.sortableArr = sortableArr;
    }

    @Override
    public void run() {
        sort(sortableArr, 0, sortableArr.length);
    }

    private static void sort(int[] in, int left, int right) {
        if (left >= right - 1) return; // Если в массиве только один элемент, значит что он уже отсортирован
        int i = partition(in, left, right);
        sort(in, left, i); // Проводим сортировку для элементов слева от последнего опорного элемента
        sort(in, i + 1, right); // Аналогично справа
    }

    /*
    Сортируем массив так, чтобы получить две части:
    слева от опорного элемента: элементы меньшие или равные опорному
    справа от опорного элемента: элементы большие опорного.
    Из метода возвращаем индекс местонахождения опорного элемента после перестановок
    */
    private static int partition(int[] in, int left, int right) {
        int pivotal = in[left]; // В качестве опорного элемента выбираем первый элемент массива
        int i = left;
        for (int j = left + 1; j < right; j++) {
            if (in[j] <= pivotal) swap(in, ++i, j);
        }
        swap(in, i, left);
        return i;
    }

    private static void swap(int[] in, int left, int right) {
        int tmp = in[left];
        in[left] = in[right];
        in[right] = tmp;
    }

    public int[] getSortableArr() {
        return sortableArr;
    }
}
