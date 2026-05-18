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

        int seed = 42; //Change for reproducibility
        System.out.println(
            System.getProperty("user.dir")
        );
        Scanner scan = new Scanner(System.in);
        System.out.println("Select number K: ");
        int k = scan.nextInt();
        System.out.println("Select number of iterations: ");
        int iterations = scan.nextInt();
        System.out.println("Select dataset path: ");
        String datasetPath = /*scan.next()*/"src/DATA/datos_ampliado.csv";
        scan.close();
        

        List<Point> points = ReadDataset.loadDataset(datasetPath);
        System.out.println("Dataset loaded with " + points.size() + " points.");
        List<Cluster> solution = new ArrayList<>();

        Kmeans kmeans = new Kmeans(k, iterations);
        long startTime = System.nanoTime();
        System.out.println("Starting kmeans");
        solution = kmeans.fit(points,seed);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("K-means clustering completed in " + duration + " ms.");
        System.out.println("Centroids results: ");
        for(Cluster c : solution){
            System.out.println(c.toString());
            System.out.println("Cluster size: " + c.getPoints().size());
        }
    }
}
