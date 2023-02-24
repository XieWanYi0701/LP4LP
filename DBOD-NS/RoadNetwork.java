package crowdsourcing;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;
import java.io.IOException;
import java.lang.NullPointerException;
import java.util.Random;
import java.util.ArrayList;

import crowdsourcing.BidirectionalDijkstra;
import crowdsourcing.PreProcess;
import crowdsourcing.Vertex;

public class RoadNetwork implements Serializable, Cloneable {
    ArrayList<Double> lons;
    ArrayList<Double> lats;
    Vertex[] graph;
    int[] nodeOrdering;
    int queries;

    public long getDistance(BidirectionalDijkstra bd, int from, int to) {
        long dist = bd.computeDist(this.graph, from, to, this.queries, this.nodeOrdering);
        this.queries += 1;
        return dist;
    }

    public int findNode(double lon, double lat) {
        Double min_dist = Double.POSITIVE_INFINITY;
        Double min_lon = Double.POSITIVE_INFINITY;
        Double min_lat = Double.POSITIVE_INFINITY;
        int min_index = -1;
        for (int i = 0; i < this.lons.size(); i++) {
            Double dist = Math.pow(lon - this.lons.get(i), 2) + Math.pow(lat - this.lats.get(i), 2);
            if (dist < min_dist) {
                min_dist = dist;
                min_index = i;
                min_lon = this.lons.get(i);
                min_lat = this.lats.get(i);
            }
        }
        return min_index;
    }

    public void buildGraph() {
        this.lons = new ArrayList<Double>();
        this.lats = new ArrayList<Double>();
        this.queries = 0;
        HashMap<String, Integer> vertex_map = new HashMap<String, Integer>();
        String delim = ",", line = "";
        int num_nodes = 0, num_edges = 0;

        // Read nodes
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/nodes.csv"));
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                // 0 1 2
                // array of [id, lon, lat]
                String[] record = line.split(delim);
                double lon = Double.parseDouble(record[1]);
                double lat = Double.parseDouble(record[2]);
                this.lons.add(lon);
                this.lats.add(lat);
                vertex_map.put(record[0], num_nodes);
                num_nodes++;
            }
            System.out.println("Read " + num_nodes + " nodes");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.graph = new Vertex[num_nodes];
        for (int i = 0; i < num_nodes; i++) {
            this.graph[i] = new Vertex(i);
        }

        // Read edges
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data/edges.csv"));
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                // 0   1       2       3       4     5            6             7             8              9
                // id, source, target, length, foot, car_forward, car_backward, bike_forward, bike_backward, wkt
                String[] record = line.split(delim);
                Long length = (long) (Double.parseDouble(record[3]) * 1000);
                Integer source = vertex_map.get(record[1]);
                Integer target = vertex_map.get(record[2]);
                if (source == null || target == null) {
                    continue;
                }

                this.graph[source].outEdges.add(target);
                this.graph[source].outECost.add(length);
                this.graph[target].inEdges.add(source);
                this.graph[target].inECost.add(length);
                num_edges++;
                // if (Integer.parseInt(record[5]) > 0) {
                //     this.graph[source].outEdges.add(target);
                //     this.graph[source].outECost.add(length);
                //     num_edges++;
                // }
                // if (Integer.parseInt(record[6]) > 0) {
                //     this.graph[target].inEdges.add(source);
                //     this.graph[target].inECost.add(length);
                //     num_edges++;
                // }
            }
            System.out.println("Read " + num_edges + " edges");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Preprocess
        try {
            String graph_filename = "data/graph.obj", ordering_filename = "data/ordering.obj";
            if ((new File(graph_filename)).isFile() && (new File(ordering_filename)).isFile()) {
                System.out.println("Preprocessing (cached)...");
                ObjectInputStream in;
                in = new ObjectInputStream(new FileInputStream(graph_filename));
                this.graph = (Vertex[]) in.readObject();
                in.close();
                in = new ObjectInputStream(new FileInputStream(ordering_filename));
                this.nodeOrdering = (int[]) in.readObject();
                in.close();
            } else {
                System.out.println("Preprocessing...");
                ObjectOutputStream out;
                out = new ObjectOutputStream(new FileOutputStream(graph_filename));
                out.writeObject(this.graph);
                out.close();
                out = new ObjectOutputStream(new FileOutputStream(ordering_filename));
                out.writeObject(this.nodeOrdering);
                out.close();
                PreProcess process = new PreProcess();
                this.nodeOrdering = process.processing(this.graph);
                out = new ObjectOutputStream(new FileOutputStream(graph_filename));
                out.writeObject(this.graph);
                out.close();
                out = new ObjectOutputStream(new FileOutputStream(ordering_filename));
                out.writeObject(this.nodeOrdering);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Ready!");
    }

}


class DemoRoadNetwork {
    public static void readData(RoadNetwork net) {
        int num_orders = 0;
        int num_reachable = 0;
        int num_unreachable = 0;
        int num_filter = 0;
        ArrayList<Integer> orders = new ArrayList<Integer>();
        BidirectionalDijkstra bd = new BidirectionalDijkstra();
        try {
            String delim = ",", line = "";
            BufferedReader reader = new BufferedReader(new FileReader("data/data.csv"));
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) { first = false; continue; }
                if (num_reachable == 100) { break; }
                String[] record = line.split(delim);
                // 9              10                11               12                 13
                // trip_distance, pickup_longitude, pickup_latitude, dropoff_longitude, dropoff_latitude
                double dist_km = Double.parseDouble(record[9]);
                double pickup_lon = Double.parseDouble(record[10]);
                double pickup_lat = Double.parseDouble(record[11]);
                double dropoff_lon = Double.parseDouble(record[12]);
                double dropoff_lat = Double.parseDouble(record[13]);

                int from = net.findNode(pickup_lon, pickup_lat);
                int to = net.findNode(dropoff_lon, dropoff_lat);
                double dist_road = net.getDistance(bd, from, to);
                if (dist_road > 0) {
                    double dist_road_km = net.getDistance(bd, from, to) / 1000.0 / 1000.0;
                    System.out.printf("#%d: from %d (%f, %f) to %d (%f, %f): %f %f (%f) (%d/%d/%d/%d)\n", num_orders,
                                      from, pickup_lon, pickup_lat, to, dropoff_lon, dropoff_lat,
                                      dist_km, dist_road_km, dist_km - dist_road_km,
                                      num_orders, num_reachable, num_unreachable, 0);
                    num_reachable++;
                } else {
                    num_unreachable++;
                }

                num_orders++;
            }
            System.out.printf("Orders/Unreachable: %d/%d\n", num_orders, num_unreachable);

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RoadNetwork net = new RoadNetwork();
        try {
            String network_filename = "data/network.obj";
            if ((new File(network_filename)).isFile()) {
                System.out.println("Read road network (cached)...");
                ObjectInputStream in;
                in = new ObjectInputStream(new FileInputStream(network_filename));
                net = (RoadNetwork) in.readObject();
                in.close();
            } else {
                System.out.println("Build road network...");
                ObjectOutputStream out;
                out = new ObjectOutputStream(new FileOutputStream(network_filename));
                out.writeObject(net);
                out.close();
                net.buildGraph();
                out = new ObjectOutputStream(new FileOutputStream(network_filename));
                out.writeObject(net);
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // readData(net);
    }
}
