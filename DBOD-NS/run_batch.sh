javac -d . *.java
javac -d . Nondiscount_batch.java 
javac -d . Sdiscount_batch.java 
javac -d . Odiscount_batch.java 
javac -d . Ediscount_batch.java 

    
#                                           given_dis edc edt n_samples batch_minutes 
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.4 0.8 3.0 2 15 Nondiscount_givendis_0.4.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 3.0 2 15 Nondiscount_givendis_0.8.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 1.2 0.8 3.0 2 15 Nondiscount_givendis_1.2.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 1.6 0.8 3.0 2 15 Nondiscount_givendis_1.6.csv   
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 2.0 0.8 3.0 2 15 Nondiscount_givendis_2.0.csv   


 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 3.0 1 15 Nondiscount_odr_1.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 3.0 3 15 Nondiscount_odr_3.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 3.0 4 15 Nondiscount_odr_4.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 3.0 5 15 Nondiscount_odr_5.csv

 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.4 3.0 2 15 Nondiscount_edc_0.4.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 1.2 3.0 2 15 Nondiscount_edc_1.2.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 1.6 3.0 2 15 Nondiscount_edc_1.6.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 2.0 3.0 2 15 Nondiscount_edc_2.0.csv

 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 2.0 2 15 Nondiscount_edt_2.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 4.0 2 15 Nondiscount_edt_4.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 5.0 2 15 Nondiscount_edt_5.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 6.0 2 15 Nondiscount_edt_6.0.csv

 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 3.0 2 5  Nondiscount_batch_5.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 3.0 2 10 Nondiscount_batch_10.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 3.0 2 20 Nondiscount_batch_20.csv
 java -Xms40g -Xmx100g crowdsourcing.Nondiscount_batch 0.8 0.8 3.0 2 25 Nondiscount_batch_25.csv


 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.4 0.8 3.0 2 15 Sdiscount_givendis_0.4.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 3.0 2 15 Sdiscount_givendis_0.8.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 1.2 0.8 3.0 2 15 Sdiscount_givendis_1.2.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 1.6 0.8 3.0 2 15 Sdiscount_givendis_1.6.csv   
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 2.0 0.8 3.0 2 15 Sdiscount_givendis_2.0.csv   


 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 3.0 1 15 Sdiscount_odr_1.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 3.0 3 15 Sdiscount_odr_3.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 3.0 4 15 Sdiscount_odr_4.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 3.0 5 15 Sdiscount_odr_5.csv

 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.4 3.0 2 15 Sdiscount_edc_0.4.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 1.2 3.0 2 15 Sdiscount_edc_1.2.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 1.6 3.0 2 15 Sdiscount_edc_1.6.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 2.0 3.0 2 15 Sdiscount_edc_2.0.csv

 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 2.0 2 15 Sdiscount_edt_2.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 4.0 2 15 Sdiscount_edt_4.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 5.0 2 15 Sdiscount_edt_5.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 6.0 2 15 Sdiscount_edt_6.0.csv

 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 3.0 2 5  Sdiscount_batch_5.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 3.0 2 10 Sdiscount_batch_10.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 3.0 2 20 Sdiscount_batch_20.csv
 java -Xms40g -Xmx100g crowdsourcing.Sdiscount_batch 0.8 0.8 3.0 2 25 Sdiscount_batch_25.csv


 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.4 0.8 3.0 2 15 Odiscount_givendis_0.4.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 3.0 2 15 Odiscount_givendis_0.8.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 1.2 0.8 3.0 2 15 Odiscount_givendis_1.2.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 1.6 0.8 3.0 2 15 Odiscount_givendis_1.6.csv   
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 2.0 0.8 3.0 2 15 Odiscount_givendis_2.0.csv   

 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 3.0 1 15 Odiscount_odr_1.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 3.0 3 15 Odiscount_odr_3.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 3.0 4 15 Odiscount_odr_4.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 3.0 5 15 Odiscount_odr_5.csv

 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.4 3.0 2 15 Odiscount_edc_0.4.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 1.2 3.0 2 15 Odiscount_edc_1.2.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 1.6 3.0 2 15 Odiscount_edc_1.6.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 2.0 3.0 2 15 Odiscount_edc_2.0.csv

 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 2.0 2 15 Odiscount_edt_2.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 4.0 2 15 Odiscount_edt_4.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 5.0 2 15 Odiscount_edt_5.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 6.0 2 15 Odiscount_edt_6.0.csv

 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 3.0 2 5  Odiscount_batch_5.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 3.0 2 10 Odiscount_batch_10.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 3.0 2 20 Odiscount_batch_20.csv
 java -Xms40g -Xmx100g crowdsourcing.Odiscount_batch 0.8 0.8 3.0 2 25 Odiscount_batch_25.csv



 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.4 0.8 3.0 2 15 Ediscount_givendis_0.4.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 3.0 2 15 Ediscount_givendis_0.8.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 1.2 0.8 3.0 2 15 Ediscount_givendis_1.2.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 1.6 0.8 3.0 2 15 Ediscount_givendis_1.6.csv   
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 2.0 0.8 3.0 2 15 Ediscount_givendis_2.0.csv   


 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 3.0 1 15 Ediscount_odr_1.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 3.0 3 15 Ediscount_odr_3.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 3.0 4 15 Ediscount_odr_4.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 3.0 5 15 Ediscount_odr_5.csv

 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.4 3.0 2 15 Ediscount_edc_0.4.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 1.2 3.0 2 15 Ediscount_edc_1.2.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 1.6 3.0 2 15 Ediscount_edc_1.6.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 2.0 3.0 2 15 Ediscount_edc_2.0.csv

 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 2.0 2 15 Ediscount_edt_2.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 4.0 2 15 Ediscount_edt_4.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 5.0 2 15 Ediscount_edt_5.0.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 6.0 2 15 Ediscount_edt_6.0.csv

 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 3.0 2 5  Ediscount_batch_5.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 3.0 2 10 Ediscount_batch_10.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 3.0 2 20 Ediscount_batch_20.csv
 java -Xms40g -Xmx100g crowdsourcing.Ediscount_batch 0.8 0.8 3.0 2 25 Ediscount_batch_25.csv






