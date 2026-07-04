package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import model.Point;

public class ReadDatasetForkJoin {

    public static List<Point> loadDataset(String filePath) {

        System.out.println("Loading dataset from: " + filePath);

        BufferedReader reader = null;

        try {

            reader = new BufferedReader(new FileReader(filePath));

            ForkJoinPool pool = new ForkJoinPool();

            List<Point> dataset = new ArrayList<>(9000000);

            reader.readLine(); // Saltar cabecera

            double[] mins = new double[27];
            double[] maxs = new double[27];

            for (int i = 0; i < 27; i++) {
                mins[i] = Double.MAX_VALUE;
                maxs[i] = Double.MIN_VALUE;
            }

            String line;
            int lines = 0;

            while ((line = reader.readLine()) != null) {

                List<String> block = new ArrayList<>(1000);

                block.add(line);

                while (block.size() < 1000 &&
                        (line = reader.readLine()) != null) {

                    block.add(line);
                }

                dataset.addAll(
                        pool.invoke(
                                new ReadTask(
                                        block,
                                        mins,
                                        maxs,
                                        0,
                                        block.size()
                                )
                        )
                );

                lines += block.size();

                if (lines % 1000000 == 0) {
                    System.out.println("Read " + lines + " lines...");
                }

            }

            System.out.println("Finished loading dataset.");
            System.out.println("Starting normalization...");

            pool.invoke(
                    new NormalizeTask(
                            dataset,
                            mins,
                            maxs,
                            0,
                            dataset.size()
                    )
            );

            System.out.println("Finished normalization.");

            pool.shutdown();

            return dataset;

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            try {

                if (reader != null)
                    reader.close();

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        return null;
    }

}