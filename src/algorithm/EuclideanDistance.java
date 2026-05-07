package algorithm;

import model.Point;

public class EuclideanDistance {
    public static double calculate(Point p1, Point p2){
        double sum = 0;
        for (int i = 0; i < p1.getDimension(); i++){
            double diff = p1.getCoordinates()[i] - p2.getCoordinates()[i];
            sum = sum + (diff * diff);
        }
        return Math.sqrt(sum);
    }
}
