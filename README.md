#  Longer Pick-up for Less Pay: Towards Discount-based Mobility Services

## DBOD-NS

### Effectiveness & Efficiency

For effectiveness and efficiency, we first select and preprocess the order records between 18:00 to 19:00 on January 13, 2013 from the NYC dataset (named `trip_data_1_sorted1.csv`) and generate the vehicle set:
```
python3 spawn_ns_eff.py
```

Before running, we need to generate the distance matrix as follows:
```
javac -d . CacheDistMatrix.java RoadNetwork.java
java crowdsourcing.DemoRoadNetwork
java crowdsourcing.CacheDistMatrix "1.13 18pm_order_fix.csv" "1.13 18pm_vehicle_fix.csv"
cat $(ls -v data/out_*) > 18pm_merge.csv
```

To reproduce the results:
```
bash run_batch.sh
```

### Scalability

For scalability, we choose and preprocess 50k order records starting at 14:00 on January 13, 2013 from the NYC dataset (named `trip_data_1_sorted1.csv`), and generate the vehicle set:
```
python3 spawn_ns_scala.py
```

Then we need to generate the distance matrix as follows:
```
java crowdsourcing.CacheDistMatrix "1.13 5w_order.csv" "1.13 5w_vehicle.csv"
cat $(ls -v data/out_*) > "1.13 5w_merge.csv"
```

To reproduce the results:
```
bash run_sca.sh
```


## DBOD-RS

### Effectiveness & Efficiency

For effectiveness and efficiency, we select and preprocess order records between 11:00 to 12:00 on January 13, 2013 (named `1.13_11am_order.csv`) from the NYC dataset, and generate the vehicle set:
```
python3 spawn_rs.py --inpath data/1.13_11am_order.csv --order-out data/1.13_11am_order_fix.csv --vehicle-out data/1.13_11am_vehicle_fix.csv --voratio 5
```

Before running, we need to generate the distance matrix as follows:
```
javac -d . CacheDistMatrixRS.java RoadNetwork.java
java crowdsourcing.DemoRoadNetwork
java crowdsourcing.CacheDistMatrixRS ./data/1.13_11am_order_fix.csv ./data/1.13_11am_vehicle_fix.csv
cat $(ls -v output/out_*) > ./data/rs_1.13_11am.csv
wc -l ./data/rs_1.13_11am.csv | cut -d' ' -f1 | xargs -I {} sed -i '1 s/^/{}\n/' ./data/rs_1.13_11am.csv
```

To reproduce the results:
```
bash run_batch_RS.sh
```


### Scalability

For scalability, similar to the above, we choose and preprocess 20k records starting at 14:00 on January 13, 2013 (named `rs_scala_20k_orders.csv`), and generate the vehicle set:
```
python3 spawn_rs.py --inpath data/rs_scala_20k_orders.csv --order-out data/rs_scala_20k_orders_fix.csv --vehicle-out data/rs_scala_20k_vehicles_fix.csv --voratio 1
```

Before running, we need to generate the distance matrix as follows:
```
java crowdsourcing.CacheDistMatrixRS ./data/rs_scala_20k_orders_fix.csv ./data/rs_scala_20k_vehicles_fix.csv
cat $(ls -v output/out_*) > ./data/rs_scala_20k_matrix.csv
wc -l ./data/rs_scala_20k_matrix.csv | cut -d' ' -f1 | xargs -I {} sed -i '1 s/^/{}\n/' ./data/rs_scala_20k_matrix.csv
```

To reproduce the results:
```
bash run_sca_RS.sh
```
