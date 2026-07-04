package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import algorithm.KmeansSerial;
import model.Cluster;
import model.Point;
import readers.ReadDatasetSerial;

public class MainSerial {
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
        
        long t0 = System.nanoTime();
        List<Point> points = ReadDatasetSerial.loadDataset(datasetPath);
        long t1 = System.nanoTime();

        System.out.println("Dataset loaded with " + points.size() + " points.");
        List<Cluster> solution = new ArrayList<>();
        KmeansSerial kmeans = new KmeansSerial(k, iterations);
        System.out.println("Starting kmeans");

        long t2 = System.nanoTime();
        solution = kmeans.fit(points,seed);
        long t3 = System.nanoTime();

        long duration = (t3 - t0) / 1_000_000;
        long loadDuration = (t1 - t0) / 1_000_000;
        long kmeansDuration = (t3 - t2) / 1_000_000;
        System.out.println("Dataset loading time: " + loadDuration + " ms.");
        System.out.println("K-means clustering time: " + kmeansDuration + " ms.");
        System.out.println("Total execution time: " + duration + " ms.");
        // System.out.println("Centroids results: ");
        // for(Cluster c : solution){
        //     System.out.println(c.toString());
        //     System.out.println("Cluster size: " + c.getPoints().size());
        // }
    }
}
