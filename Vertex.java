package A6_Dijkstra;

import java.util.concurrent.ConcurrentHashMap;

public class Vertex {
    private final long idNum;
    private final String label;
    private ConcurrentHashMap<String, Edge> inEdges = new ConcurrentHashMap<String, Edge>();
    private ConcurrentHashMap<String, Edge> outEdges = new ConcurrentHashMap<String, Edge>();
    private double distance;
    private Vertex previous;
    private boolean known;

    public Vertex(long idNum, String label) {
        this.idNum = idNum;
        this.label = label;
        known = false;
    }

    public long getIdNum() {
        return idNum;
    }

    public String getLabel() {
        return label;
    }

    public double getDistance() {
        return distance;
    }

    public boolean getKnown() {
        return known;
    }

    public void setKnown(Boolean b) {
        known = b;

    }

    public Vertex getPrevious() {
        return previous;
    }

    public void setPrevious(Vertex prev) {
        this.previous = prev;
    }

    public void setDistance(double newDistance) {
        distance = newDistance;
    }

    public ConcurrentHashMap<String, Edge> getInEdgesMap() {
        return inEdges;
    }

    public ConcurrentHashMap<String, Edge> getOutEdgesMap() {
        return outEdges;
    }

    public boolean containsEdgeBetween(Vertex endVertex) {
        if (this.getOutEdgesMap().get(endVertex.getLabel()) == null) {
            return false;
        } else {
            return true;
        }
    }
}
