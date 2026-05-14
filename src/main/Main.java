package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import algorithm.Kmeans;
import model.Cluster;
import model.Point;
import utils.ReadDataset;

public class Main {
    public static void main(String[] args) throws Exception {
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Select number K: ");
        int k = scan.nextInt();
        System.out.println("Select number of iterations: ");
        int iterations = scan.nextInt();
        System.out.println("Select dataset path: ");
        String datasetPath = scan.next();
        scan.close();
        

        List<Point> points = ReadDataset.loadDataset(datasetPath);
        List<Cluster> solution = new ArrayList<>();

        Kmeans kmeans = new Kmeans(k, iterations);
        long startTime = System.nanoTime();
        solution = kmeans.fit(points);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("K-means clustering completed in " + duration + " ms.");
        System.out.println("Centroids results: ");
        for(Cluster c : kmeans.getClusters()){
            System.out.println(c.toString());
        }
    }
}
