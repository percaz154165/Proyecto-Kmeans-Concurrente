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
    }

    private void initCentroids(List<Point> points){
        Random rand = new Random();

        for (int i = 0; i < k; i++){
            int index = rand.nextInt(points.size());
            Cluster newCluster = new Cluster(points.get(index));
            this.clusters.add(newCluster);
        }
    }
}
