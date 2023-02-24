package crowdsourcing;

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
import java.lang.Math;
import java.util.ArrayList;
import java.util.logging.Logger;

//import crowdsourcing.RoadNetwork;

public class Odiscount_batch {
    static Logger logger = Logger.getLogger("main");
    static Random rand;
    static int COUNT;


    public static void main(String[] args) {
        COUNT = 0;
        Double given_dis = Double.parseDouble(args[0]);
        Double edc = Double.parseDouble(args[1]);
        Double edt = Double.parseDouble(args[2]);
        int n_samples = Integer.parseInt(args[3]);
        int batch_minutes = Integer.parseInt(args[4]);
        String outfile = args[5];
        rand = new Random(42);
        String line = "";
        String delim = ",";
        int count = 0;
        int cnt =0;

        //read data
        logger.info("Reading data");
        ArrayList<String[]> listDistance = new ArrayList<String[]>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("18pm_merge.csv"));
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(delim);
                listDistance.add(record);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] sample = listDistance.get(0);
        double[][] matrix_odistance= new double [listDistance.size()][sample.length*n_samples/5];
        for (String[] record : listDistance) {
            int ncols = 0;
            for (int j = 0; j < record.length; j++) {
                if ((j+1) % 5 <= (n_samples-1)) {
                    matrix_odistance[cnt][ncols] = Double.valueOf(record[j].toString());
                    ncols++;
                }
            }
            cnt++;
        }
        System.out.println( (sample.length*n_samples/5)+ " lines");

        long start_time, end_time;
        start_time = System.currentTimeMillis();

        ArrayList<String[]> list = new ArrayList<String[]>();
        count = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("1.13 18pm_order_fix.csv"));
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(delim);
                boolean skip = false;
                for (int i = 1; i <= 5; i++) {
                    if (Double.parseDouble(record[i]) == 0.0) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
                list.add(record);
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(count + " lines");

        ArrayList<String[]> list1 = new ArrayList<String[]>();
        count = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("1.13 18pm_vehicle_fix.csv"));
            while ((line = reader.readLine()) != null) {
                String[] record = line.split(delim);
                boolean skip = false;
                for (int i = 1; i <= 2; i++) {
                    if (Double.parseDouble(record[i]) == 0.0) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                    continue;
                }
                if ((count+1) % 5 <= (n_samples-1)) {
                    list1.add(record);
                }

                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(list1.size() + " lines");


        // generate expected discount fatcor and allowed maximum pick-up distance
        cnt = 0;
        double r1,r2;
        int n_divides= 4;
        String sdf = "yyyy-MM-dd HH:mm:ss";// 字符串日期转换
        ArrayList<Object[]> new_list = new ArrayList<Object[]>();

        for (String[] record : list) {
            Object[] new_record = new Object[9];
            new_record[0] = stringToDate(record[0], sdf);
            for (int j = 1; j < record.length; j++) {
                new_record[j] = Double.valueOf(record[j].toString());
            }
            r1 = getNext(edc);
            r2 = getNext(edt);
            new_record[6] = r1;
            if (r2 >= given_dis) {
                new_record[7] = r2;
            } else {
                new_record[7] = given_dis;
            }
            new_record[8] = -1;
            new_list.add(new_record);
        }

        ArrayList<Object[]> new_list1 = new ArrayList<Object[]>();
        for (String[] record : list1) {
            cnt++;
            Object[] new_record = new Object[4];
            new_record[0] = stringToDate(record[0], sdf);
            for (int j = 1; j < record.length; j++) {
                new_record[j] = Double.valueOf(record[j].toString());
            }
            new_record[3] = -1;
            new_list1.add(new_record);
        }

        // batch process
        long l = Date.parse(" 13 Jan 2013 18:00");
        Date start = new Date(l);
        ArrayList<Integer> split = new ArrayList<Integer>();
        split = getSplit(start,batch_minutes,new_list);
        System.out.println(split);

        long l_1 = Date.parse(" 13 Jan 2013 18:00");
        Date start1 = new Date(l_1);
        ArrayList<Integer> split1 = new ArrayList<Integer>();
        split1 = getSplit(start1,batch_minutes,new_list1);
        System.out.println(split1);

        //Computing profit 
        logger.info("Computing profit");
        double[]ebmaxprofit = new double[split.size()];
        double max_profit =0.0;
        HashMap<Integer, Integer> map_orders;
        HashMap<Integer, Integer> map_vehicles;
        double matrix_distance[][];
        Object[] ret;
        ArrayList<Double> discount_arr;
        double[] discount_arr0;
        for (int i = 0; i < split.size(); i++) {
            logger.info("Batch #" + i);
            int a = (i == 0) ? split.get(0) : (i == split.size()-1) ? split.get(split.size() - 2) : split.get(i - 1);
            int b = (i == 0) ? split.get(1) : (i == split.size()-1) ? new_list.size() : split.get(i + 1);
            int c = (i == 0) ? split1.get(0) : (i == split.size()-1) ? split1.get(split1.size() - 2) : split1.get(i - 1);
            int d = (i == 0) ? split1.get(1) : (i == split.size()-1) ? new_list1.size() : split1.get(i + 1);

            ret = getDistMatrix(a, b, c, d, new_list, new_list1, matrix_odistance);
            map_orders = (HashMap<Integer, Integer>) ret[0];
            map_vehicles = (HashMap<Integer, Integer>) ret[1];
            matrix_distance = (double[][]) ret[2];
            discount_arr =new ArrayList<Double>();

            for(int j = 0; j < map_orders.size(); j++){
                int j_actual = map_orders.get(j);
                Double discount = (Double) new_list.get(j_actual)[6];
                Double tol = (Double) new_list.get(j_actual)[7];
                for (int k = 0; k < matrix_distance[0].length; k++) {
                    double pick_dis = matrix_distance[j][k];
                    if (pick_dis > given_dis && pick_dis <= tol ) {
                        discount_arr.add(discount);
                        break;
                    }
                }
            }
            discount_arr0 =new double[discount_arr.size()];
            for(int n=0;n<discount_arr.size();n++){
                discount_arr0[n]=discount_arr.get(n);
            }
            Arrays.sort(discount_arr0);
            ebmaxprofit[i]=getMaxprofit(matrix_distance, map_orders, map_vehicles, new_list, new_list1, discount_arr0, 0, discount_arr0.length, n_divides, given_dis);
            max_profit+=ebmaxprofit[i];
        }

        logger.info("Writing to file");
        System.out.println("Total profit: "+ max_profit);
        end_time = System.currentTimeMillis();
        Long total_time = (end_time - start_time);

        try {
            FileWriter writer_profit = new FileWriter(outfile);
            writer_profit.write("OPT " +",");
            for(int i =0;i< ebmaxprofit.length;i++){
                writer_profit.write(ebmaxprofit[i] + ","  );
            }
            writer_profit.write("\n");
            writer_profit.write("\n"+"OPT Total profit" +","+ max_profit);
            writer_profit.write("\n"+" OPT Total time,"+total_time/1000.0);
            writer_profit.write("\n"+"Given dis" +","+ given_dis);
            writer_profit.write("\n"+"Expected discount" +","+ edc);
            writer_profit.write("\n"+"Expected distance" +","+ edt);
            writer_profit.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Distance Matrix
    public static Object[] getDistMatrix(int a, int b, int c, int d,ArrayList<Object[]> orders,
                                         ArrayList<Object[]> vehicles,double[][] matrixOridistance ){
        System.out.println("Computing dist matrix...");
        HashMap<Integer, Integer> map_orders = getHashMap(a, b, orders, 8);
        HashMap<Integer, Integer> map_vehicles = getHashMap(c, d, vehicles, 3);
        double[][] matrix_distance = new double[map_orders.size()][map_vehicles.size()];

        long timeA = System.currentTimeMillis();
        for (int p = 0; p < matrix_distance.length; p++) {
            int p_actual = map_orders.get(p);
            for (int n = 0; n < matrix_distance[0].length; n++) {
                int n_actual = map_vehicles.get(n);
                matrix_distance[p][n] = matrixOridistance[p_actual][n_actual];
            }
        }
        long timeB = System.currentTimeMillis();
        System.out.println("Total time: " + (timeB - timeA) + " ms");
        return new Object[] { map_orders, map_vehicles, matrix_distance };
    }

    //process pended orders and vehicles 
    public static HashMap<Integer, Integer> getHashMap(int a, int b, ArrayList<Object[]> sets, int t) {
        HashMap<Integer, Integer> vertex_map = new HashMap<Integer, Integer>();
        int num_nodes = 0;
        int complete = 0;
        for (int k = a; k < b; k++) {
            complete = (int) sets.get(k)[t];
            if (complete != 1) {
                vertex_map.put(num_nodes, k);
                num_nodes++;
            }
        }
        return vertex_map;

    }

    // use HungarianAlgorithm to calculate the profit of each batch
    public static double getProfit(double matrix_distance[][], HashMap<Integer, Integer> map_orders,
                                   HashMap<Integer, Integer> map_vehicles,
                                   ArrayList<Object[]> orders, ArrayList<Object[]> vehicles, double discount,int last,double given_dis) {
        double matrix[][] = new double[matrix_distance.length][matrix_distance[0].length];
        double accum = 0.0;
        Double labor_fare = 1.0;
        //Double b_cost =3.0;

        //System.out.println("Matching...");
        long timeA = System.currentTimeMillis();
        for (int j = 0; j < matrix_distance.length; j++) {
            int j_actual = map_orders.get(j);
            Double ep = (Double) orders.get(j_actual)[6];
            Double tol = (Double) orders.get(j_actual)[7];
            Double fare = (Double) orders.get(j_actual)[5];
            Double total_dis = (Double) orders.get(j_actual)[2];
            int complete = (int) orders.get(j_actual)[8];

            for (int k = 0; k < matrix_distance[0].length; k++) {
                double pick_dis = matrix_distance[j][k];
                if (pick_dis <= given_dis) {
                    Double cost = fare - labor_fare * (total_dis + pick_dis);
                    matrix[j][k] = (cost < 0) ? 0 : cost;
                } else {
                    if (pick_dis > tol) {
                        matrix[j][k] = 0;
                    } else {
                        if (ep > discount) {
                            matrix[j][k] = 0;
                        } else {
                            Double cost = fare - labor_fare * (total_dis + pick_dis)
                                - discount * (pick_dis - given_dis);
                            matrix[j][k] = (cost < 0) ? 0 : cost;
                        }

                    }
                }

                matrix[j][k] = 10000 - matrix[j][k];
            }
        }

        HungarianAlgorithm ha = new HungarianAlgorithm(matrix);
        int[] result = ha.execute();
        for (int p = 0; p < result.length; p++) {
            if (result[p] != -1) {
                if(last ==1){
                    if (matrix[p][result[p]] != 10000) {
                        orders.get(map_orders.get(p))[8] = 1;
                        vehicles.get(map_vehicles.get(result[p]))[3] = 1;
                    }
                }
                accum += (10000 - matrix[p][result[p]]);
            }
        }
        COUNT++;
        System.out.println(COUNT);
        long timeB = System.currentTimeMillis();
        return accum;
    }

    // exponential distribution
    public static double getNext(double lambda) {
        return Math.log(1 - rand.nextDouble()) *(-lambda);
    }

    // Change date format
    public static Date stringToDate(String strDate, String format) {
        Date date = null;
        if (format != null && !"".equals(format)) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                date = sdf.parse(strDate);
            } catch (Exception var5) {
                date = null;
            }
        }
        return date;
    }

    // find the maximum profit
    public static double getMax(double profit[][]) {
        double max0 =0.0;
        ArrayList<Double> list = new ArrayList<Double>();
        for(int i=0; i< profit.length; i++){
            list.add(profit[i][1]);
        }
        max0 = Collections.max(list);
        return max0;
    }

    //batch process
    public static ArrayList<Integer> getSplit(Date start,int bat,ArrayList<Object[]> list){
        ArrayList<Integer> split = new ArrayList<Integer>();
        System.out.println(start);
        for (int i = 0; i < list.size(); i++) {
            Date current = (Date) list.get(i)[0];
            if (current.compareTo(start) == 0) {
                split.add(i);
                break;
            }
        }

        for (int i = 0; i < list.size(); i++) {
            Date finish = new Date(start.getTime() + Long.valueOf(bat) * 1000 );
            Date current = (Date) list.get(i)[0];
            if (current.compareTo(finish) >= 0) {
                split.add(i);
                start = finish;
            }
        }
        return split;
    }
 
    // iteration
    public static double getMaxprofit(double[][] matrix_distance, HashMap<Integer, Integer> map_orders,
                                      HashMap<Integer, Integer> map_vehicles,ArrayList<Object[]> orders,
                                      ArrayList<Object[]> vehicles,double[] profit_array,int left, int right,int n_divides, double given_dis){
        int size = (right - left) / (n_divides+1) ;
        System.out.println(left + ", " + right);
        int c,pt;
        double discount, profit_max;
        ArrayList<Double> profit_list = new ArrayList<Double>();
        if(right == left){
            return getProfit(matrix_distance, map_orders, map_vehicles, orders, vehicles, 0, 1, given_dis);
        }
       
        if(size == 0){
            n_divides = n_divides/2;
            return getMaxprofit(matrix_distance, map_orders, map_vehicles, orders, vehicles, profit_array, left, right, n_divides, given_dis);
        }
        
        if(n_divides <=3){

            int best_index = 0;
            double best_profit = 0.0;
            for(int i=left+size;i< right;i=i+size){
                discount = profit_array[i];
                double profit = getProfit(matrix_distance, map_orders, map_vehicles,orders, vehicles, discount, 0,  given_dis);
                if (profit > best_profit) {
                    best_profit = profit;
                    best_index = i; }
            }
            c = best_index;
            discount = profit_array[c];
            return getProfit(matrix_distance, map_orders, map_vehicles, orders, vehicles, discount, 1, given_dis);
        }

        int best_index = 0;
        double best_profit = 0.0;
        for(int i=left+size;i< right;i=i+size){
            discount = profit_array[i];
            double profit = getProfit(matrix_distance, map_orders, map_vehicles,orders, vehicles, discount, 0,  given_dis);
            //profit_list.add();
            if (profit > best_profit) {
                best_profit = profit;
                best_index = i; }
        }
        pt = best_index;
        left = Math.max(left, pt-size);
        right = Math.min(right, pt+size);
        return getMaxprofit(matrix_distance, map_orders, map_vehicles, orders, vehicles, profit_array, left, right, n_divides/2, given_dis);
    }


}
