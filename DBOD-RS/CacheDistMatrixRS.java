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
    private ArrayList<Double> lons;
    private ArrayList<Double> lats;
    private String outfile;
    private BidirectionalDijkstra bd;
    private ArrayList<Integer> nodes;
    private String prefix;

    Worker(ArrayList<Double> lons, ArrayList<Double> lats, int idx, int start, int size) {
        this.bd = new BidirectionalDijkstra();
        this.prefix = "Thread " + idx + ": ";
        this.lons = lons;
        this.lats = lats;
        this.idx = idx;
        this.start = start;
        this.size = size;
        (new File("output")).mkdir();
        this.outfile = "output/out_" + this.idx + ".csv";


        this.logger = Logger.getLogger("Thread " + this.idx);
        this.logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
                private static final String format = "%1$tF %1$tT | %2$-7s | %3$s %n";
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                                         new Date(lr.getMillis()),
                                         lr.getLevel().getLocalizedName(),
                                         lr.getMessage());
                }
            });
        this.logger.addHandler(handler);
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
            for (int r = row_start + this.start; r < this.start + this.size; r++) {
                int r_graph = this.nodes.get(r);
                for (int c = (r == row_start ? col_start : 0); c < this.nodes.size(); c++) {
                    int c_graph = this.nodes.get(c);
                    double dist = this.getDist(r_graph, c_graph);
                    writer.write(dist + (c+1==this.nodes.size() ? "\n" : ","));
                    writer.flush();
                }
                int percent = (int) ((r - this.start) * 100.0 / this.size);
                if (percent % 10 == 0) { this.logger.info(this.prefix + r + " - " + percent + "%"); }
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
        this.nodes = new ArrayList<Integer>();
        // for (int i = this.start; i < this.start + this.size; i++) {
        //     double lon = this.lons.get(i), lat = this.lats.get(i);
        //     this.nodes.add(net.match(lon, lat));
        // }
        for (int i = 0; i < this.lons.size(); i++) {
            double lon = this.lons.get(i), lat = this.lats.get(i);
            this.nodes.add(net.match(lon, lat));
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

public class CacheDistMatrixRS {
    private static Logger logger;

    public void run(String orderFile, String vehicleFile) {
        this.logger = Logger.getLogger("CacheDistMatrixRS");
        this.logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
                private static final String format = "%1$tF %1$tT | %2$-7s | %3$s %n";
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                                         new Date(lr.getMillis()),
                                         lr.getLevel().getLocalizedName(),
                                         lr.getMessage());
                }
            });
        this.logger.addHandler(handler);

        int numThreads = 50;
        long time1, time2;
        long start, end;
        start = System.currentTimeMillis();
        ArrayList<Double> lons = new ArrayList<>();
        ArrayList<Double> lats = new ArrayList<>();

        String line = "", delim = ",", sdf = "yyyy-MM-dd HH:mm:ss";

        this.logger.info("Reading orders...");
        // format is [datetime, trip time (secs), trip distance,
        //            pickup longitude, pickup latitude,
        //            dropoff longitude, dropoff latitude, total amount]
        try {
            BufferedReader reader = new BufferedReader(new FileReader(orderFile));
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(delim);

                boolean skip = false;
                for (int i = 1; i < record.length; i++) {
                    if (Double.parseDouble(record[i]) == 0.0) { skip = true; break; }
                }
                if (skip) { continue; }

                double pickupLon = Double.valueOf(record[3]), pickupLat = Double.valueOf(record[4]);
                double dropoffLon = Double.valueOf(record[5]), dropoffLat = Double.valueOf(record[6]);

                lons.add(pickupLon);
                lats.add(pickupLat);
                lons.add(dropoffLon);
                lats.add(dropoffLat);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.logger.info("Reading vehicles...");
        // format is [datetime, lon, lat]
        try {
            BufferedReader reader = new BufferedReader(new FileReader(vehicleFile));
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(delim);

                // Check the record
                boolean skip = false;
                for (int i = 1; i < record.length; i++) {
                    if (Double.parseDouble(record[i]) == 0.0) { skip = true; break; }
                }
                if (skip) { continue; }

                // Create the object
                Date datetime = stringToDate(record[0], sdf);
                double lon = Double.valueOf(record[1]), lat = Double.valueOf(record[2]);
                lons.add(lon);
                lats.add(lat);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        assert lons.size() == lats.size();
        this.logger.info("Read " + lons.size() + " locations");

        // Start threads
        Worker[] workers = new Worker[numThreads];
        for (int i = 0; i < numThreads; i++) {
            int idx = i;
            int size = (int) Math.ceil(lons.size() / (double) numThreads);
            int s = size * i;
            workers[i] = new Worker(lons, lats, idx, s, (s+size>=lons.size() ? lons.size()-s : size));
            workers[i].start();
        }

        try {
            for (int i = 0; i < numThreads; i++) {
                workers[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        end = System.currentTimeMillis();
        this.logger.info("Total time: " + (end - start) / 1000.0 + " s");
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

    public static void main(String[] args) {
        CacheDistMatrixRS obj = new CacheDistMatrixRS();
        obj.run(args[0], args[1]);
    }
}
