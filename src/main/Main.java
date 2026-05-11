package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import algorithm.Kmeans;
import model.Point;

public class Main {
    public static void main(String[] args) throws Exception {
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Select number K: ");
        int k = scan.nextInt();
        System.out.println("Select number of iterations: ");
        int iterations = scan.nextInt();
        scan.close();
        
        List<Point> points = new ArrayList<>();

        Kmeans kmeans = new Kmeans(k, iterations);
        long startTime = System.nanoTime();
        kmeans.fit(points);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        System.out.println("K-means clustering completed in " + duration + " ms.");
    }
}
