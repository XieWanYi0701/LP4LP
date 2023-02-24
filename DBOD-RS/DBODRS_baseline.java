package crowdsourcing;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.lang.*;

class DummyRoadNetwork {
    final int MAXN = 1000;
    double[][] graph;
    ArrayList<Double> lons;
    ArrayList<Double> lats;
    double[][] matrix;
    DummyRoadNetwork() {
        this.lons = new ArrayList<Double>();
        this.lats = new ArrayList<Double>();
    }

    DummyRoadNetwork(String filename) {
        this.graph = new double[MAXN][MAXN];
        this.lons = new ArrayList<Double>();
        this.lats = new ArrayList<Double>();
        try {
            Scanner in = new Scanner(new File(filename));
            int n = in.nextInt();
            for (int i = 0; i < n; i++) {
                Double lon = in.nextDouble(), lat = in.nextDouble();
                lons.add(lon); lats.add(lat);
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) { graph[i][j] = in.nextDouble(); }
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void load(String filename, int size) {
        String line = "", delim = ",";
        this.matrix = new double[size][size];
        try {
            int count = 0;
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(delim);
                for (int i = 0; i < record.length; i++) {
                    this.matrix[count][i] = Double.valueOf(record[i]);
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public int match(double lon, double lat) {
        double bestDist, bestLon, bestLat;
        bestDist = bestLon = bestLat = Double.POSITIVE_INFINITY;
        int bestIndex = -1;
        for (int i = 0; i < this.lons.size(); i++) {
            double dist = Math.pow(lon - this.lons.get(i), 2) + Math.pow(lat - this.lats.get(i), 2);
            if (dist < bestDist) {
                bestDist = dist; bestIndex = i;
                bestLon = this.lons.get(i); bestLat = this.lats.get(i);
            }
        }
        return bestIndex;
    }

    public double compute(Location from, Location to) {
        if (from.index == -1 || to.index == -1) { return 0.0; }
        return this.matrix[from.index][to.index];
    }

    // public double compute(Location from, Location to) {
    //     if (from.node == -1 || to.node == -1) { return 0.0; }
    //     return this.graph[from.node][to.node];
    // }
};

class Config {
    public String orderFile, vehicleFile, matrixFile, resultFile, startDatetime;
    public int capacity, range, voratio;
    public double maxDist, maxDetour, alpha, meanTol, meanExp;
    public int numSelect, numFull;
    public String toString() {
        return String.format("Config(%s,\n%s,\n%s,\n%s,\n%s,\n%d, %d, %d,\n%f, %f, %f, %f, %f, %d, %d)",
                             orderFile, vehicleFile, matrixFile, resultFile, startDatetime,
                             capacity, range, voratio,
                             maxDist, maxDetour, alpha, meanTol, meanExp,
                             numSelect, numFull);
    }
};

class Result {
    int numOrders, numVehicles;
    double gamma, origProfit, addProfit, discountProfit;
    long time;
    Result(int o ,int v, double g, double op, double ap, double dp, long time) {
        this.numOrders = o; this.numVehicles = v;
        this.gamma = g; this.origProfit = op; this.addProfit = ap; this.discountProfit = dp;
        this.time = time;
    }
};

class Plan {
    public int pickup;
    public int dropoff;
    public double dx, dy, dz, dw;
    public double delta1, delta2; // delta1 = dx + dy - d(prev, next), delta2 = dz + dw - d(prev, next)
    public double delta;
    public double pickupDist, pickupDelta;
    public double gain;
    public Plan() {
        this.pickup = Integer.MIN_VALUE;
        this.dropoff = Integer.MIN_VALUE;
        this.dx = 0.0;
        this.dy = 0.0;
        this.dz = 0.0;
        this.dw = 0.0;
        this.delta1 = 0.0;
        this.delta2 = 0.0;
        this.delta = this.delta1 + this.delta2;
        this.pickupDist = 0;
        this.pickupDelta = 0;
        this.gain = Double.NEGATIVE_INFINITY;
    }
    public Plan(int u, int o, double dx, double dy, double dz, double dw,
                double delta1, double delta2, double p, double d, double g) {
        this.pickup = u;
        this.dropoff = o;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.dw = dw;
        this.delta1 = delta1;
        this.delta2 = delta2;
        this.delta = this.delta1 + this.delta2;
        this.pickupDist = p;
        this.pickupDelta = d;
        this.gain = g;
    }
	@Override
	public String toString() {
        return String.format("Plan(%d, %d, %f, %f, %f, %f, %f, %f)",
                             this.pickup, this.dropoff, this.gain,
                             this.dx, this.dy, this.dz, this.dw, this.pickupDist);
	}
};

class Mark {
    final static int SENTINEL = 1;
    final static int PICKUP = 2;
    final static int DROPOFF = 3;
    public Location location;
    public double accum;
    public Order order;
    public int type; // 0: sentinel, 1: pickup, 2: dropoff
    public Mark(Location l, double a, Order o, int t) {
        this.location = l;
        this.accum = a;
        this.order = o;
        this.type = t;
    }
    public String toString() {
        return "(" + this.location.index + ", " + this.accum + ", " + this.type + ")";
    }
};

class Bound implements Comparable<Bound> {
    double gamma, da, db, dc, gain;
    public Bound(double gamma, double da, double db, double dc, double gain) {
        this.gamma = gamma;
        this.da = da;
        this.db = db;
        this.dc = dc;
        this.gain = gain;
    }
    public int compareTo(Bound b) {
        if (this.gamma == b.gamma) {
            return 0;
        }
        else if (this.gamma > b.gamma) {
            return 1;
        }
        else {
            return -1;
        }
    }
    public String toString() {
        return String.format("Bound(%f, %f, %f, %f, %f)", this.gamma, this.da, this.db, this.dc, this.gain);
    }
};

class Timestamped {
    public Date datetime;
};

class Location {
    public double lon, lat;
    public int node, index;
    public Location() {
        this.lon = 0;
        this.lat = 0;
        this.node = -1;
        this.index = -1;
    }
    public Location(double lon, double lat, int node, int index) {
        this.lon = lon;
        this.lat = lat;
        this.node = node;
        this.index = index;
    }
	@Override
	public String toString() {
		return "[" + this.lon + ", " + this.lat + ", " + this.index + "]";
	}
};

class Order extends Timestamped implements Comparable<Order> {
    // public Date datetime;
    public Location pickup, dropoff;
    public double rev;
    public double tol;          // tolerant distance
    public double exp;          // expected discount factor
    public boolean assigned;
    public Order(Date datetime, Location pickup, Location dropoff, double rev, double tol, double exp) {
        this.datetime = datetime;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.rev = rev;
        this.tol = tol;
        this.exp = exp;

        this.assigned = false;
    }
    public int compareTo(Order o) {
        if (this.exp == o.exp) {
            return 0;
        }
        else if (this.exp > o.exp) {
            return 1;
        }
        else {
            return -1;
        }
    }
	@Override
	public String toString() {
		return "Order("+ this.datetime + ", " + this.pickup.index + ", " + this.dropoff.index
            + ", " + rev + ", " + tol + ", " + exp + ")";
	}
};

class Vehicle extends Timestamped {
    // public Date datetime;
    public Location location;
    public int remain;
    public ArrayList<Mark> route;
    public ArrayList<Order> orders;
    public ArrayList<Plan> plans;
    public boolean assigned;
    public Vehicle(Date d, Location l, int r) {
        this.datetime = d;
        this.location = l;

        this.remain = r;
        this.route = new ArrayList<Mark>();
        this.route.add(new Mark(this.location, 0.0, null, Mark.SENTINEL));
        this.route.add(new Mark(new Location(), 0.0, null, Mark.SENTINEL));
        this.orders = new ArrayList<Order>();
        this.plans = new ArrayList<Plan>();
        this.assigned = false;
    }
    public boolean isEmpty() {
        return (this.orders.size() == 0);
    }
    public boolean isFull() {
        return (this.remain == 0);
    }
    public void clear() {
        this.remain += this.orders.size();
        this.route = new ArrayList<Mark>();
        this.route.add(new Mark(this.location, 0.0, null, Mark.SENTINEL));
        this.route.add(new Mark(new Location(), 0.0, null, Mark.SENTINEL));
        this.orders = new ArrayList<Order>();
        this.plans = new ArrayList<Plan>();
    }
    public void assignOrder(Order order, Plan plan) {
        // 将订单插入到汽车的规划里
        int pickup = plan.pickup, dropoff = plan.dropoff;
        double dx = plan.dx, dy = plan.dy, dz = plan.dz, dw = plan.dw;

        // 插入接驾位置
        this.route.add(pickup, new Mark(order.pickup, dx + this.route.get(pickup-1).accum, order, Mark.PICKUP));
        for (int i = pickup + 1; i < this.route.size(); i++) {
            Mark m = this.route.get(i);
            m.accum += plan.delta1; // 该位置后面的累计路程增加
        }

        // 插入目标位置
        dropoff += 1;
        this.route.add(dropoff, new Mark(order.dropoff, dz + this.route.get(dropoff-1).accum, order, Mark.DROPOFF));
        for (int i = dropoff + 1; i < this.route.size(); i++) {
            Mark m = this.route.get(i);
            m.accum += plan.delta2; // 该位置后面的累计路程增加
        }

        this.orders.add(order);
        this.plans.add(plan);
        this.remain -= 1;
    }
    public Bound getBound(Order target, ArrayList<Order> select, Config config, RoadNetwork network) {
        // 判断目标订单是否可以加入

        // 把目标订单和已选择订单对应的位置挑出来
        ArrayList<Mark> seq = new ArrayList<>();
        int p = -1, q = -1;
        for (int i = 0; i < this.route.size(); i++) {
            Mark m = this.route.get(i);
            // System.out.println(m);
            if (m.order == target && m.type == Mark.PICKUP) { p = seq.size(); }
            if (m.order == target && m.type == Mark.DROPOFF) { q = seq.size(); }
            if (select.contains(m.order) || m.order == target || m.type == Mark.SENTINEL) { seq.add(m); }
        }
        assert (p != -1 && q != -1);

        // 计算目标订单造成的距离增量
        Location prev, curr, next;
        double dx, dy, dz;
        prev = seq.get(p - 1).location; curr = seq.get(p).location; next = seq.get((p + 1 == q ? p + 2 : p + 1)).location;
        dx = network.compute(prev, curr); dy = network.compute(curr, next); dz = network.compute(prev, next);
        double delta1 = dx + dy - dz;
        // System.out.println(dx + " + " + dy + " - " + dz);
        prev = seq.get(q - 1).location; curr = seq.get(q).location; next = seq.get(q + 1).location;
        dx = network.compute(prev, curr); dy = network.compute(curr, next); dz = network.compute(prev, next);
        double delta2 = dx + dy - dz;
        // System.out.println(dx + " + " + dy + " - " + dz);
        double delta = delta1 + delta2;
        // System.out.println(delta + " = " + delta1 + " + " + delta2);

        // 计算接驾距离
        double pickupDist = 0;
        for (int i = 1; i <= p; i++) {
            pickupDist += network.compute(seq.get(i-1).location, seq.get(i).location);
        }
        // System.out.println(pickupDist);

        // 计算目标订单对其他订单造成的接驾距离增量并与折扣相乘
        double pickupDelta = 0;
        for (int i = seq.size()-1; i > p; i--) {
            Mark m = seq.get(i);
            if (m.type == Mark.PICKUP) { pickupDelta += target.exp * delta1; }
        }
        for (int i = seq.size()-1; i > q; i--) {
            Mark m = seq.get(i);
            if (m.type == Mark.DROPOFF) { pickupDelta += target.exp * delta2; }
        }
        // System.out.println(pickupDelta);

        double gain = target.rev - config.alpha * delta
            - target.exp * (pickupDist - config.maxDist) - pickupDelta;
        return new Bound(target.exp, target.rev, delta, pickupDist - config.maxDist, gain);
    }
    public ArrayList<Bound> getBounds(Config config, RoadNetwork network) {
        ArrayList<Bound> bounds = new ArrayList<Bound>();
        Collections.sort(this.orders); // 先按照期望折扣从小到大排序

        ArrayList<Order> select = new ArrayList<>();
        double a = 0, b = 0, c = 0;
        for (int i = 0; i < this.orders.size(); i++) {
            Order target = this.orders.get(i);
            Bound bound = this.getBound(target, select, config, network); // 计算有效折扣对应增益
            // System.out.println(bound);
            // 增益大于零加入该订单
            if (bound.gain <= 0) { continue; }
            double da = bound.da, db = bound.db, dc = bound.dc;
            select.add(target);
            bounds.add(bound);

            // 计算无效折扣
            double gamma = ((a + da) - config.alpha * (b + db)) / (c + dc);
            a += da; b += db; c += dc;
            bounds.add(new Bound(gamma, a, b, c, 0));

            // 无效折扣小于等于下一个订单的期望折扣，整个删除，否则加入
            if (i+1<this.orders.size() && gamma <= this.orders.get(i+1).exp) {
                select.clear();
                a = b = c = 0;
            } else {
                select.add(target);
            }
        }
        return bounds;
    }
    public double getDelta(Order order, Plan plan, int stage, double maxDist, double gamma) {
        // 计算订单对其他订单的折扣增益
        // beyond global: sum( ep_i' * delta(pd_i'j) )
        // find discount factor: sum( gamma * delta(pd_i'j) )
        // final order dispatch: sum( gamma * I[pd_i'j > pd_0] * delta(pd_i'j) )

        // System.out.println(plan.pickup + ", " + plan.dropoff);
        // System.out.println(this.route);
        // if (plan.pickup < 0 || plan.dropoff < 0) { return 0.0; }
        assert plan.pickup >= 1 && plan.dropoff >= 1;

        double sum = 0;
        for (int i = this.route.size()-1; i >= plan.dropoff; i--) {
            Mark m = this.route.get(i);
            Order exclude = m.order;
            if (m.type == Mark.PICKUP) {
                int indicator = (m.accum > maxDist) ? 1 : 0;
                sum += (stage == DBODRS.STAGE_BEYOND) ? exclude.exp * plan.delta :
                    (stage == DBODRS.STAGE_FINAL && (m.accum+plan.delta>maxDist) && m.accum<=maxDist) ? gamma*(m.accum+plan.delta-maxDist) :
                    (stage == DBODRS.STAGE_FINAL) ? indicator * gamma * plan.delta : 0;
            }
        }
        for (int i = plan.dropoff-1; i >= plan.pickup; i--) {
            Mark m = this.route.get(i);
            Order exclude = m.order;
            if (m.type == Mark.PICKUP) {
                int indicator = (m.accum > maxDist) ? 1 : 0;
                sum += (stage == DBODRS.STAGE_BEYOND) ? exclude.exp * plan.delta1 :
                    (stage == DBODRS.STAGE_FINAL && (m.accum+plan.delta1>maxDist) && m.accum<=maxDist) ? gamma*(m.accum+plan.delta1-maxDist) :
                    (stage == DBODRS.STAGE_FINAL) ? indicator * gamma * plan.delta1 : 0;
            }
        }


        // double sum = 0;
        // for (int i = this.route.size()-1; i >= plan.dropoff; i--) {
        //     Mark m = this.route.get(i);
        //     Order exclude = m.order;
        //     if (m.type == Mark.PICKUP) {
        //         int indicator = (m.accum > maxDist) ? 1 : 0;
        //         sum += (stage == DBODRS.STAGE_BEYOND) ? exclude.exp * plan.delta2 :
        //             // (stage == DBODRS.STAGE_FIND) ? order.exp * plan.delta2 :
        //             (stage == DBODRS.STAGE_FINAL && (m.accum+plan.delta2>maxDist) && m.accum<=maxDist) ? gamma*(m.accum+plan.delta2-maxDist) :
        //             (stage == DBODRS.STAGE_FINAL) ? indicator * gamma * plan.delta2 : 0;
        //     }
        // }
        // for (int i = this.route.size()-1; i >= plan.pickup; i--) {
        //     Mark m = this.route.get(i);
        //     Order exclude = m.order;
        //     if (m.type == Mark.PICKUP) {
        //         int indicator = (m.accum > maxDist) ? 1 : 0;
        //         sum += (stage == DBODRS.STAGE_BEYOND) ? exclude.exp * plan.delta1 :
        //             // (stage == DBODRS.STAGE_FIND) ? order.exp * plan.delta1:
        //             (stage == DBODRS.STAGE_FINAL && (m.accum+plan.delta1>maxDist) && m.accum<=maxDist) ? gamma*(m.accum+plan.delta1-maxDist) :
        //             (stage == DBODRS.STAGE_FINAL) ? indicator * gamma * plan.delta1 : 0;
        //     }
        // }

        // System.out.println(this.route);
        // System.out.println("sum = " + sum);
        return sum;
    }
    public double getProfit(double alpha, double maxDist, double gamma, RoadNetwork network, Logger logger) {
        double sum = 0;
        double accum = 0;
        for (int i = 0; i < this.route.size(); i++) {
            Mark m = this.route.get(i);
            double pickupDist = m.accum;

            // 验证计算的累计距离是否正确
            accum = (i == 0) ? 0 : accum + network.compute(this.route.get(i-1).location, m.location);
            // System.out.println(accum + " == " + m.accum + "?");
            if (Math.abs(accum - m.accum) >= 1e-8) {
                logger.warning("accum: " + accum + ", m.accum: " + m.accum);
            }
            // assert Math.abs(accum - m.accum) < 1e-8;

            Order order = m.order;
            if (m.type == Mark.PICKUP) {
                double discount = (gamma == -1 ? order.exp : gamma) * (pickupDist - maxDist) * (pickupDist > maxDist ? 1 : 0);
                // System.out.println(String.format("%f - %f = %f - %f * (%f - %f) * %d", order.rev, discount,
                //                                  order.rev, (gamma == -1 ? order.exp : gamma), pickupDist, maxDist,
                //                                  (pickupDist > maxDist ? 1 : 0)));
                sum += order.rev - discount;
            }
        }
        // for (int i = 0; i < this.orders.size(); i++) {
        //     Order order = this.orders.get(i);
        //     Plan plan = this.plans.get(i);
        //     double pickupDist = 0;
        //     for (int j = 0; j < this.route.size(); i++) {
        //         Mark m = this.route.get(j);
        //         if (m.order == order && m.type == Mark.PICKUP) {
        //             pickupDist = m.accum; break;
        //         }
        //     }
        //     double discount = (gamma == -1 ? order.exp : gamma) * (pickupDist - maxDist) * (pickupDist > maxDist ? 1 : 0);
        //     System.out.println(String.format("%f - %f = %f - %f * (%f - %f)", order.rev, discount,
        //                                      order.rev, gamma, plan.pickupDist, maxDist));
        //     sum += order.rev - discount;
        // }
        // System.out.println("alpha * td: " + alpha + " * " + this.route.get(this.route.size() - 1).accum);

        sum -= alpha * this.route.get(this.route.size() - 1).accum;
        // System.out.println(this.route);
        // System.out.println("sum = " + sum);
        return sum;
    }
	@Override
	public String toString() {
        String routeStr = "";
        for (int i = 0; i < this.route.size(); i++) {
            routeStr += "" + this.route.get(i) + (i+1==this.route.size()?"":"->");
        }
		return "Vehicle(" + this.datetime + ", " + this.location + ", " + remain + ", " + routeStr + ")";
	}
};


public class DBODRS_baseline {
    private Logger logger;
    private Random rand;
    private Config config;
    private ArrayList<Order> orders;
    private ArrayList<Vehicle> vehicles;
    private RoadNetwork network;
    private int batch;

    public final static int STAGE_WITHIN = 1;
    public final static int STAGE_BEYOND = 2;
    public final static int STAGE_FIND = 3;
    public final static int STAGE_FINAL = 4;

    public DBODRS_baseline(Config config) {
        // Setup logger
        this.logger = Logger.getLogger("DBOSRS");
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
        try {
            (new File("logs")).mkdir();
            FileHandler fh = new FileHandler("logs/dbodrs.log");
            this.logger.addHandler(fh);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.logger.setLevel(Level.ALL);

        this.rand = new Random(42);
        this.config = config;
        this.orders = new ArrayList<Order>();
        this.vehicles = new ArrayList<Vehicle>();

        this.logger.info("\n" + config);
    }

    private static Date stringToDate(String strDate, String format) {
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

    private static ArrayList<Integer> getSplit(Date start, int batch, ArrayList<Timestamped> list) {
        ArrayList<Integer> split = new ArrayList<Integer>();
        for (int i = 0 ; i < list.size(); i++) {
            Timestamped obj = list.get(i);
            Date current = (Date) obj.datetime;
            if (current.compareTo(start) == 0) {
                split.add(i);
                break;
            }
        }
        for (int i = 0; i < list.size(); i++) {
            Date finish = new Date(start.getTime() + Long.valueOf(batch) * 1000);
            Timestamped obj = list.get(i);
            Date current = (Date) obj.datetime;
            if (current.compareTo(finish) >= 0) {
                split.add(i);
                start = finish;
            }
        }
        return split;
    }

    private static int[] findMaxGain(Plan[][] matrix) {
        double maxv = Double.NEGATIVE_INFINITY;
        int maxi = -1, maxj = -1;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j].gain > maxv) {
                    maxv = matrix[i][j].gain;
                    maxi = i;
                    maxj = j;
                }
            }
        }
        return new int[] {maxi, maxj};
    }

    private double getNextExponential(double lambda) {
        return Math.log(1 - this.rand.nextDouble()) *(-lambda);
    }

    private Plan routePlan(Order order, Vehicle vehicle, int stage, double gamma) {
        assert stage == STAGE_WITHIN || stage == STAGE_BEYOND || stage == STAGE_FINAL;
        // this.logger.info("" + order);

        if (vehicle.isFull()) { // capacity constraint
            // route planning at preliminary stage should not violate this constraint
            // assert preliminary == false;
            // this.logger.info("Violate capacity constaint: " + vehicle);
            return new Plan();
        }

        // 遍历每个插入位置，取对距离增量最小的
        ArrayList<Mark> route = vehicle.route;
        double bestDist = Double.POSITIVE_INFINITY;
        Plan bestPlan = new Plan();
        for (int i = 1; i < route.size(); i++) {
            Location prev = route.get(i-1).location;
            Location next = route.get(i).location;
            double accum = route.get(i-1).accum;
            double dx = this.network.compute(prev, order.pickup);
            double dy = this.network.compute(order.pickup, next);
            double delta1 = dx + dy - this.network.compute(prev, next);
            // if (stage == STAGE_WITHIN) {
            //     this.logger.info(prev + ", " + order.pickup + " -- " + dx);
            //     this.logger.info(order.pickup + ", " + next + " -- " + dy);
            // }

            double pickupDist = accum + dx;
            if ((stage == STAGE_WITHIN) && pickupDist > this.config.maxDist) { // pickup constraint
                // this.logger.info("Violate pickup constaint (1): " + accum + "+" + dx + ">" + this.config.maxDist);
                continue;
            }
            if ((stage == STAGE_BEYOND) && (pickupDist > order.tol || pickupDist <= this.config.maxDist)) { // pickup constraint
                // this.logger.info("Violate pickup constaint (2): " + accum + "+" + dx + ">" + this.config.maxDist);
                continue;
            }
            if ((stage == STAGE_FINAL) && pickupDist > order.tol) { // pickup constraint
                // this.logger.info("Violate pickup constaint (3): " + accum + "+" + dx + ">" + order.tol);
                continue;
            }
            if ((stage == STAGE_FINAL) && pickupDist > this.config.maxDist
                && pickupDist <= order.tol && gamma < order.exp) { // discount constraint
                // this.logger.info("Violate pickup constaint (4): " + accum + "+" + dx + ">" + this.config.maxDist);
                continue;
            }

            for (int j = i; j < route.size(); j++) {
                prev = (i == j) ? order.pickup : route.get(j-1).location;
                next = route.get(j).location;
                double dz = this.network.compute(prev, order.dropoff);
                double dw = this.network.compute(order.dropoff, next);
                double delta2 = dz + dw - this.network.compute(prev, next);
                // if (stage == STAGE_WITHIN) {
                //     this.logger.info(prev + ", " + order.dropoff + " -- " + dx);
                //     this.logger.info(order.dropoff + ", " + next + " -- " + dy);
                // }

                double direct = this.network.compute(order.pickup, order.dropoff);
                double detour = (i == j) ? 0 : route.get(j-1).accum - route.get(i).accum;
                if (dy + detour + dz > this.config.maxDetour * direct) { // detour constraint
                    // route planning at preliminary stage should not violate this constraint
                    // assert preliminary == false;
                    // this.logger.info("Violate detour constaint: " +
                    //                  dy + "+" + detour + "+" + dz + ">" + this.config.maxDetour + "*" + direct);
                    continue;
                }

                if (delta1 + delta2 < bestDist) {
                    bestDist = delta1 + delta2;
                    double gain = Double.NEGATIVE_INFINITY, pickupDelta = 0;
                    bestPlan = new Plan(i, j, dx, dy, dz, dw, delta1, delta2, pickupDist, pickupDelta, gain);
                    if (stage == STAGE_WITHIN) {
                        gain = order.rev - this.config.alpha * bestDist;
                    } else if (stage == STAGE_BEYOND) {
                        pickupDelta = vehicle.getDelta(order, bestPlan, stage, this.config.maxDist, 0);
                        // this.logger.info("discount = " + (order.exp * (pickupDist - this.config.maxDist)));
                        // this.logger.info("pickupDelta = " + pickupDelta);
                        gain = order.rev - this.config.alpha * bestDist
                            - order.exp * (pickupDist - this.config.maxDist)
                            - pickupDelta;
                    }
                    // else if (stage == STAGE_FIND) {
                    //     gain = order.rev - this.config.alpha * bestDist
                    //         - order.exp * (pickupDist - this.config.maxDist)
                    //         - vehicle.getDelta(order, bestPlan, stage, this.config.maxDist, 0);
                    // }
                    else if (stage == STAGE_FINAL) {
                        pickupDelta = vehicle.getDelta(order, bestPlan, stage, this.config.maxDist, gamma);
                        gain = order.rev - this.config.alpha * bestDist
                            - gamma * (pickupDist - this.config.maxDist) * (pickupDist > this.config.maxDist ? 1 : 0)
                            - pickupDelta;
                    } else {
                        ;
                    }
                    bestPlan.pickupDelta = pickupDelta;
                    bestPlan.gain = gain;
                }
            }
        }
        // if (stage == STAGE_WITHIN) {
        //     this.logger.info("Best plan: " + bestPlan);
        // }
        return bestPlan;
    }

    // private void printIntermediate(ArrayList<Order> orders, ArrayList<Vehicle> vehicles, Plan[][] matrix) {
    //     for (Order o : orders) {
    //         this.logger.info("" + o);
    //     }
    //     for (Vehicle v : vehicles) {
    //         this.logger.info("" + v);
    //     }
    //     for (int i = 0; i < matrix.length; i++) {
    //         for (int j = 0; j < matrix[0].length; j++) {
    //             this.logger.info("" + matrix[i][j]);
    //         }
    //     }
    // }

    private double computePlatformProfit(ArrayList<Vehicle> vehicles, double gamma) {
        double platform = 0;
        for (Vehicle v : vehicles) {
            if (v.isEmpty()) { continue; }
            double profit = v.getProfit(this.config.alpha, this.config.maxDist, gamma, this.network, this.logger);
            // this.logger.info("Profit: " + profit + ", Vehicle: " + v);
            platform += profit;
        }
        return platform;
    }

    private double greedyDispatch(ArrayList<Order> orders, ArrayList<Vehicle> vehicles, int stage, double gamma, boolean baseline) {
        assert stage == STAGE_WITHIN || stage == STAGE_BEYOND || stage == STAGE_FINAL;
        // this.logger.info("Have: " + orders.size() + " orders, " + vehicles.size() + " vehicles");

        // for (int i = vehicles.size()-1; i >= 0; i--) {
        //     if (vehicles.get(i).isFull()) { vehicles.remove(i); }
        // }

        // 先计算好订单分配给汽车的增益矩阵
        Plan[][] matrix = new Plan[orders.size()][vehicles.size()];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                Order order = orders.get(i);
                Vehicle vehicle = vehicles.get(j);
                Plan plan = this.routePlan(order, vehicle, stage, gamma);
                matrix[i][j] = plan;
            }
        }

        boolean[] orderMask = new boolean[orders.size()];
        Arrays.fill(orderMask, false);
        int[] pair = new int[] {-1, -1};
        double profit = 0;
        // 每次取矩阵里的最大增益加入到派单规划里，直到小于等于零退出
        while ((pair = findMaxGain(matrix)).length == 2 && pair[0] != -1 && pair[1] != -1) {
            int orderIdx = pair[0];
            int vehicleIdx = pair[1];
            Order order = orders.get(orderIdx);
            Vehicle vehicle = vehicles.get(vehicleIdx);
            Plan plan = matrix[orderIdx][vehicleIdx];
            double gain = plan.gain;
            if (gain <= 0) { break; }

            // 将订单分配给汽车
            profit += gain;
            vehicle.assignOrder(order, plan);
            orderMask[orderIdx] = true;
            // if (stage == STAGE_WITHIN) {
            //     this.logger.info("" + plan);
            // }

            // if (stage == STAGE_BEYOND) {
            //     this.logger.info(String.format("%d: gain (%f) = %f - %f * (%f + %f) - %f * (%f - %f) - %f",
            //                                    order.pickup.index, gain, order.rev,
            //                                    this.config.alpha, plan.delta1, plan.delta2,
            //                                    order.exp, plan.pickupDist, this.config.maxDist, plan.pickupDelta));
            // }
            // if (stage == STAGE_FINAL) {
            //     // this.logger.info("Max gain in matrix: (" + pair[0] + ", " + pair[1] + "): " + gain + ", plan: " + matrix[orderIdx][vehicleIdx]);
            //     this.logger.info(String.format("%d: gain (%f) = %f - %.1f * (%f + %f) - %f * (%f - %f) * %d - %f (dx: %f)",
            //                                    order.pickup.index, gain, order.rev,
            //                                    this.config.alpha, plan.delta1, plan.delta2,
            //                                    gamma, plan.pickupDist, this.config.maxDist, (plan.pickupDist > this.config.maxDist ? 1 : 0),
            //                                    plan.pickupDelta, plan.dx));
            //     // this.logger.info("" + vehicle.route);
            // }

            // 更新汽车对应那一列
            for (int i = 0; i < orders.size(); i++) {
                if (orderMask[i]) { continue; } // 删除的订单不用更新
                matrix[i][vehicleIdx] = this.routePlan(orders.get(i), vehicle, stage, gamma);
            }
            // 删除订单对应那一列
            for (int j = 0; j < vehicles.size(); j++) {
                matrix[orderIdx][j] = new Plan();
            }

        }

        // 验证利润是否算对
        double check = this.computePlatformProfit(vehicles, (stage == STAGE_BEYOND) ? -1 : gamma);
        int numAssignedOrders = 0, numAssignedVehicles = 0;
        for (boolean b : orderMask) { if (b) { numAssignedOrders += 1; } }
        for (Vehicle v : vehicles) { if (!v.isEmpty()) { numAssignedVehicles += 1; } }
        // this.logger.info("Profit: " + profit + " (" + check + ")"
        //                  + ", Assigned: " + numAssignedOrders + " orders & " + numAssignedVehicles + " vehicles");
        if (Math.abs(check - profit) >= 1e-8) {
            this.logger.warning("Profit: " + profit + " (" + check + ")"
                             + ", Assigned: " + numAssignedOrders + " orders & " + numAssignedVehicles + " vehicles"
                             + " (stage: " + stage + ", batch: " + this.batch + ")");
        }
        // assert Math.abs(check - profit) < 1e-8 : "Profit: " + profit + " (" + check + ")"
        //     + ", Assigned: " + numAssignedOrders + " orders & " + numAssignedVehicles + " vehicles"
        //     + " (stage: " + stage + ", batch: " + this.batch + ")";

        if (baseline) {
            for (int i = 0; i < orders.size(); i++) {
                if (orderMask[i]) orders.get(i).assigned = true;
            }
            for (int i = 0; i < vehicles.size(); i++) {
                if (!vehicles.get(i).isEmpty()) vehicles.get(i).assigned = true;
            }
            return profit;
        }

        if (stage == STAGE_WITHIN) {
            // 因为第二阶段的订单集和汽车集需要去除第一阶段已经分配过的和满载的
            for (int i = orders.size()-1; i >= 0; i--) {
                if (orderMask[i]) { orders.remove(i); } // 移除已经分配的订单
            }
            for (int i = vehicles.size()-1; i >= 0; i--) {
                if (vehicles.get(i).isFull()) { vehicles.remove(i); } // 移除满载的汽车
            }
            for (Vehicle v : vehicles) { v.clear(); } // 将汽车的规划清空
        } else if (stage == STAGE_FINAL) {
            // 最后派单阶段中把订单和汽车正式标记为已分配
            for (int i = 0; i < orders.size(); i++) {
                if (orderMask[i]) orders.get(i).assigned = true;
            }
            for (int i = 0; i < vehicles.size(); i++) {
                if (!vehicles.get(i).isEmpty()) vehicles.get(i).assigned = true;
            }
        }

        return profit;
    }

    private double determineDiscount(ArrayList<Order> orders, ArrayList<Vehicle> vehicles) {
        // this.logger.info("Have: " + orders.size() + " orders, " + vehicles.size() + " vehicles");

        ArrayList<Bound> bounds = new ArrayList<Bound>();
        // 对每辆汽车求它的所有有效折扣和无效折扣，加入列表
        for (Vehicle v : vehicles) {
            ArrayList<Bound> bs = v.getBounds(this.config, this.network);
            for (Bound b : bs) { bounds.add(b); }
        }

        // 按照折扣从小到大排序
        Collections.sort(bounds);

        // 取最大折扣
        ArrayList<Double> profits = new ArrayList<Double>();
        double bestProfit = Double.NEGATIVE_INFINITY;
        int bestIndex = -1;
        double a = 0, b = 0, c = 0;
        for (int i = 0 ; i < bounds.size(); i++) {
            Bound bound = bounds.get(i);
            a += bound.da; b += bound.db; c += bound.dc;
            double profit = a - this.config.alpha * b - bound.gamma * c;
            profits.add(profit);
            if (profit > bestProfit) { bestProfit = profit; bestIndex = i; }
        }
        double gamma = (bestIndex == -1) ? 0 : bounds.get(bestIndex).gamma;
        double profit = (bestIndex == -1) ? 0 : profits.get(bestIndex);

        // this.logger.info("bounds: " + bounds);
        // this.logger.info("profits: " + profits);
        // this.logger.info("gamma: " + gamma);
        // this.logger.info("profit: " + profit);

        return gamma;
    }

    private Result simulate(ArrayList<Order> orders, ArrayList<Vehicle> vehicles, boolean baseline, boolean determined) {
        long start = System.currentTimeMillis();
        int numOrders = orders.size(), numVehicles = vehicles.size();

        // 保留原始订单，用于最后的派单
        ArrayList<Order> origOrders = new ArrayList<>();
        ArrayList<Vehicle> origVehicles = new ArrayList<>();
        for (Order o : orders) { origOrders.add(o); }
        for (Vehicle v : vehicles) { origVehicles.add(v); }

        if (baseline && !determined) {
            double origProfit = this.greedyDispatch(orders, vehicles, STAGE_WITHIN, 0, baseline);
            long end = System.currentTimeMillis();
            return new Result(numOrders, numVehicles, 0, origProfit, 0, 0, end-start);
        } else if (baseline && determined) {
            double gamma = this.config.meanExp;
            // for (Vehicle v : origVehicles) { v.clear(); } // 清除由于上面过程分配给汽车的订单
            double discountProfit = this.greedyDispatch(origOrders, origVehicles, STAGE_FINAL, gamma, baseline);
            long end = System.currentTimeMillis();
            return new Result(numOrders, numVehicles, gamma, 0, 0, discountProfit, end-start);
        }

        // 第一步：先派接驾距离小于等于全局最大距离
        // this.logger.info("------------------------------------------------------------");
        // this.logger.info("Stage 1");
        double origProfit = this.greedyDispatch(orders, vehicles, STAGE_WITHIN, 0, baseline);

        // 第二步：再派接驾距离大于全局最大距离
        // this.logger.info("------------------------------------------------------------");
        // this.logger.info("Stage 2");
        double addProfit = this.greedyDispatch(orders, vehicles, STAGE_BEYOND, 0, baseline);

        // 第三步：确定折扣因子
        // this.logger.info("------------------------------------------------------------");
        // this.logger.info("Stage 3");
        double gamma =  this.determineDiscount(orders, vehicles);

        // 第四步：最终派单
        // this.logger.info("------------------------------------------------------------");
        // this.logger.info("Stage 4");
        for (Vehicle v : origVehicles) { v.clear(); } // 清除由于上面过程分配给汽车的订单
        double discountProfit = this.greedyDispatch(origOrders, origVehicles, STAGE_FINAL, gamma, baseline);

        long end = System.currentTimeMillis();
        return new Result(numOrders, numVehicles, gamma, origProfit, addProfit, discountProfit, end-start);


        /*
        long start = System.currentTimeMillis();
        int numOrders = orders.size(), numVehicles = vehicles.size();

        // 保留原始订单，用于最后的派单
        ArrayList<Order> origOrders = new ArrayList<>();
        ArrayList<Vehicle> origVehicles = new ArrayList<>();
        for (Order o : orders) { origOrders.add(o); }
        for (Vehicle v : vehicles) { origVehicles.add(v); }

        // 第一步：先派接驾距离小于等于全局最大距离
        // this.logger.info("------------------------------------------------------------");
        // this.logger.info("Stage 1");
        double origProfit = this.greedyDispatch(orders, vehicles, STAGE_WITHIN, 0, baseline);
        if (baseline && !determined) {
            long end = System.currentTimeMillis();
            return new Result(numOrders, numVehicles, 0, origProfit, 0, 0, end-start);
        }

        // 第二步：再派接驾距离大于全局最大距离
        // this.logger.info("------------------------------------------------------------");
        // this.logger.info("Stage 2");
        double addProfit;
        if (determined) {
            addProfit = 0.0;
        } else {
            addProfit = this.greedyDispatch(orders, vehicles, STAGE_BEYOND, 0, baseline);
        }

        // 第三步：确定折扣因子
        // this.logger.info("------------------------------------------------------------");
        // this.logger.info("Stage 3");
        double gamma;
        if (determined) {
            gamma = this.config.meanExp;
        } else {
            gamma = this.determineDiscount(orders, vehicles);
        }

        // 第四步：最终派单
        // this.logger.info("------------------------------------------------------------");
        // this.logger.info("Stage 4");
        for (Vehicle v : origVehicles) { v.clear(); } // 清除由于上面过程分配给汽车的订单
        double discountProfit = this.greedyDispatch(origOrders, origVehicles, STAGE_FINAL, gamma, baseline);

        long end = System.currentTimeMillis();
        return new Result(numOrders, numVehicles, gamma, origProfit, addProfit, discountProfit, end-start);
        */
    }

    private void batchSimulate(boolean baseline, boolean determined) {
        // 订单集和汽车集分批次
        long start = System.currentTimeMillis();
        int range = this.config.range;
        Date orderStart = stringToDate(this.config.startDatetime, "yyyy-MM-dd HH:mm:ss");
        Date vehicleStart = stringToDate(this.config.startDatetime, "yyyy-MM-dd HH:mm:ss");
        ArrayList<Timestamped> os = new ArrayList<Timestamped>();
        ArrayList<Timestamped> vs = new ArrayList<Timestamped>();
        for (Order o : this.orders) { os.add((Timestamped) o); }
        for (Vehicle v : this.vehicles) { vs.add((Timestamped) v); }
        ArrayList<Integer> osp = getSplit(orderStart, range, os);
        ArrayList<Integer> vsp = getSplit(vehicleStart, range, vs);

        this.logger.info("Order split: " + osp);
        this.logger.info("Vehicle split: " + vsp);
        // this.logger.info("\n\n\n");
        for (Order o : this.orders) { o.assigned = false; }
        for (Vehicle v : this.vehicles) { v.assigned = false; v.clear(); }

        ArrayList<Result> results = new ArrayList<>();
        for (int i = 0; i < osp.size(); i++) {
            int a, b, c, d;
            // int a = osp.get(i), b = osp.get(i-1), c = vsp.get(i), d = vsp.get(i-1);
            if (osp.size() >= 2) {
                a = (i == 0) ? osp.get(0) : (i == osp.size()-1) ? osp.get(osp.size() - 2) : osp.get(i - 1);
                b = (i == 0) ? osp.get(1) : (i == osp.size()-1) ? this.orders.size() : osp.get(i + 1);
                c = (i == 0) ? vsp.get(0) : (i == osp.size()-1) ? vsp.get(vsp.size() - 2) : vsp.get(i - 1);
                d = (i == 0) ? vsp.get(1) : (i == osp.size()-1) ? this.vehicles.size() : vsp.get(i + 1);
                // this.logger.info("================================================================================");
                // this.logger.info("Batch #" + i + ": (" + a + ", " + b + "), (" + c + ", " + d + ")");
            } else {
                a = 0;
                b = this.orders.size();
                c = 0;
                d = this.vehicles.size();
            }
            this.batch = i;

            // 排除已经分配过的订单和汽车
            ArrayList<Order> orders = new ArrayList<Order>();
            ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
            for (int j = a; j < b; j++) { if (!this.orders.get(j).assigned) orders.add(this.orders.get(j)); }
            for (int j = c; j < d; j++) { if (!this.vehicles.get(j).assigned) vehicles.add(this.vehicles.get(j)); }

            // 开始派单
            // for (Order o : orders) { this.logger.info("" + o); }
            // for (Vehicle v : vehicles) { this.logger.info("" + v); }
            Result result = this.simulate(orders, vehicles, baseline, determined);
            results.add(result);

            // this.logger.info("\n\n\n");
        }
        long end = System.currentTimeMillis();

        this.logger.info("Running time: " + (end-start) + " ms");
        try {
            String resultFile = new String(this.config.resultFile);
            if (baseline && !determined) {
                int p = resultFile.lastIndexOf('.');
                resultFile = resultFile.substring(0, p) + "_baseline" + resultFile.substring(p);
            } else if (baseline && determined) {
                int p = resultFile.lastIndexOf('.');
                resultFile = resultFile.substring(0, p) + "_baseline_exp" + resultFile.substring(p);
            }
            this.logger.info("Write to file " + resultFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile));
            writer.write("batch,numOrders,numVehicles,gamma,origProfit,addProfit,discountProfit,time\n");
            int sumOrders = 0, sumVehicles = 0; double sumOrigProfit = 0, sumDiscountProfit = 0;
            for (int i = 0; i < results.size(); i++) {
                Result result = results.get(i);
                writer.write(String.format("%d,%d,%d,%f,%f,%f,%f,%d\n", i, result.numOrders, result.numVehicles,
                                           result.gamma, result.origProfit, result.addProfit, result.discountProfit,
                                           result.time));
                sumOrders += result.numOrders;
                sumVehicles += result.numVehicles;
                sumOrigProfit += result.origProfit;
                sumDiscountProfit += result.discountProfit;
            }
            writer.write(String.format(",%d,%d,%f,%f,%f,%f,%d\n", sumOrders, sumVehicles,
                                       0.0, sumOrigProfit, 0.0, sumDiscountProfit,
                                       end-start));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read() {
        String orderFile = this.config.orderFile, vehicleFile = this.config.vehicleFile;
        String line = "", delim = ",", sdf = "yyyy-MM-dd HH:mm:ss";

        this.logger.info("Reading orders...");
        // format is [datetime, trip time (secs), trip distance,
        //            pickup longitude, pickup latitude,
        //            dropoff longitude, dropoff latitude, total amount]
        try {
            int count = 0;
            BufferedReader reader = new BufferedReader(new FileReader(orderFile));
            while ((line = reader.readLine()) != null) {
                if (count >= this.config.numSelect) { break; }
                String[] record = line.split(delim);

                // Check the record
                boolean skip = false;
                for (int i = 1; i < record.length; i++) {
                    if (Double.parseDouble(record[i]) == 0.0) { skip = true; break; }
                }
                if (skip) { continue; }

                // Create the object
                Date datetime = stringToDate(record[0], sdf);
                double pickupLon = Double.valueOf(record[3]), pickupLat = Double.valueOf(record[4]);
                double dropoffLon = Double.valueOf(record[5]), dropoffLat = Double.valueOf(record[6]);
                // int pickupNode = this.network.match(pickupLon, pickupLat);
                // int dropoffNode = this.network.match(dropoffLon, dropoffLat);
                int pickupNode = -1, dropoffNode = -1;
                double rev = Double.valueOf(record[7]);
                double tol = getNextExponential(this.config.meanTol); // tolerant distance
                tol = (tol < this.config.maxDist) ? this.config.maxDist : tol;
                double exp = getNextExponential(this.config.meanExp); // expected discount factor
                Location pickup = new Location(pickupLon, pickupLat, pickupNode, this.orders.size()*2);
                Location dropoff = new Location(dropoffLon, dropoffLat, dropoffNode, this.orders.size()*2+1);
                Order order = new Order(datetime, pickup, dropoff, rev, tol, exp);
                this.orders.add(order);
                assert order.tol >= this.config.maxDist;

                // this.logger.info("" + order);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        this.logger.info("Reading vehicles...");
        // format is [datetime, lon, lat]
        try {
            int count = 0;
            BufferedReader reader = new BufferedReader(new FileReader(vehicleFile));
            while ((line = reader.readLine()) != null) {
                if (count >= this.config.numSelect) { break; }
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
                // int node = this.network.match(lon, lat);
                int node = -1;
                Location location = new Location(lon, lat, node, this.orders.size()+this.vehicles.size());
                Vehicle vehicle = new Vehicle(datetime, location, this.config.capacity);
                this.vehicles.add(vehicle);

                // this.logger.info("" + vehicle);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Downsample
        // for (int i = this.vehicles.size(); i >= 0; i--) {
        //     if (i % 5 >= this.config.voratio) { this.vehicles.remove(i); }
        // }

        this.logger.info("Read " + this.orders.size() + " orders and " + this.vehicles.size() + " vehicles");
    }

    private void loadRoadNetwork() {
        this.logger.info("Building/Loading road network...");
        this.network = new RoadNetwork();
        // this.network.load(this.config.matrixFile);
        String line = "", delim = ",";
        int count = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.config.matrixFile));
            boolean first = true;
            while ((line = reader.readLine()) != null) {
                if (first) {
                    int size = this.config.numSelect * 3;
                    this.network.matrix = new double[size][size];
                    first = false;
                    continue;
                }
                if (count < this.config.numSelect * 2 ||
                    (count >= this.config.numFull * 2 &&
                     count < this.config.numFull * 2 + this.config.numSelect)) {
                    String[] record = line.split(delim);
                    for (int i = 0, l = 0; i < record.length; i++) {
                        if (i < this.config.numSelect * 2 ||
                            (i >= this.config.numFull * 2 &&
                             i < this.config.numFull * 2 + this.config.numSelect)) {
                            this.network.matrix[count][l] = Double.valueOf(record[i]);
                            l++;
                        }
                    }
                    count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.logger.info("Matrix size: " + this.network.matrix.length);
    }

    public static Config readConfig(String configFile) {
        Config config = new Config();
        try {
            String line = "";
            int count = 0;
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            while ((line = reader.readLine()) != null) {
                int comment = line.indexOf('#');
                if (comment == -1) {
                    line = line.trim();
                } else {
                    line = line.substring(0, comment).trim();
                }

                switch (count) {
                case 0:
                    config.orderFile = line; break;
                case 1:
                    config.vehicleFile = line; break;
                case 2:
                    config.matrixFile = line; break;
                case 3:
                    config.startDatetime = line; break;
                case 4:
                    config.capacity = Integer.valueOf(line); break;
                case 5:
                    config.range = Integer.valueOf(line); break;
                case 6:
                    config.voratio = Integer.valueOf(line); break;
                case 7:
                    config.maxDist = Double.valueOf(line); break;
                case 8:
                    config.maxDetour = Double.valueOf(line); break;
                case 9:
                    config.alpha = Double.valueOf(line); break;
                case 10:
                    config.meanTol = Double.valueOf(line); break;
                case 11:
                    config.meanExp = Double.valueOf(line); break;
                case 12:
                    config.resultFile = line; break;
                case 13:
                    config.numSelect = Integer.valueOf(line); break;
                case 14:
                    config.numFull = Integer.valueOf(line); break;
                default:
                    break;
                }

                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return config;
    }

    public static void main(String[] args) {
        String configFile = args[0];
        Config config = readConfig(configFile);

        DBODRS_baseline algo = new DBODRS_baseline(config);
        algo.read();
        algo.loadRoadNetwork();

        algo.batchSimulate(true, false);
        //algo.batchSimulate(true, true);
        //algo.batchSimulate(false, false);
    }
}
