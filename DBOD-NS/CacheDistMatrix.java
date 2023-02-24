package crowdsourcing;

import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.lang.Math.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.*;
import crowdsourcing.RoadNetwork;

class Worker extends Thread {
    public double[][] ret;
    public int idx, start, size;
    private RoadNetwork net;
    private Logger logger;
    private ArrayList<Object[]> orders;
    private ArrayList<Object[]> vehicles;
    private String outfile;
    private BidirectionalDijkstra bd;
    private ArrayList<Integer> node_orders, node_vehicles;
    private String prefix;

    Worker(ArrayList<Object[]> orders, ArrayList<Object[]> vehicles, int idx, int start, int size) {
        this.bd = new BidirectionalDijkstra();
        this.prefix = "Thread " + idx + ": ";
        this.orders = orders;
        this.vehicles = vehicles;
        this.idx = idx;
        this.start = start;
        this.size = size;
        this.outfile = "data/out_" + this.idx + ".csv";


        this.logger = Logger.getLogger("Thread " + this.idx);
        // try {
        //     FileHandler fh = new FileHandler("logs/cache_thread_" + this.idx + ".log");
        //     this.logger.addHandler(fh);
        //     this.logger.setLevel(Level.ALL);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     System.exit(1);
        // }
    }

    public double getDist(int from, int to) {
        Double dist = 10000.0;
        Long dist_road = this.net.getDistance(this.bd, from, to);
        if (dist_road > 0) {
            dist = dist_road / 1000.0 / 1000.0;
        }
        return dist;
    }

    public void writeDistMatrix() {
        int part = (int) (this.size * 0.1);
        try {
            int row_start = 0, col_start = 0;
            if ((new File(this.outfile)).isFile()) {
                String line = "", delim = ",";
                BufferedReader reader = new BufferedReader(new FileReader(this.outfile));
                int rows = 0;
                while ((line = reader.readLine()) != null) {
                    String[] record = line.split(delim);
                    rows++;
                    row_start = rows - 1;
                    col_start = record.length;
                }
                reader.close();
                this.logger.info(this.prefix + "resume from " + row_start + ", " + col_start);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(this.outfile, true));
            for (int r = row_start; r < this.node_orders.size(); r++) {
                int r_graph = this.node_orders.get(r);
                for (int c = (r == row_start ? col_start : 0); c < this.node_vehicles.size(); c++) {
                    int c_graph = this.node_vehicles.get(c);
                    double dist = this.getDist(r_graph, c_graph);
                    writer.write(dist + (c+1==this.node_vehicles.size() ? "\n" : ","));
                    writer.flush();
                }
                int percent = (int) (r * 100.0 / this.size);
                if (percent % 10 == 0) { this.logger.info(this.prefix + r + " - " + percent + "%"); }
                break;
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long time1, time2;
        this.logger = Logger.getLogger("" + Thread.currentThread().getId());
        this.logger.info(this.prefix + "start");

        // Read network
        try {
            time1 = System.currentTimeMillis();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("data/network.obj"));
            this.net = (RoadNetwork) in.readObject();
            time2 = System.currentTimeMillis();
            this.logger.info(this.prefix + "Read road network - total time: " + (time2 - time1) / 1000.0 + " s");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Get data
        time1 = System.currentTimeMillis();
        this.node_orders = new ArrayList<Integer>();
        this.node_vehicles = new ArrayList<Integer>();
        for (int i = this.start; i < this.start + this.size; i++) {
            double lon = (double) this.orders.get(i)[3], lat = (double) this.orders.get(i)[4];
            this.node_orders.add(net.findNode(lon, lat));
        }
        for (int i = 0; i < this.vehicles.size(); i++) {
            double lon = (double) this.vehicles.get(i)[1], lat = (double) this.vehicles.get(i)[2];
            this.node_vehicles.add(net.findNode(lon, lat));
        }
        time2 = System.currentTimeMillis();
        this.logger.info(this.prefix + "Find node - total time: " + (time2 - time1) / 1000.0 + " s");

        // Compute distance
        time1 = System.currentTimeMillis();
        writeDistMatrix();
        time2 = System.currentTimeMillis();
        this.logger.info(this.prefix + "Get distance matrix - total time: " + (time2 - time1) / 1000.0 + " s");
    }
};

public class CacheDistMatrix {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("CacheDistMatrix");
        // try {
        //     FileHandler fh = new FileHandler("logs/cache_main.log");
        //     logger.addHandler(fh);
        //     logger.setLevel(Level.ALL);
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     System.exit(1);
        // }

        String filename_orders = args[0], filename_vehicles = args[1];
        int n_threads = 50;
        long time1, time2;
        long start_time, end_time;
        start_time = System.currentTimeMillis();

        // Read data
        String sdf = "yyyy-MM-dd HH:mm:ss";
        String line = "";
        String delim = ",";
        ArrayList<Object[]> orders = new ArrayList<Object[]>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename_orders));
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                // if (first) { first = false; continue; }
                Object[] record = new Object[6];
                String[] row = line.split(delim);
                boolean skip = false;
                for (int i = 1; i <= 5; i++) {
                    if (Double.parseDouble(row[i]) == 0.0) { skip = true; break; }
                }
                if (skip) { skip = false; continue; }
                record[0] = stringToDate(row[0], sdf);
                for (int j = 1; j < record.length; j++) {
                    record[j] = Double.valueOf(row[j].toString());
                }
                orders.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Read " + orders.size() + " lines");

        ArrayList<Object[]> vehicles = new ArrayList<Object[]>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename_vehicles));
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                // if (first) { first = false; continue; }
                Object[] record = new Object[4];
                String[] row = line.split(delim);
                boolean skip = false;
                for (int i = 1; i <= 2; i++) {
                    if (Double.parseDouble(row[i]) == 0.0) { skip = true; break; }
                }
                if (skip) { skip = false; continue; }
                record[0] = stringToDate(row[0], sdf);
                for (int j = 1; j < row.length; j++) {
                    record[j] = Double.valueOf(row[j].toString());
                }
                vehicles.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Read " + vehicles.size() + " lines");

        // Start threads
        Worker[] workers = new Worker[n_threads];
        for (int i = 0; i < n_threads; i++) {
            int idx = i;
            int size = (int) Math.ceil(orders.size() / (double) n_threads);
            int start = size * i;
            workers[i] = new Worker(orders, vehicles, idx, start, (start+size>=orders.size() ? orders.size()-start : size));
            workers[i].start();
        }

        try {
            for (int i = 0; i < n_threads; i++) {
                workers[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Write to file
        // try {
        //     FileWriter writer = new FileWriter(filename_output);
        //     for (int i = 0; i < n_threads; i++) {
        //         int start = workers[i].start;
        //         int size = workers[i].size;
        //         double[][] ret = workers[i].ret;
        //         for (int r = 0; r < ret.length; r++) {
        //             for (int c = 0; c < ret[r].length; c++) {
        //                 writer.write(ret[r][c] + (c+1==ret[r].length ? "\n": ","));
        //             }
        //         }
        //     }
        //     writer.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

        end_time = System.currentTimeMillis();
        logger.info("Total time: " + (end_time - start_time) / 1000.0 + " s");
    }

    //字符串日期转换
    public static Date stringToDate(String strDate, String format) {
        Date date = null;
        if (format != null && !"".equals(format)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                date = sdf.parse(strDate);
            } catch (Exception e) {
                date = null;
            }
        }
        return date;
    }

}
