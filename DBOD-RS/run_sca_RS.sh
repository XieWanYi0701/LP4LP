javac -d . CacheDistMatrixRS.java ContractionHierarchies.java RoadNetwork.java DBODRS.java
javac -d . DBODRS.java
javac -d . DBODRS_exp.java
javac -d . DBODRS_baseline.java

java -ea crowdsourcing.DBODRS ./data/rs_scala_1000_config.txt
java -ea crowdsourcing.DBODRS_exp ./data/rs_scala_1000_config.txt
java -ea crowdsourcing.DBODRS_baseline ./data/rs_scala_1000_config.txt

java -ea crowdsourcing.DBODRS ./data/rs_scala_5000_config.txt
java -ea crowdsourcing.DBODRS ./data/rs_scala_10000_config.txt
java -ea crowdsourcing.DBODRS ./data/rs_scala_15000_config.txt
java -ea crowdsourcing.DBODRS ./data/rs_scala_20000_config.txt


java -ea crowdsourcing.DBODRS_exp ./data/rs_scala_5000_config.txt
java -ea crowdsourcing.DBODRS_exp ./data/rs_scala_10000_config.txt
java -ea crowdsourcing.DBODRS_exp ./data/rs_scala_15000_config.txt
java -ea crowdsourcing.DBODRS_exp ./data/rs_scala_20000_config.txt


java -ea crowdsourcing.DBODRS_baseline ./data/rs_scala_5000_config.txt
java -ea crowdsourcing.DBODRS_baseline ./data/rs_scala_10000_config.txt
java -ea crowdsourcing.DBODRS_baseline ./data/rs_scala_15000_config.txt
java -ea crowdsourcing.DBODRS_baseline ./data/rs_scala_20000_config.txt






