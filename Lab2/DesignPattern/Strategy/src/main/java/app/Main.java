package app;

import context.Sorter;
import strategy.BubbleSort;
import strategy.QuickSort;

public class Main {
    public static void main(String[] args) {
        int[] array1 = {5, 2, 8, 1, 9};
        int[] array2 = {5, 2, 8, 1, 9};

        Sorter sorter = new Sorter(new BubbleSort());
        sorter.doSort(array1);

        sorter.setStrategy(new QuickSort());
        sorter.doSort(array2);
    }
}
