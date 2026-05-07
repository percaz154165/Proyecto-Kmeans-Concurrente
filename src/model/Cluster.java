package model;
import java.util.ArrayList;
import java.util.List;


public class Cluster {
    private Point centroid;
    private List<Point> points;

    public Cluster(Point centroid) {
        this.centroid = centroid;
        this.points = new ArrayList<>();
    }

    public Point getCentroid() {
        return centroid;
    }

    public void setCentroid(Point centroid) {
        this.centroid = centroid;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void clearPoints() {
        this.points.clear();
    }

    public void addPoint(Point point) {
        this.points.add(point);
    }

    public void removePoint(Point point) {
        this.points.remove(point);
    }

    public void removePointByIndex(int index) {
        if (index >= 0 && index < points.size()) {
            this.points.remove(index);
        }
    }
}

    
