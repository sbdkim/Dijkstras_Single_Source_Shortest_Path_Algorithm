package A6_Dijkstra;

public class Edge {
    private final long idNum;
    private long weight;
    private Vertex sourceVertex;
    private Vertex endVertex;
    private String label;

    public Edge(long idNum, Vertex sourceVertex, Vertex endVertex, long weight, String label) {
        this.idNum = idNum;
        this.weight = weight;
        this.endVertex = endVertex;
        this.sourceVertex = sourceVertex;
        this.label = label;
    }


    public long getIdNum() {
        return idNum;
    }

    public String getLabel() {
        return label;
    }

    public long getWeight() {
        return weight;
    }

    public Vertex getSourceVertex() {
        return sourceVertex;
    }

    public Vertex getEndVertex() {
        return endVertex;
    }
}
