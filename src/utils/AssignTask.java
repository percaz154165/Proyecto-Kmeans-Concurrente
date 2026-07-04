package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

import model.Cluster;
import model.Point;
import utils.EuclideanDistance;

public class AssignTask extends RecursiveTask<List<Cluster>> {

    private static final int THRESHOLD = 10000;

    private final List<Point> points;
    private final List<Cluster> clusters;
    private final int k;
    private final int start;
    private final int end;

    public AssignTask(List<Point> points,
                      List<Cluster> clusters,
                      int k,
                      int start,
                      int end) {

        this.points = points;
        this.clusters = clusters;
        this.k = k;
        this.start = start;
        this.end = end;
    }

    @Override
    protected List<Cluster> compute() {

        if (end - start <= THRESHOLD) {
            return assignBlock();
        }

        int mid = (start + end) / 2;

        AssignTask left = new AssignTask(points, clusters, k, start, mid);
        AssignTask right = new AssignTask(points, clusters, k, mid, end);

        left.fork();

        List<Cluster> rightClusters = right.compute();
        List<Cluster> leftClusters = left.join();

        // Fusionar los resultados
        for (int i = 0; i < k; i++) {
            leftClusters.get(i).getPoints().addAll(rightClusters.get(i).getPoints());
        }

        return leftClusters;
    }

    private List<Cluster> assignBlock() {

        List<Cluster> localClusters = new ArrayList<>(k);

        // Copiamos únicamente los centroides
        for (Cluster c : clusters) {
            localClusters.add(new Cluster(c.getCentroid()));
        }

        // Asignación de puntos
        for (int i = start; i < end; i++) {

            Point p = points.get(i);

            Cluster closestCluster = null;
            double minDistance = Double.MAX_VALUE;

            for (Cluster c : localClusters) {

                double distance = EuclideanDistance.calculate(p, c.getCentroid());

                if (distance < minDistance) {
                    minDistance = distance;
                    closestCluster = c;
                }
            }

            closestCluster.addPoint(p);
        }

        return localClusters;
    }
}