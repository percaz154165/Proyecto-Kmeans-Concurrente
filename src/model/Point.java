package model;
public class Point {
    private double[] coordinates;

    public Point(double[] coordinates) {
        this.coordinates = coordinates;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public int getDimension() {
       return this.coordinates.length;
    }
}
