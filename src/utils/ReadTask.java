package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import model.Point;

public class ReadTask extends RecursiveTask<List<Point>> {

    private static final int THRESHOLD = 200;

    private final List<String> lines;
    private final double[] mins;
    private final double[] maxs;
    private final int start;
    private final int end;

    public ReadTask(List<String> lines,
                    double[] mins,
                    double[] maxs,
                    int start,
                    int end) {

        this.lines = lines;
        this.mins = mins;
        this.maxs = maxs;
        this.start = start;
        this.end = end;
    }

    @Override
    protected List<Point> compute() {

        if (end - start <= THRESHOLD) {
            return processBlock();
        }

        int mid = (start + end) / 2;

        ReadTask left = new ReadTask(lines, mins, maxs, start, mid);
        ReadTask right = new ReadTask(lines, mins, maxs, mid, end);

        left.fork();

        List<Point> rightPoints = right.compute();
        List<Point> leftPoints = left.join();

        leftPoints.addAll(rightPoints);

        return leftPoints;
    }

    private List<Point> processBlock() {

        List<Point> points = new ArrayList<>();

        double[] localMin = new double[27];
        double[] localMax = new double[27];

        for (int i = 0; i < 27; i++) {
            localMin[i] = Double.MAX_VALUE;
            localMax[i] = Double.MIN_VALUE;
        }

        for (int i = start; i < end; i++) {

            String[] parts = lines.get(i).split(",");

            if (parts.length != 27)
                continue;

            double[] coords = new double[27];

            for (int j = 0; j < 27; j++) {

                coords[j] = Double.parseDouble(parts[j]);

                localMin[j] = Math.min(localMin[j], coords[j]);
                localMax[j] = Math.max(localMax[j], coords[j]);
            }

            points.add(new Point(coords));
        }

        synchronized (mins) {

            for (int i = 0; i < 27; i++) {
                mins[i] = Math.min(mins[i], localMin[i]);
                maxs[i] = Math.max(maxs[i], localMax[i]);
            }
        }

        return points;
    }
}