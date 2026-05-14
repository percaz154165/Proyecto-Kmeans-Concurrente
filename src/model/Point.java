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

    public void normalize(double[] mins, double[] maxs){
        for(int i = 0; i < this.coordinates.length; i++){
            if(maxs[i] - mins[i] == 0) {
                this.coordinates[i] = 0;
            } else {
                this.coordinates[i] = (this.coordinates[i] - mins[i]) / (maxs[i] - mins[i]);
            }
        }
    }
}
