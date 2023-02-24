javac -d . CacheDistMatrixRS.java ContractionHierarchies.java RoadNetwork.java DBODRS.java
javac -d . DBODRSDET.java
javac -d . DBODRSDET_exp.java
javac -d . DBODRSDET_baseline.java
 

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_batch_5.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_batch_5.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_batch_5.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_batch_10.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_batch_10.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_batch_10.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_batch_20.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_batch_20.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_batch_20.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_batch_25.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_batch_25.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_batch_25.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_voratio_1.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_voratio_1.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_voratio_1.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_voratio_3.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_voratio_3.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_voratio_3.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_voratio_4.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_voratio_4.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_voratio_4.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_voratio_5.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_voratio_5.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_voratio_5.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_dro_1.2.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_dro_1.2.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_dro_1.2.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_dro_1.5.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_dro_1.5.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_dro_1.5.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_dro_2.1.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_dro_2.1.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_dro_2.1.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_dro_2.4.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_dro_2.4.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_dro_2.4.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_edc_0.4.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_edc_0.4.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_edc_0.4.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_edc_1.2.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_edc_1.2.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_edc_1.2.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_edc_1.6.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_edc_1.6.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_edc_1.6.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_edc_2.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_edc_2.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_edc_2.0.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_edt_2.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_edt_2.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_edt_2.0.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_edt_4.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_edt_4.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_edt_4.0.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_edt_5.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_edt_5.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_edt_5.0.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_edt_6.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_edt_6.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_edt_6.0.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_pd0_0.4.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_pd0_0.4.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_pd0_0.4.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_pd0_1.2.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_pd0_1.2.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_pd0_1.2.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_pd0_1.6.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_pd0_1.6.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_pd0_1.6.txt

 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_baseline ./data/rs_data_config_pd0_2.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET_exp ./data/rs_data_config_pd0_2.0.txt
 java -Xms40g -Xmx200g -ea crowdsourcing.DBODRSDET ./data/rs_data_config_pd0_2.0.txt










 


 
