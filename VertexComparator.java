package A6_Dijkstra;

import java.util.Comparator;

public class VertexComparator implements Comparator<Vertex> {

    @Override
    public int compare(Vertex v1, Vertex v2) {
        if (v1.getDistance() > v2.getDistance()) {
            return 1;
        } else {
            return -1;
        }
    }
}
