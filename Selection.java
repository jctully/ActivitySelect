import java.util.Scanner;
import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.*;

public class Selection{

    static class Edge {
        int from;
        int to;
        int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    static class Graph {
        int vertices;
        ArrayList<Edge>[] adjList;

        Graph(int vertices) {
            this.vertices = vertices;
            adjList = new ArrayList[vertices];
            for (int i = 0; i < vertices; i++) {
                adjList[i] = new ArrayList();
            }
        }

        public void addEdge(int source, int destination, int weight) {
            Edge edge = new Edge(source, destination, weight);
            adjList[source].add(edge); //for directed graph
        }
        public void printGraph(){
            for (int i = 0; i <vertices ; i++) {
                ArrayList<Edge> list = adjList[i];
                for (int j = 0; j < list.size(); j++) {
                    System.out.println("vertex-" + i + " is connected to " +
                            list.get(j).to + " with weight " +  list.get(j).weight);
                }
            }
        }
    }

    //Function for parsing a data file for the input. Takes in file name, returns array of ints
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
        String[][] rides = parse(file);

        String[][] temp = new String[rides.length][2];
        int[][] times = new int[rides.length][2];

        //build int array of times
        for (int i = 0; i < times.length; i++) {
            temp[i] = Arrays.copyOfRange(rides[i], 1, 3);
            for(int j=0; j<2; j++){
                times[i][j] = Integer.parseInt(temp[i][j]);
            }
        }
        for(int[] a : times)
            System.out.println(Arrays.toString(a));
        
        //find maximum end time
        int maxTime = 0;
        for(int i=0; i<times.length; i++){
                maxTime = Math.max(times[i][1], maxTime);
        }

        //make graph, adding edges from ride A to ride B if B starts after A ends, with weight (B.start - A.end)
        Graph graph = new Graph(times.length);
        for(int i=0; i<times.length; i++){
            int endtime = times[i][1];
            for(int j=0; j<times.length; j++){
                if (times[j][0] >= endtime){
                    graph.addEdge(i, j, times[j][0] - endtime);
                }
            }
        }
        graph.printGraph();


        //call dijkstra to get min cost path (maximizes time on rides)




        //pick min cost ride and return

    }
}