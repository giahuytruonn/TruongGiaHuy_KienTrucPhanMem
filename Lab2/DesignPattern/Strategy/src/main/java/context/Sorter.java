package context;

import strategy.SortStrategy;

public class Sorter {
    private SortStrategy strategy;

    public Sorter(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void doSort(int[] array) {
        strategy.sort(array);
    }
}
