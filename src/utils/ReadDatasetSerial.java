package utils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import model.Point;

public class ReadDatasetSerial {

    public static List<Point> loadDataset(String filePath) {
        System.out.println("Loading dataset from: " + filePath);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new java.io.FileReader(filePath));
            String line;
            List<Point> dataset = new ArrayList<>(9000000);
            reader.readLine(); // Skip header line
            int i = 0;
            double mins[] = new double[27];
            double maxs[] = new double[27];
            // Initialize mins and maxs arrays
            for(int j = 0; j < 27; j++) {
                mins[j] = Double.MAX_VALUE;
                maxs[j] = Double.MIN_VALUE;
            }
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length != 27) {
                    System.out.println("Skipping invalid line: " + line);
                    continue;
                }
                double[] coordinates = new double[parts.length];
                for(int j = 0; j < parts.length; j++) {
                    coordinates[j] = Double.parseDouble(parts[j]);
                    // Update mins and maxs
                    if(coordinates[j] < mins[j]) {
                        mins[j] = coordinates[j];
                    }
                    if(coordinates[j] > maxs[j]) {
                        maxs[j] = coordinates[j];
                    }
                }
                Point point = new Point(coordinates);
                dataset.add(point);
                if(i % 1000000 == 0) {
                    System.out.println("Readed " + i + " lines...");
                }
                i++;
            }

                System.out.println("Finished loading dataset. Total points: " + dataset.size());
                System.out.println("Starting normalization of dataset...");
                int pointIndex = 0;
                for(Point point : dataset) {
                    point.normalize(mins, maxs);
                    if(pointIndex % 1000000 == 0) {
                        System.out.println("Normalized " + pointIndex + " points...");
                    }
                    pointIndex++;
                }
                System.out.println("Finished normalization of dataset.");

                return dataset;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("ERROR LOADING DATASET!!!");
        return null;
    }
}
