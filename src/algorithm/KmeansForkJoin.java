package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.RecursiveAction;

import model.Cluster;
import model.Point;
import utils.AssignTask;
import utils.EuclideanDistance;
import utils.UpdateTask;

public class KmeansForkJoin {
    private int k;
    private int iterations;
    private List<Cluster> clusters;
    private ForkJoinPool pool;

    public KmeansForkJoin(int k, int iter, int procs){
        this.k = k;
        this.iterations=iter;
        this.clusters = new java.util.ArrayList<>();
        this.pool = new ForkJoinPool(procs);
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
        AssignTask task = new AssignTask(points, this.clusters, this.k, 0, points.size());
        List<Cluster> newClusters = pool.invoke(task);
    }

    private void updateCentroids() {
        pool.invoke(new UpdateTask(clusters, 0, k));
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
