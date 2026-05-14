package algorithm;

import java.util.List;
import java.util.Random;

import model.Cluster;
import model.Point;

public class Kmeans {
    private int k;
    private int iterations;
    private List<Cluster> clusters;

    public Kmeans(int k, int iter){
        this.k = k;
        this.iterations=iter;
        this.clusters = new java.util.ArrayList<>();
    }

    private void initCentroids(List<Point> points){
        Random rand = new Random();

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
                c.setCentroid(new Point(sum));
            }
        }
    }

    public List<Cluster> fit(List<Point> points){
        initCentroids(points);

        for (int i = 0; i < iterations; i++){
            assignPointsToClusters(points);
            updateCentroids();
            cleanClusters();
        }
        assignPointsToClusters(points);
        return this.clusters;
    }

    public List<Cluster> getClusters(){
        return this.clusters;
    }
}
