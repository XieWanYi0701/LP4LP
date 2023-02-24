import argparse
from pathlib import Path
import random
import numpy as np
import pandas as pd


def gen_orders(order_filename, out_filename):
    df = pd.read_csv(order_filename, names=['datetime', 'time', 'distance', 'pickup_lon', 'pickup_lat', 'dropoff_lon', 'dropoff_lat', 'amount'])
    g = df.groupby(['datetime'])
    orders_per_minute = g.size().tolist()
    seconds_per_minute = [sorted([random.randint(0, 59) for _ in range(l)]) for l in orders_per_minute]
    seconds = []
    for s in seconds_per_minute:
        seconds.extend(s)
    assert len(df) == len(seconds)
    for idx, row in df.iterrows():
        df.at[idx, 'datetime'] = pd.to_datetime(df.at[idx, 'datetime']) + pd.to_timedelta(seconds[idx], 's')
    df.to_csv(out_filename, index=False, header=None)


def gen_vehicles(order_filename, out_filename, voratio):
    df = pd.read_csv(order_filename, header=None, names=['datetime', 'w', 'h', 'a', 't', 'e', 'v', 'er'])
    df = df[~(df.iloc[:, :].eq(0).any(1))]
    g = df.groupby(['datetime'])
    orders_per_minute = g.size().tolist()
    datetime_per_minute = list(g.groups.keys())
    vehicles = []
    mean_lon = -74.1
    mean_lat = 40.6
    for datetime, n_orders in zip(datetime_per_minute, orders_per_minute):
        n_vehicles = voratio * n_orders
        one_minute = [[datetime, np.random.random() * 0.4 + (mean_lon), np.random.random() * 0.3 + (mean_lat)] for i in range(n_vehicles)]
        vehicles.extend(one_minute)
    out = pd.DataFrame(vehicles)
    out.to_csv(out_filename, index=False, header=None)


def main(args):
    order_filename = Path(args.inpath)
    out_order_filename = Path(args.order_out)
    out_vehicle_filename = Path(args.vehicle_out)
    gen_orders(order_filename, out_order_filename)
    gen_vehicles(out_order_filename, out_vehicle_filename, args.voratio)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--inpath', required=True)
    parser.add_argument('--order-out', required=True)
    parser.add_argument('--vehicle-out', required=True)
    parser.add_argument('--voratio', type=int, required=True)
    args = parser.parse_args()
    main(args)
