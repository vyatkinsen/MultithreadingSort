public class Merger extends Thread {

    private final int[] first;
    private final int[] second;
    private final int[] res;

    public Merger(int[] first, int[] second) {
        this.first = first;
        this.second = second;
        this.res = new int[first.length + second.length];
    }

    @Override
    public void run() {
        merge();
    }

    private void merge() {
        int leftArrayPntr = 0;
        int rightArrayPntr = 0;
        int resPntr = 0;
        while (leftArrayPntr < first.length && rightArrayPntr < second.length) {
            // Сравниваем элементы из двух подмассивов и помещаем в результирующий массив меньший из них
            res[resPntr++] = first[leftArrayPntr] <= second[rightArrayPntr] ? first[leftArrayPntr++] : second[rightArrayPntr++];
        }
        if (leftArrayPntr == first.length) while (rightArrayPntr < second.length) res[resPntr++] = second[rightArrayPntr++];
        else if (rightArrayPntr == second.length) while (leftArrayPntr < first.length) res[resPntr++] = first[leftArrayPntr++];
    }

    public int[] getRes() {
        return res;
    }
}
