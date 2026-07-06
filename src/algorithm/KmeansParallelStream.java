package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import model.Cluster;
import model.Point;
import utils.EuclideanDistance;

public class KmeansParallelStream {
    private int k;
    private int iterations;
    private List<Cluster> clusters;
    private ExecutorService pool;

    public KmeansParallelStream(int k, int iter, int procs){
        this.k = k;
        this.iterations=iter;
        this.clusters = new java.util.ArrayList<>();
        this.pool = Executors.newFixedThreadPool(procs);
    }

    private void initCentroids(List<Point> points,int seed){
        Random rand = new Random(seed);

        for (int i = 0; i < k; i++){
            int index = rand.nextInt(points.size());
            Cluster newCluster = new Cluster(points.get(index));
            this.clusters.add(newCluster);
        }
    }

    private void cleanClusters(){
        clusters.parallelStream().forEach(Cluster::clearPoints);
    }

    private List<Cluster> assignBlocks(List<Point> points,int start, int end){
        List<Cluster> localClusters = new java.util.ArrayList<>(this.k);

        for(Cluster c : this.clusters){
            localClusters.add(
                new Cluster(
                    c.getCentroid()
                )
            );
        
        }

       for (int i = start; i < end; i++){
            Point p = points.get(i);
            Cluster closestCluster = null;
            double minDistance = Double.MAX_VALUE;

            for (Cluster c : localClusters){
                double distance = EuclideanDistance.calculate(p, c.getCentroid());
                if (distance < minDistance){
                    minDistance = distance;
                    closestCluster = c;
                }
            }
            
            closestCluster.addPoint(p);
            
        }

        return localClusters;
    }

    private void assignPointsToClusters(List<Point> points){
        List<Future<List<Cluster>>> futures = new ArrayList<>();
        int blockSize = Math.max(1, Math.floorDiv(points.size(), Runtime.getRuntime().availableProcessors()));

        for (int i = 0; i < points.size(); i += blockSize) {
            int start = i;
            int end = Math.min(i + blockSize, points.size());
            Future<List<Cluster>> future = pool.submit(() -> assignBlocks(points, start, end));
            futures.add(future);
        }

        for (Future<List<Cluster>> future : futures) {
            try {
                List<Cluster> localClusters = future.get();
                for (int i = 0; i < this.k; i++) {
                    this.clusters.get(i).getPoints().addAll(localClusters.get(i).getPoints());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

private void updateCentroids() {

    clusters.parallelStream().forEach(c -> {

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

    });

}

    public List<Cluster> fit(List<Point> points, int seed){
        initCentroids(points, seed);
        int i = 0;
        while (i < this.iterations && !hasConverged()){
            assignPointsToClusters(points);
            updateCentroids();
            cleanClusters();
            i++;
            if(i % 10 == 0) {
                System.out.println("Iteration " + i + " completed...");
            }

        }
        assignPointsToClusters(points);
        System.out.println("Converged in " + i + " iterations from " + this.iterations + " given.");
        this.pool.shutdown();
        return this.clusters;
    }

    public List<Cluster> getClusters(){
        return this.clusters;
    }

    private boolean hasConverged(){
        for (Cluster c : this.clusters){
            if (!c.converged){
                return false;
            }
        }
        return true;
    }
}
