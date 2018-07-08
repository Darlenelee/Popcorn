import pandas as pd

pd.set_option("display.max_columns", 20)
df = pd.DataFrame(pd.read_csv('train.csv',header=0)\
                  ,columns=["id","vendor_id","pickup_datetime","dropoff_datetime",\
                            "passenger_count","pickup_longitude","pickup_latitude",\
                            "dropoff_longitude", "dropoff_latitude"])

print df.head()
print df.tail()

df.sort_values(by="pickup_datetime",ascending=True,inplace=True)

print df.head()
print df.tail()

df.to_csv('train-sort.csv', encoding = 'utf-8', index = False)