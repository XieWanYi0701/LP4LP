javac -d . *.java
javac -d . Nondiscount.java 
javac -d . Odiscount.java
javac -d . Ediscount.java 
javac -d . Sdiscount.java   



#                    given_dis edc edt n_samples batch_minutes



java -Xms40g -Xmx200g crowdsourcing.Nondiscount 0.8 0.8 3.0 1000 15 Nondiscount_sca_1000.csv
java -Xms40g -Xmx200g crowdsourcing.Ediscount 0.8 0.8 3.0 1000 15 Ediscount_sca_1000.csv
java -Xms40g -Xmx100g crowdsourcing.Sdiscount 0.8 0.8 3.0 1000 15 Sdiscount_sca_1000.csv
java -Xms40g -Xmx100g crowdsourcing.Odiscount 0.8 0.8 3.0 1000 15 Odiscount_sca_1000.csv

java -Xms40g -Xmx100g crowdsourcing.Sdiscount 0.8 0.8 3.0 5000 15 Sdiscount_sca_5000.csv
java -Xms40g -Xmx200g crowdsourcing.Ediscount 0.8 0.8 3.0 5000 15 Ediscount_sca_5000.csv
java -Xms40g -Xmx200g crowdsourcing.Nondiscount 0.8 0.8 3.0 5000 15 Nondiscount_sca_5000.csv
java -Xms40g -Xmx100g crowdsourcing.Odiscount 0.8 0.8 3.0 5000 15 Odiscount_sca_5000.csv

java -Xms40g -Xmx100g crowdsourcing.Odiscount 0.8 0.8 3.0 10000 15 Odiscount_sca_10000.csv
java -Xms40g -Xmx200g crowdsourcing.Nondiscount 0.8 0.8 3.0 10000 15 Nondiscount_sca_10000.csv
java -Xms40g -Xmx200g crowdsourcing.Ediscount 0.8 0.8 3.0 10000 15 Ediscount_sca_10000.csv

java -Xms40g -Xmx200g crowdsourcing.Odiscount 0.8 0.8 3.0 15000 15 Odiscount_sca_15000.csv
java -Xms40g -Xmx200g crowdsourcing.Nondiscount 0.8 0.8 3.0 15000 15 Nondiscount_sca_15000.csv
java -Xms40g -Xmx200g crowdsourcing.Ediscount 0.8 0.8 3.0 15000 15 Ediscount_sca_15000.csv

java -Xms40g -Xmx100g crowdsourcing.Odiscount 0.8 0.8 3.0 20000 15 Odiscount_sca_20000.csv
java -Xms40g -Xmx200g crowdsourcing.Nondiscount 0.8 0.8 3.0 20000 15 Nondiscount_sca_20000.csv
java -Xms40g -Xmx200g crowdsourcing.Ediscount 0.8 0.8 3.0 20000 15 Ediscount_sca_20000.csv







