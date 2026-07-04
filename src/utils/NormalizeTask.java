package utils;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import model.Point;

public class NormalizeTask extends RecursiveAction {

    private static final int THRESHOLD = 10000;

    private final List<Point> dataset;
    private final double[] mins;
    private final double[] maxs;
    private final int start;
    private final int end;

    public NormalizeTask(List<Point> dataset,
                         double[] mins,
                         double[] maxs,
                         int start,
                         int end) {

        this.dataset = dataset;
        this.mins = mins;
        this.maxs = maxs;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {

        if (end - start <= THRESHOLD) {

            for (int i = start; i < end; i++) {
                dataset.get(i).normalize(mins, maxs);
            }

            return;
        }

        int mid = (start + end) / 2;

        invokeAll(
                new NormalizeTask(dataset, mins, maxs, start, mid),
                new NormalizeTask(dataset, mins, maxs, mid, end)
        );
    }
}