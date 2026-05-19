package utils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import model.Point;

public class ReadDatasetVirtual {

    public static List<Point> loadDataset(String filePath) {
        System.out.println("Loading dataset from: " + filePath);

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new java.io.FileReader(filePath));
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
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
            int blockID = 0;
            List<String> block = new ArrayList<>(1000);
            List<Future<BlockResult>> futures = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                block.add(line);
                if(block.size() == 1000 || line == null) {
                    futures.add(processBlock(blockID, block, mins, maxs, executor));
                    block.clear();
                    blockID ++;
                }
                if(i % 1000000 == 0) {
                    System.out.println("Readed " + i + " lines...");
                }
                i++;
                
            } 
            if(!block.isEmpty()) {
                futures.add(processBlock(blockID, block, mins, maxs, executor));
                block.clear();
                blockID ++;
            }

            List<BlockResult> results = new ArrayList<>();

            for(Future<BlockResult> f : futures){
                results.add(f.get());
            }

            results.sort((a, b) -> Integer.compare(a.blockID, b.blockID));

            for(BlockResult r : results) {
                dataset.addAll(r.points());
            }
            

            System.out.println("Finished loading dataset. Total points: " + dataset.size());
            System.out.println("Starting normalization of dataset...");

            int numTasks = Runtime.getRuntime().availableProcessors();
            int blockSize = (dataset.size()+numTasks-1)/numTasks;

            List<Future<?>> futuresNorm = new ArrayList<>();

            for(int k=0;k<dataset.size();k+=blockSize){

                int start=k;
                int end=Math.min(k+blockSize,dataset.size());

                Future<?> f=executor.submit(() -> {

                    for(int j=start;j<end;j++){
                        dataset.get(j).normalize(mins,maxs);
                    }

                });

                futuresNorm.add(f);
            }

            for(Future<?> f:futuresNorm){
                f.get();
            }
            System.out.println("Finished normalization of dataset.");

                
                executor.shutdown();
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

    private static Future<BlockResult> processBlock(int blockID, List<String> block, double[] mins, double[] maxs, ExecutorService executor) {
        List<String> blockCopy = new ArrayList<>(block);
        int currentID = blockID;
        
        return executor.submit(() -> {
            List<Point> localPoints = new ArrayList<>();
            double newMins[] = new double[27];
            double newMaxs[] = new double[27];
            
            // Initialize mins and maxs arrays
            for(int j = 0; j < 27; j++) {
                newMins[j] = Double.MAX_VALUE;
                newMaxs[j] = Double.MIN_VALUE;
            }

            for(String l : blockCopy) {
                String[] parts = l.split(",");
                if(parts.length != 27) {
                    System.out.println("Skipping invalid line: " + l);
                    continue;
                }
                double[] coordinates = new double[parts.length];
                for(int j = 0; j < parts.length; j++) {
                    coordinates[j] = Double.parseDouble(parts[j]);
                    // Update mins and maxs
                    if(coordinates[j] < newMins[j]) {
                        newMins[j] = coordinates[j];
                    }
                    if(coordinates[j] > newMaxs[j]) {
                        newMaxs[j] = coordinates[j];
                    }
                    
                }
                Point point = new Point(coordinates);
                localPoints.add(point);
            }
            synchronized (mins) {
                for(int j = 0; j < 27; j++) {
                    if(newMins[j] < mins[j]) {
                        mins[j] = newMins[j];
                    }
                    if(newMaxs[j] > maxs[j]) {
                        maxs[j] = newMaxs[j];
                    }
                }
            }
            return new BlockResult(currentID, localPoints);
        });
    }

    private record BlockResult(int blockID, List<Point> points){}


}
