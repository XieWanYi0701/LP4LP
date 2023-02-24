import pandas as pd
import random
import datetime
import numpy as np


def filter_orders():
    start = datetime.datetime(2013, 1, 13, 14, 0, 0)
    end = datetime.datetime(2013, 1, 14, 0, 0, 0)
    df = pd.read_csv('trip_data_1_sorted1.csv')
    df['pickup_datetime'] = pd.to_datetime(df['pickup_datetime'])
    out = df[~(df.iloc[:, :].eq(0).any(1))]
    out = out[out['pickup_datetime'] >= start]
    out = out[:50000]
    print(out)
    out.to_csv('1.13 5w_order.csv', index=False, header=None)


def gen_vehicles():
    inname = '1.13 5w_order.csv'
    outname = '1.13 5w_vehicle.csv'
    mean_lon = -74.1
    mean_lat = 40.6

    df = pd.read_csv(inname, header=None, names=['datetime', 'w', 'h', 'at', 'ev', 'er'])
    df_filtered = df[~(df.iloc[:, :].eq(0).any(1))]
    print('Number of valid records: {}'.format(len(df_filtered)))

    g = df_filtered.groupby(['datetime'])
    orders_per_minute = g.size().tolist()
    datetime_per_minute = list(g.groups.keys())

    cars = []
    for datetime, n_orders in zip(datetime_per_minute, orders_per_minute):
        n_cars = n_orders
        one_minute = [[datetime, np.random.random() * 0.4 + (mean_lon), np.random.random() * 0.3 + (mean_lat)] for i in range(n_cars)]
        cars.extend(one_minute)

    out = pd.DataFrame(cars)
    print(out)

    print('Write data to {}'.format(outname))
    out.to_csv(outname, header=None, index=False)


filter_orders()
gen_vehicles()
