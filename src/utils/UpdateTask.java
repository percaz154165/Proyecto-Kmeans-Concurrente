package utils;

import java.util.List;
import java.util.concurrent.RecursiveAction;

import model.Cluster;
import model.Point;
import utils.EuclideanDistance;

public class UpdateTask extends RecursiveAction {

    private static final int THRESHOLD = 2;

    private final List<Cluster> clusters;
    private final int start;
    private final int end;

    public UpdateTask(List<Cluster> clusters, int start, int end) {
        this.clusters = clusters;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {

        if (end - start <= THRESHOLD) {
            updateBlock();
            return;
        }

        int mid = (start + end) / 2;

        UpdateTask left = new UpdateTask(clusters, start, mid);
        UpdateTask right = new UpdateTask(clusters, mid, end);

        invokeAll(left, right);
    }

    private void updateBlock() {

        for (int j = start; j < end; j++) {

            Cluster c = clusters.get(j);
            List<Point> clusterPoints = c.getPoints();

            if (!clusterPoints.isEmpty()) {

                int dim = clusterPoints.get(0).getDimension();
                double[] sum = new double[dim];

                for (Point p : clusterPoints) {
                    double[] coords = p.getCoordinates();

                    for (int i = 0; i < dim; i++) {
                        sum[i] += coords[i];
                    }
                }

                for (int i = 0; i < dim; i++) {
                    sum[i] /= clusterPoints.size();
                }

                Point newCentroid = new Point(sum);

                if (EuclideanDistance.calculate(newCentroid, c.getCentroid()) < 1e-4) {
                    c.converged = true;
                } else {
                    c.setCentroid(newCentroid);
                }
            }
        }
    }
}