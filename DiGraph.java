package A6_Dijkstra;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DiGraph implements DiGraph_Interface {
    private long nodeCount;
    private long edgeCount;
    private HashMap<String, Vertex> nodeLabelMap = new HashMap<String, Vertex>();
    private HashSet<Long> edgeIDs = new HashSet<Long>();
    private HashSet<Long> nodeIDs = new HashSet<Long>();


    public DiGraph() { // default constructor
        //explicitly include this, and this is the one the grader uses
    }

    @Override
    public boolean addNode(long idNum, String label) {
        // check if id is >= 0, and if idNum and label are unique before adding
        if (idNum >= 0) {
            if (!nodeLabelMap.containsKey(label) && !nodeIDs.contains(idNum)) {
                nodeLabelMap.put(label, new Vertex(idNum, label));
                nodeIDs.add(idNum);
                nodeCount++;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean addEdge(long idNum, String sLabel, String dLabel, long weight, String eLabel) {
        // have to also check for if the edge from s to d already exists, eLabels don't have to be unique
        if (idNum >= 0 && nodeLabelMap.containsKey(sLabel) &&
                nodeLabelMap.containsKey(dLabel) && !edgeIDs.contains(idNum)) {

            Vertex sourceVertex = nodeLabelMap.get(sLabel);
            Vertex endVertex = nodeLabelMap.get(dLabel);
            Edge newEdge = new Edge(idNum, sourceVertex, endVertex, weight, eLabel);

            if (!sourceVertex.containsEdgeBetween(endVertex)) {
                // store in a hashmap where the string of the endVertex maps to the outEdgeMap
                sourceVertex.getOutEdgesMap().put(dLabel, newEdge);
                // store in a hashmap where the string of the sourceVertex maps to the outEdgeMap
                endVertex.getInEdgesMap().put(sLabel, newEdge);
                edgeIDs.add(idNum);
                edgeCount++;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean delNode(String label) {
        if (nodeLabelMap.containsKey(label)) {
            Vertex removedVertex = nodeLabelMap.get(label);
            // have to update edgeCount since node is being removed
            edgeCount = edgeCount - removedVertex.getInEdgesMap().size() - removedVertex.getOutEdgesMap().size();

            removedVertex.getInEdgesMap().clear();
            removedVertex.getOutEdgesMap().clear();

            nodeLabelMap.remove(label);
            nodeCount--;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delEdge(String sLabel, String dLabel) {
        if (nodeLabelMap.containsKey(sLabel) && nodeLabelMap.containsKey(dLabel)) {
            Vertex sourceVertex = nodeLabelMap.get(sLabel);
            Vertex endVertex = nodeLabelMap.get(dLabel);

            // check to see if an edge exists between the two nodes
            if (sourceVertex.containsEdgeBetween(endVertex)) {
                // remove the id from the edgeIDs set
                long removedEdgeID = sourceVertex.getOutEdgesMap().get(endVertex.getLabel()).getIdNum();
                edgeIDs.remove(removedEdgeID);

                // find the edge in the sourceVertex and endVertex and remove it
                sourceVertex.getOutEdgesMap().remove(endVertex.getLabel());
                endVertex.getInEdgesMap().remove(sourceVertex.getLabel());

                // decrement edgeCount because an edge was deleted
                edgeCount--;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public long numNodes() {
        return nodeCount;
    }

    @Override
    public long numEdges() {
        return edgeCount;
    }

    @Override
    public String[] topoSort() {
        // first have to scan the graph to check for the node with 0 inEdges
        ArrayList<String> topoList = new ArrayList<String>();
        Queue<Vertex> noEdgeQueue = new LinkedList<Vertex>();

        // iterate through the graph and check each node for 0 inEdges
        for (Map.Entry<String, Vertex> entry : nodeLabelMap.entrySet()) {
            // if a node has 0 inEdges, add to noEdgeSet
            if (entry.getValue().getInEdgesMap().size() == 0) {
                noEdgeQueue.add(entry.getValue());
            }
        }

        // now implement Kahn's Algorithm
        while (!noEdgeQueue.isEmpty()) {
            // use poll method to return top of the queue, then add to sorted list
            Vertex sortedVertex = noEdgeQueue.poll();
            topoList.add(sortedVertex.getLabel());


            // iterate through all neighbor nodes and remove the inEdge that connected it
            // as well as check if the inDegree is then 0
            // have to use concurrentHashMap to change collection while iterating over it to still use for each loops
            for (ConcurrentHashMap.Entry<String, Edge> entry : sortedVertex.getOutEdgesMap().entrySet()) {
                Vertex endVertex = nodeLabelMap.get(entry.getKey());
                this.delEdge(sortedVertex.getLabel(), endVertex.getLabel());
                // if inEdgesMap of neighbor nodes is now 0, add to noEdgeQueue
                if (endVertex.getInEdgesMap().size() == 0) {
                    noEdgeQueue.add(endVertex);
                }
            }
        }
        // if graph has edges then return null, because there was a cycle somewhere
        if (edgeCount != 0) {
            return null;
        } else { // else return the array of labels of the sorted list
            String[] topoArray = new String[topoList.size()];
            topoArray = topoList.toArray(topoArray);
            return topoArray;
        }
    }

    @Override
    // passes in string label for start vertex, then returns an array of shortestPathInfo objects for
    // every vertex in the graph, including itself
    public ShortestPathInfo[] shortestPath(String label) {
        // get the startVertex
        Vertex startVertex = nodeLabelMap.get(label);
        ShortestPathInfo[] shortestPathArray = new ShortestPathInfo[nodeLabelMap.size()];

        int i = 0;
        this.dijkstrasShortestPath(startVertex, startVertex);
        for (HashMap.Entry<String, Vertex> entry : nodeLabelMap.entrySet()) {
            Vertex currentVertex = entry.getValue();
            long totalDistance;
            if (currentVertex.getDistance() == Double.POSITIVE_INFINITY) {
                totalDistance = -1;
            } else {
                totalDistance = (long)currentVertex.getDistance();
            }
            shortestPathArray[i] = new ShortestPathInfo(currentVertex.getLabel(), totalDistance);
            i++;
        }
        return shortestPathArray;
    }

    public void dijkstrasShortestPath(Vertex startVertex, Vertex destinationVertex) {

        // initialize the start vertex
        startVertex.setDistance(0);
        startVertex.setKnown(false);
        startVertex.setPrevious(null);

        // create priority queue
        PriorityQueue<Vertex> priorityQueue = new PriorityQueue<>(nodeLabelMap.size(), new VertexComparator());
        priorityQueue.add(startVertex);

        // for each vertex in the DiGraph, if the vertex is not the startVertex then set distance to infinity
        // and set previous to null;
        for (Map.Entry<String, Vertex> entry : nodeLabelMap.entrySet()) {
            Vertex currentVertex = entry.getValue();
            if (currentVertex.getIdNum() != startVertex.getIdNum()) {
                currentVertex.setDistance(Double.POSITIVE_INFINITY);
                currentVertex.setPrevious(null);
                currentVertex.setKnown(false);
            }
        }

        // while pQueue is not empty...
        while (!priorityQueue.isEmpty()) {
            Vertex currentVertex = priorityQueue.poll();
            // if currentVertex is not known
            if (!currentVertex.getKnown()) {
                currentVertex.setKnown(true);

                // for each vertex adj to the currentVertex
                for (ConcurrentHashMap.Entry<String, Edge> entry : currentVertex.getOutEdgesMap().entrySet()) {
                    Edge adjEdge = entry.getValue();
                    Vertex adjVertex = nodeLabelMap.get(entry.getKey());

                    Double newDistance = currentVertex.getDistance() + adjEdge.getWeight();
                    if (adjVertex.getDistance() > newDistance) {
                        adjVertex.setDistance(newDistance);
                        adjVertex.setPrevious(currentVertex);

                        // have to add the new updated adjVertex as a new node, so it can be revisited again
                        priorityQueue.add(adjVertex);
                    }
                }
            }
        }
        long totalDistance;
        if (destinationVertex.getDistance() == Double.POSITIVE_INFINITY) {
             totalDistance = -1;
        } else {
            totalDistance = (long)destinationVertex.getDistance();
        }
        ShortestPathInfo shortestPath = new ShortestPathInfo(destinationVertex.getLabel(), totalDistance);
    }
}
