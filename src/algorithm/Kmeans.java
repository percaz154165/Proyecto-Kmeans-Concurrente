package algorithm;

import java.util.List;
import java.util.Random;

import model.Cluster;
import model.Point;
import utils.EuclideanDistance;

public class Kmeans {
    private int k;
    private int iterations;
    private List<Cluster> clusters;

    public Kmeans(int k, int iter){
        this.k = k;
        this.iterations=iter;
        this.clusters = new java.util.ArrayList<>();
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
        for (Cluster c : this.clusters){
            c.clearPoints();
        }
    }

    private void assignPointsToClusters(List<Point> points){
        for (Point p : points){
            Cluster closestCluster = null;
            double minDistance = Double.MAX_VALUE;

            for (Cluster c : this.clusters){
                double distance = EuclideanDistance.calculate(p, c.getCentroid());
                if (distance < minDistance){
                    minDistance = distance;
                    closestCluster = c;
                }
            }

            closestCluster.addPoint(p);
        }
    }

    private void updateCentroids(){ 
        for (Cluster c : this.clusters){
            List <Point> clusterPoints = c.getPoints();
            if (!clusterPoints.isEmpty()){
                int dim = clusterPoints.get(0).getDimension();
                double[] sum = new double[dim];
                for (Point p : clusterPoints){
                    double[] coords = p.getCoordinates();
                    for (int i = 0; i < dim; i++){
                        sum[i] += coords[i];
                    }
                }
                for (int i = 0; i < dim; i++){
                    sum[i] /= clusterPoints.size();
                }
                if(EuclideanDistance.calculate(new Point(sum), c.getCentroid()) < 1e-4){
                    c.converged = true;
                } else {
                    c.setCentroid(new Point(sum));
                }
            }
        }
    }

    public List<Cluster> fit(List<Point> points, int seed){
        initCentroids(points, seed);
        int i = 0;
        while (i < this.iterations && !hasConverged()){
            assignPointsToClusters(points);
            updateCentroids();
            cleanClusters();
            i++;
            if(i % 40 == 0) {
                System.out.println("Iteration " + i + " completed...");
            }

        }
        assignPointsToClusters(points);
        System.out.println("Converged in " + i + " iterations from " + this.iterations + " given.");
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
