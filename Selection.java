import java.util.Scanner;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.*;

public class Selection{

    //class representing edges in a graph, used in the Graph class
    static class Edge {
        int source;
        int destination;
        int weight;

        public Edge(int source, int destination, int weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }
    }

    //heapnode object, used by the minheap class below
    static class HeapNode{
        int vertex;
        int distance;
        int pred;
    }

    //graph class, containing within it a method implementing Dijkstra's shortest path algorithm on the specified graph object
    // This implementation of dijkstra's algorithm using a min heap was taken from the tutorialhorizon dijkstra's implementation at
    // https://algorithms.tutorialhorizon.com/dijkstras-shortest-path-algorithm-spt-adjacency-list-and-min-heap-java-implementation/
    static class Graph {
        int vertices;
        LinkedList<Edge>[] adjList;

        Graph(int vertices) {
            this.vertices = vertices;
            adjList = new LinkedList[vertices];
            for (int i = 0; i < vertices; i++) {
                adjList[i] = new LinkedList();
            }
        }

        public void addEdge(int source, int destination, int weight) {
            Edge edge = new Edge(source, destination, weight);
            adjList[source].add(edge); //for directed graph
        }

        // public void removeEdge(int source, int destination){
        //     Edge edge = new Edge(source, destination, weight);
        //     adjList[source].remove(edge);
        // }

        //method to perform dijkstra's shortest path algorithm
        public LinkedList<Integer> dijkstra_GetMinDistances(int sourceVertex){
            int INFINITY = Integer.MAX_VALUE;
            boolean[] SPT = new boolean[vertices];

            //create heapNode for all the vertices
            HeapNode [] heapNodes = new HeapNode[vertices];
            for (int i = 0; i <vertices ; i++) {
                heapNodes[i] = new HeapNode();
                heapNodes[i].vertex = i;
                heapNodes[i].distance = INFINITY;
                heapNodes[i].pred = -1;
            }

            //decrease the distance for the first index
            heapNodes[sourceVertex].distance = 0;

            //add all the vertices to the MinHeap
            MinHeap minHeap = new MinHeap(vertices);
            for (int i = 0; i <vertices ; i++) {
                minHeap.insert(heapNodes[i]);
            }
            //while minHeap is not empty
            while(minHeap.currentSize != 0){
                //extract the min
                HeapNode extractedNode = minHeap.extractMin();

                //extracted vertex
                int extractedVertex = extractedNode.vertex;
                SPT[extractedVertex] = true;

                //iterate through all the adjacent vertices
                LinkedList<Edge> list = adjList[extractedVertex];
                for (int i = 0; i <list.size() ; i++) {
                    Edge edge = list.get(i);
                    int destination = edge.destination;
                    //only if  destination vertex is not present in SPT
                    if(SPT[destination]==false ) {
                        ///check if distance needs an update or not
                        //means check total weight from source to vertex_V is less than
                        //the current distance value, if yes then update the distance
                        int newKey =  heapNodes[extractedVertex].distance + edge.weight ;
                        int currentKey = heapNodes[destination].distance;
                        if(currentKey>newKey){
                            decreaseKey(minHeap, newKey, destination);
                            heapNodes[destination].distance = newKey;
                            heapNodes[destination].pred = extractedVertex;

                        }
                    }
                }
            }
            //print SPT
            //printDijkstra(heapNodes, sourceVertex);
            LinkedList<Integer> path = getPath(heapNodes, sourceVertex);
            path.add(heapNodes[vertices-1].distance);
            return path;
        }

        public void decreaseKey(MinHeap minHeap, int newKey, int vertex){

            //get the index which distance's needs a decrease;
            int index = minHeap.indexes[vertex];

            //get the node and update its value
            HeapNode node = minHeap.mH[index];
            node.distance = newKey;
            minHeap.bubbleUp(index);
        }

        public LinkedList<Integer> getPath(HeapNode[] resultSet, int sourceVertex){
            int dest = vertices-1;
            LinkedList<Integer> path = new LinkedList<>();
            while(resultSet[dest].pred != -1){
                path.add(resultSet[dest].vertex);
                dest = resultSet[dest].pred;
            }
            path.removeFirst();
            return path;        
        }


        public void printDijkstra(HeapNode[] resultSet, int sourceVertex){
            System.out.println("Dijkstra Algorithm: (Adjacency List + Min Heap)");
            for (int i = 0; i <vertices ; i++) {
                System.out.println("Source Vertex: " + sourceVertex + " to vertex " +   + i +
                        " distance: " + resultSet[i].distance + " via " + resultSet[i].pred);
            }

            int dest = resultSet.length-1;
            LinkedList<Integer> path = new LinkedList<>();
            while(resultSet[dest].pred != -1){
                path.add(resultSet[dest].vertex);
                dest = resultSet[dest].pred;
            }
            path.removeFirst();
            
        }

        public void printGraph(){
            for (int i = 0; i <vertices ; i++) {
                LinkedList<Edge> list = adjList[i];
                for (int j = 0; j < list.size(); j++) {
                    System.out.println("vertex-" + i + " is connected to " +
                            list.get(j).destination + " with weight " +  list.get(j).weight);
                }
            }
        }
    }

    //minHeap class is used in dijkstra's to keep track of nodes to visit
    //This minheap implementation was also taken from the tutorialhorizon dijkstra's implementation at
    // https://algorithms.tutorialhorizon.com/dijkstras-shortest-path-algorithm-spt-adjacency-list-and-min-heap-java-implementation/
    static class MinHeap{
        int capacity;
        int currentSize;
        HeapNode[] mH;
        int [] indexes; //will be used to decrease the distance


        public MinHeap(int capacity) {
            this.capacity = capacity;
            mH = new HeapNode[capacity + 1];
            indexes = new int[capacity];
            mH[0] = new HeapNode();
            mH[0].distance = Integer.MIN_VALUE;
            mH[0].vertex=-1;
            currentSize = 0;
        }

        public void insert(HeapNode x) {
            currentSize++;
            int idx = currentSize;
            mH[idx] = x;
            indexes[x.vertex] = idx;
            bubbleUp(idx);
        }

        public void bubbleUp(int pos) {
            int parentIdx = pos/2;
            int currentIdx = pos;
            while (currentIdx > 0 && mH[parentIdx].distance > mH[currentIdx].distance) {
                HeapNode currentNode = mH[currentIdx];
                HeapNode parentNode = mH[parentIdx];

                //swap the positions
                indexes[currentNode.vertex] = parentIdx;
                indexes[parentNode.vertex] = currentIdx;
                swap(currentIdx,parentIdx);
                currentIdx = parentIdx;
                parentIdx = parentIdx/2;
            }
        }

        public HeapNode extractMin() {
            HeapNode min = mH[1];
            HeapNode lastNode = mH[currentSize];
//            update the indexes[] and move the last node to the top
            indexes[lastNode.vertex] = 1;
            mH[1] = lastNode;
            mH[currentSize] = null;
            sinkDown(1);
            currentSize--;
            return min;
        }

        public void sinkDown(int k) {
            int smallest = k;
            int leftChildIdx = 2 * k;
            int rightChildIdx = 2 * k+1;
            if (leftChildIdx < currentSize && mH[smallest].distance > mH[leftChildIdx].distance) {
                smallest = leftChildIdx;
            }
            if (rightChildIdx < currentSize && mH[smallest].distance > mH[rightChildIdx].distance) {
                smallest = rightChildIdx;
            }
            if (smallest != k) {

                HeapNode smallestNode = mH[smallest];
                HeapNode kNode = mH[k];

                //swap the positions
                indexes[smallestNode.vertex] = k;
                indexes[kNode.vertex] = smallest;
                swap(k, smallest);
                sinkDown(smallest);
            }
        }

        public void swap(int a, int b) {
            HeapNode temp = mH[a];
            mH[a] = mH[b];
            mH[b] = temp;
        }
    }


    //Function for parsing a data file for the input. 
    //Takes in file name, returns 2D array of String with each row being [ride name, start time, end time]
    private static String[][] parse(String file) {
        Scanner sc = null;
        try {
            File f = new File(file);
            sc = new Scanner(f);
        }   
        catch (FileNotFoundException e) {
            System.out.println("Error opening given file.");
        }

        String line = "";
        while(sc.hasNextLine()){
            line += sc.nextLine();
        }
        line = line.replaceAll("[:\\[),\\s]", " ");
        line = line.replaceAll("  ", " ");
        String[] toks = line.split(" ");

        String[][] rides = new String[toks.length/3][3];
        for(int i=0; i<toks.length; i+=3){
            rides[i/3][0] = toks[i];
            rides[i/3][1] = toks[i+1];
            rides[i/3][2] = toks[i+2];
        }
        return rides;
    }

    public static void main(String[] args){
        String file = args[0];
        //each row holds [ride name, start time, end time]
        String[][] rides = parse(file);

        //build int array of times from rides
        String[][] temp = new String[rides.length][2];
        int[][] times = new int[rides.length + 2][2];
        for (int i = 0; i < times.length-2; i++) {
            temp[i] = Arrays.copyOfRange(rides[i], 1, 3);
            for(int j=0; j<2; j++){
                times[i+1][j] = Integer.parseInt(temp[i][j]);
            }
        }
        // for(int[] a : times)
        //     System.out.println(Arrays.toString(a));
        
        //find maximum end time, create start and end dummy nodes
        int maxTime = 0;
        for(int i=0; i<times.length; i++){
                maxTime = Math.max(times[i][1], maxTime);
        }
        times[0] = new int[]{0,0};
        times[times.length-1] = new int[]{maxTime, maxTime};
        
        //make weighted directed graph, adding edges from ride A to ride B if A ends before B starts, with weight (B.start - A.end)
        Graph graph = new Graph(times.length);
        for(int i=0; i<times.length-1; i++){
            int endtime = times[i][1];
            for(int j=0; j<times.length; j++){
                //dont add edge from dummy start to dummy end
                if (i==0 && j==times.length-1){
                    continue;
                }
                else if (times[j][0] >= endtime){
                    graph.addEdge(i, j, times[j][0] - endtime);
                }
            }
        }
        
        //call dijkstra to get min cost path (maximizes time on rides)
        //graph.printGraph();
        LinkedList<Integer> path = graph.dijkstra_GetMinDistances(0);
        int distance = path.removeLast();

        //trace best path backward and retrieve ride names from input file
        while(path.size() > 0){
            int i = path.pollLast();
            System.out.print(rides[i-1][0] + " ");
        }

    }
}