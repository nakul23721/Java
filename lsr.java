  
import java.io.*;
import java.util.PriorityQueue;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Comparator;
import java.util.Iterator;

class Vertex implements Comparable<Vertex>{
    public final String name;
    public ArrayList<Edge> adjacencies;
    public int minDistance = Integer.MAX_VALUE;
    public Vertex previous;
	
    public Vertex(String argName){
		name = argName;
	}
    public String toString(){
		return name;
	}
    public int compareTo(Vertex other){
        return Integer.compare(minDistance, other.minDistance);
    }
}

class Edge{
    public final Vertex target;
    public final int cost;
	
    public Edge(Vertex argTarget, int argCost){
		target = argTarget; cost = argCost;
	}
}

// for sorting
class CustomComparator implements Comparator<Vertex> {
    @Override
    public int compare(Vertex v1, Vertex v2){
        return v1.name.compareTo(v2.name);
    }
}

class LSRCompute{
    public static void computePaths(Vertex source) {
        source.minDistance = 0;
		
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
		/*
		Iterator<Vertex> iterator = vertexQueue.iterator();
		System.out.print("queue: ");
		while(iterator.hasNext()){
			System.out.print (iterator.next().toString() + " ");
		}
		System.out.println();*/
		
            Vertex u = vertexQueue.poll();
		/*
		iterator = vertexQueue.iterator();
		System.out.print("after queue: ");
		while(iterator.hasNext()){
			System.out.print (iterator.next().toString() + " ");
		}
		System.out.println();
		*/
			
            //System.out.print("u: "+u);
            // Visit each edge exiting u
            for (Edge e : u.adjacencies){
                //System.out.print(" e: "+e.target.name);
                Vertex v = e.target;
                int cost = e.cost;
                int distanceThroughU = u.minDistance + cost;
                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);

                    v.minDistance = distanceThroughU ;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }
	
    public static List<Vertex> getShortestPathTo(Vertex target) {
        List<Vertex> path = new ArrayList<Vertex>();
        for (Vertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }


    public static void main(String[] args){
        String[] nodeName = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

        int totalNodes = 0;
        int i = 0;
        int j = 0;

        try{
            LineNumberReader lnr = new LineNumberReader(new FileReader(new File("routes.lsa")));
            lnr.skip(Long.MAX_VALUE);
            //totalNodes = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
            totalNodes = lnr.getLineNumber();
            lnr.close();
        }catch(Exception e){//Catch exception if any
            System.err.println("Errors: " + e.getMessage());
        }

        // create all nodes by total number
        ArrayList<Vertex> tmpVertex = new ArrayList<Vertex>();
		
        for(i=0; i<totalNodes; i++){
            // create new node(A,B,C...), assign it into tmpVertex
            tmpVertex.add(new Vertex(nodeName[i]));
        }

        try{
            // Open the file
            FileInputStream fstream = new FileInputStream(args[0]);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;
            i = 0;
            //Read File Line By Line, for create neighbors
            while ((strLine = br.readLine()) != null){
                // split by spaces store in aLineOfrecord
                String[] aLineOfrecord = strLine.split("\\s+");

                // create neighbors array
                ArrayList<Edge> tmpNB = new ArrayList<Edge>();

                for(int neighbors=1; neighbors<aLineOfrecord.length; neighbors++){ // start at 1, exclude 0(newNode itself)
                    int nb = Arrays.asList(nodeName).indexOf( aLineOfrecord[neighbors].substring(0,1)); // take out the neighbor as alphabet
                    int cost = Integer.parseInt( aLineOfrecord[neighbors].substring(2,3) );  // take the neighbor cost
                    tmpNB.add( new Edge(tmpVertex.get(nb), cost) );  // add into the neighbor array
                }
                tmpVertex.get(j++).adjacencies = tmpNB;  // make the relation betwwen the newNode and its neighbors
            }

            //Close the input stream
            br.close();
        }
        catch(Exception e){//Catch exception if any
            System.err.println("Errorss: " + e.getMessage());
        }

        int src = 0;
        src = Arrays.asList(nodeName).indexOf( args[1] );

        switch( args[2].toUpperCase() ){
            case "CA":  // compute all nodes
                while(true){
                    computePaths(tmpVertex.get(src));  // recompute the shortest path
                    System.out.println( "Source " + tmpVertex.get(src) + ":" );

					for (Vertex v : tmpVertex) {  // print out all the shortest paths of each node
                        if(v!=tmpVertex.get(src)){
							System.out.print( v + ": " );
							List<Vertex> path = getShortestPathTo(v);
							System.out.print("Path: ");
							for(i = 0; i < path.size(); i++) {
								System.out.print( path.get(i) );
								if (i<path.size()-1)
									System.out.print( ">" );
							}
							System.out.println(" Cost:"+ v.minDistance);
						}
					}
                    printAllrela(tmpVertex);    // print out all nodes relations
                    addNode(tmpVertex, nodeName); // for add or delete nodes

                    for (Vertex allnodes : tmpVertex){ // reset all the distance
                        allnodes.minDistance = Integer.MAX_VALUE;
                    }

					pressAnyKeyToContinue();
				}
                //break;

            case "SS":  // print a specific node
                while(true){
                    computePaths(tmpVertex.get(src));  // recompute the shortest path
                    System.out.println( "Source " + tmpVertex.get(src) + ":" );

                    System.out.print("To which node? ");
                    Scanner sc = new Scanner(System.in);
                    String inputDes = sc.next().toUpperCase();

                    Vertex v = tmpVertex.get( Arrays.asList(nodeName).indexOf( inputDes ) );
                    System.out.print( v + ": " );
                    List<Vertex> path = getShortestPathTo(v);
                    System.out.print("Path: ");
                    for(i = 0; i < path.size(); i++) {
                        System.out.print( path.get(i) );
                        if (i<path.size()-1)
                            System.out.print( ">" );
                    }
                    System.out.print(" Cost:"+ v.minDistance);

                    printAllrela(tmpVertex);    // print out all nodes relations
                    addNode(tmpVertex, nodeName); // for add or delete nodes


                    for (Vertex allnodes : tmpVertex){ // reset all the distance
                        allnodes.minDistance = Integer.MAX_VALUE;;
                    }
                    pressAnyKeyToContinue();
                }
        }
    }

    private static void pressAnyKeyToContinue(){  // see function name
        System.out.println(" [Press any key to continue]");
        try {
            System.in.read();
        }
        catch(Exception e){
            System.err.println("Errorss: " + e.getMessage());
        }
    }

    // for add new node
    private static ArrayList<Vertex> addNode(ArrayList<Vertex> vertex, String[] nodes){
        System.out.print("do you want to add / delete node (y/n)? ");
        Scanner sc = new Scanner(System.in);
        String ans = sc.next().toUpperCase(); // for Y or N

        if(ans.toUpperCase().equals("Y")){
            System.out.print("so you want to add or delete(add/del)? ");
            sc = new Scanner(System.in);
            ans = sc.next().toUpperCase();

            if(ans.equals("ADD")){
                System.out.println("please type the new node relation (newNode: existNode1:cost existNode2:cost...):");
                sc = new Scanner(System.in);
                String newRecords = sc.nextLine().toUpperCase(); // for new records
                String[] aLineOfrecord = newRecords.split("\\s+"); // split by space
                // create neighbors array
                ArrayList<Edge> tmpNB = new ArrayList<Edge>();

                // new node
                vertex.add( new Vertex(aLineOfrecord[0].substring(0,1)) );

                // add neighbors
                for(int neighbors=1; neighbors<aLineOfrecord.length; neighbors++){
                    int nb = -1; // for locate neighbor index
                    for(Vertex nodename: vertex){ // finding index of existing node
                        if( nodename.name.equals(aLineOfrecord[neighbors].substring(0,1)) ){
                           nb = vertex.indexOf(nodename); // found index of the existing node, for add in neighbors list to newNode
                        }
                    }

                    int cost = Integer.parseInt( aLineOfrecord[neighbors].substring(2,3) );

                    //System.out.println("tmpNB.add( new Edge("+vertex.get(nb)+","+cost+") )");
                    tmpNB.add( new Edge(vertex.get(nb), cost) ); // add adjacencies to new node
                    //System.out.println(vertex.get(nb) + ".adjacencies.add(" + vertex.get(vertex.size()-1) +", "+ cost + ")");
                    vertex.get(nb).adjacencies.add( new Edge(vertex.get(vertex.size()-1), cost) ); // add adjacencies to new node
                }
                vertex.get(vertex.size()-1).adjacencies = tmpNB; // add the neighbor relations into newNode adjacencies
                //G: B:2 F:3
            }
            else if(ans.equals("DEL")){  // delete node
                System.out.println("which of the following node you want to delete? ");
                System.out.println(vertex);    // print out all existing nodes
                System.out.print("please enter one node only: ");
                sc = new Scanner(System.in);
                ans = sc.next().toUpperCase(); // get the node to be removed

                int i=0;
                while(i<vertex.size()){ // loop all vertexes to search
                    for(int j=0; j<vertex.get(i).adjacencies.size(); j++){
                        if(vertex.get(i).adjacencies.get(j).target.name.equals(ans)){  // if itself is the adjacencies of other nodes, remove
                            vertex.get(i).adjacencies.remove(j);
                        }
                    }

                    if( vertex.get(i).name.equals(ans) ){ // if found that node
                        //System.out.println(ans+" bye");   // delete
                        vertex.remove(i);                 // after remove, the nodes will pad to left, so position changed in the arraylist
                    }else{                                // so if remoced, dont increment i
                        i=i+1;
                    }
                }
            }
        }else if(ans.toUpperCase().equals("N")){
			//System.exit(0);
		}
        Collections.sort(vertex, new CustomComparator());  // sort nodeName in accending order

        return vertex;
    }

    public static void printAllrela(ArrayList<Vertex> vertexies){
        System.out.println();
        System.out.println("======= below are the relations =======");
        for(int i=0; i<vertexies.size(); i++){
            System.out.print(vertexies.get(i).name + ": ");
            for(int j=0; j<vertexies.get(i).adjacencies.size(); j++){
                 System.out.print(vertexies.get(i).adjacencies.get(j).target + ":" + vertexies.get(i).adjacencies.get(j).cost + " ");
            }
            System.out.println();
        }
    }
}
