import pandas as pd
import random
import datetime
import numpy as np


def filter_orders():
    start = datetime.datetime(2013, 1, 13, 18, 00, 0)
    end = datetime.datetime(2013, 1, 13, 19, 00, 0)
    df = pd.read_csv('trip_data_1_sorted1.csv')
    df['pickup_datetime'] = pd.to_datetime(df['pickup_datetime'])
    out = df[df['pickup_datetime'].dt.date == start.date()]
    out = out[out['pickup_datetime'].dt.time.between(start.time(), end.time())]
    out = out.sort_values(by='pickup_datetime', ascending=True)
    print(out)
    out.to_csv('1.13 18pm_order.csv', index=False, header=None)


def fix_datetime(data, secs, filename):
    for idx, row in data.iterrows():
        data.at[idx, 'datetime'] = pd.to_datetime(data.at[idx, 'datetime']) + pd.to_timedelta(secs[idx], 's')
    data.to_csv(filename, index=False, header=None)


def fix_orders():
    order_filename = '1.13 18pm_order.csv'
    df = pd.read_csv(order_filename, names=['datetime', 'x', 'dist', 'lon', 'lat', 'z'])
    g = df.groupby(['datetime'])
    orders_per_minute = g.size().tolist()
    seconds_per_minute = [sorted([random.randint(0, 59) for _ in range(l)]) for l in orders_per_minute]
    seconds = []
    for s in seconds_per_minute:
        seconds.extend(s)
    assert len(df) == len(seconds)
    fix_datetime(df, seconds, '1.13 18pm_order_fix.csv')


def gen_vehicles():
    inname = '1.13 18pm_order_fix.csv'
    outname = '1.13 18pm_vehicle_fix.csv'
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
        n_cars = 5 * n_orders
        one_minute = [[datetime, np.random.random() * 0.4 + (mean_lon), np.random.random() * 0.3 + (mean_lat)] for i in range(n_cars)]
        cars.extend(one_minute)

    out = pd.DataFrame(cars)
    print(out)

    print('Write data to {}'.format(outname))
    out.to_csv(outname, header=None, index=False)


filter_orders()
fix_orders()
gen_vehicles()
