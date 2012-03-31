/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.generator.core;

import cz.cvut.generator.graph.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ZDENEK
 */
public class Generator implements GeneratorOutputI, GeneratorConfigI {
    Graph g;
    private int nodeCount;                     //number of nodes
    private double minWeight;                   //minimal weight of edge
    private double maxWeight;                   //maximal weight of edge
    private int componentCount;                 //number of graph components
    private ArrayList<GraphType> properties;    //list of graph properties
    private Node[] nodes;
    private ArrayList<Node> nodeList;
    private ArrayList<Edge> edgeList;
    private boolean directed;
    private boolean weighted;
    private Random rand;
    
    
    public Generator(){
        g = new Graph();
        directed = false;
        weighted = false;
        rand = new Random();
    }
    
    public void initialize(){
        generateNodes();
        edgeList = new ArrayList<Edge>();
        if(properties.contains(GraphType.DIRECTED))directed = true;
        if(properties.contains(GraphType.WEIGHTED))weighted = true;
    }
    
    //TODO
    public void generate(){
        
    }
    
    private void generateNodes(){
        nodes = new Node[nodeCount];
        //creation array of nodes with id from 0 to nodeCount
        for(int i = 0; i < nodeCount; i++){
            nodes[i] = new Node(i, "Node: " + Integer.toString(i));         //or should id's be random???
        }
        //shuffle array
        for(int i = 0; i < nodeCount; i++){
            int rna = rand.nextInt(nodeCount);
            int rnb = rand.nextInt(nodeCount);
            Node tmp = nodes[rna];
            nodes[rna] = nodes[rnb];
            nodes[rnb] = tmp;            
        }
        nodeList = new ArrayList<Node>(Arrays.asList(nodes));
    }
    
    public void generateComplete(){
        for(int i = 0; i < nodeCount; i++){
            for(int j = i+1; j < nodeCount; j++){
                edgeList.add(new Edge(nodes[i], nodes[j]));
            }
        }
    }
    
    public void generateSimple(){
        int maxDensity = (int)Math.sqrt(nodeCount); //maximal number of edges from one node
        //System.out.println("max density: " + maxDensity);
        int noEdge = 0;
        Edge e;
        ArrayList<Node> notUsed = new ArrayList<Node>(Arrays.asList(nodes));
        ArrayList<Node> connectedPart = new ArrayList<Node>();
        Node a = notUsed.remove(rand.nextInt(notUsed.size()));
        Node b = notUsed.remove(rand.nextInt(notUsed.size()));
        edgeList.add(new Edge(a, b));
        connectedPart.add(a);
        connectedPart.add(b);
        while(!notUsed.isEmpty()){
            a = notUsed.remove(rand.nextInt(notUsed.size()));
            //connect a to rest of yet connected graph
            b = connectedPart.get(rand.nextInt(connectedPart.size()));
            //switching orientation
            if(rand.nextInt(2) == 0) e = new Edge(a, b);
            else e = new Edge(b, a);
            //multiplicity check
            if(!edgeList.contains(e) && a != b){
                edgeList.add(e);
                connectedPart.add(a);               
            }
            //add random number of edges coming from a
            noEdge = rand.nextInt(maxDensity);
            for(int i = 0; i < noEdge; i++){
                b = nodes[rand.nextInt(nodeCount)];
                if(a != b)edgeList.add(new Edge(a, b));
                connectedPart.add(b);
            }          
        }
    }
    
    
    //TODO
    public void generateBipartite(){
        int maxEdgeCountPart1;
        int maxEdgeCountPart2;
        int edgeCount;
        Node a;
        Edge e;
        ArrayList<Node> part1 = new ArrayList(Arrays.asList(nodes));
        ArrayList<Node> part2 = new ArrayList<Node>();
        int minPartSize = (int) Math.sqrt(nodeCount);
        int part2size = rand.nextInt(nodeCount  - minPartSize) + minPartSize;
        //minimal part1 size ensuring
        while(part2size > nodeCount - minPartSize) part2size = rand.nextInt(nodeCount  - minPartSize) + minPartSize;
        //choose nodes for part2
        for(int i = 0; i < part2size; i++){
            part2.add(part1.remove(rand.nextInt(part1.size())));
        }
        maxEdgeCountPart1 = part2.size();
        maxEdgeCountPart2 = part1.size();
        //generate edges coming from part1 nodes
        for(Node n: part1){
            edgeCount = rand.nextInt(maxEdgeCountPart1);
            for(int i = 0; i < edgeCount; i++){
                a = part2.get(rand.nextInt(part2.size()));
                e = new Edge(n, a);
                if(!edgeList.contains(e)) edgeList.add(e);
            }
        }
        //generate edges coming from part2 nodes
        for(Node n: part2){
            edgeCount = rand.nextInt(maxEdgeCountPart2);
            for(int i = 0; i < edgeCount; i++){
                a = part1.get(rand.nextInt(part1.size()));
                e = new Edge(n, a);
                if(!edgeList.contains(e)) edgeList.add(e);
            }
        }
    }
    
    //TODO
    public void generateCyclic(){
        
    }
    
    //TODO
    public void generateAcyclic(){
        
    }
    
    //TODO
    public void generateTree(){
        
    }
    
    //TO ERASE
    //dummy method for parser testing
    public void generateFullDummy(){
        int nodeCount = 1000;   //set number of nodes as you wish
        
        ArrayList<Edge> edges = new ArrayList<Edge>();
        ArrayList<Node> nodesList = new ArrayList<Node>();
        Node[] nodes = new Node[nodeCount];
        for(int i = 0; i < nodeCount; i++){
            nodes[i] = new Node(i+1, "N:"+(i+1));
            nodesList.add(nodes[i]);
        }
        for(int i = 0; i < nodeCount; i++){
            for(int j = i+1; j < nodeCount; j++){
                edges.add(new Edge(nodes[i], nodes[j]));
            }
        }
        
        g.setDirected(false);
        g.setEdges(edges);
        g.setNodes(nodesList);       
    }
    
    private void prepareOutputGraph(){
        if(weighted){
            for(Edge e: edgeList){
            e.setWeight(rand.nextDouble() * (maxWeight - minWeight) + minWeight);
            }
        }        
        g.setEdges(edgeList);
        g.setNodes(nodeList);
        g.setDirected(directed);
    }

    @Override
    public Graph getGraph() {
        prepareOutputGraph();
        return g;
    }

    @Override
    public void setNodesCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    @Override
    public void setProperties(List<GraphType> property) {
        this.properties = (ArrayList<GraphType>) property;
    }

    @Override
    public void setMinEdgeWeight(double weight) {
        this.minWeight = weight;
    }

    @Override
    public void setMaxEdgeWeight(double weight) {
        this.maxWeight = weight;
    }

    @Override
    public void setComponentCount(int componentCount) {
        this.componentCount = componentCount;
    }
    
    public List<GraphType> getProperties(){
        return this.properties;
    }
    
    
    
}
