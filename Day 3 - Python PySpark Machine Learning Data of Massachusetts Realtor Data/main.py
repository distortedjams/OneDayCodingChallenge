from pyspark.sql.functions import col
import pyspark.sql.functions as F
from pyspark.sql import SparkSession
from pyspark.ml.feature import VectorAssembler
from pyspark.ml.regression import DecisionTreeRegressor
from pyspark.ml.evaluation import RegressionEvaluator

# Create a SparkSession
spark = SparkSession.builder \
    .appName("House Price Prediction") \
    .config("spark.sql.debug.maxToStringFields", "500") \
    .getOrCreate()

# Ask for user input for the details of the house
bed = int(input("Enter the number of bedrooms: "))
bath = int(input("Enter the number of bathrooms: "))
acre_lot = float(input("Enter the size of the lot in acres: "))
house_size = int(input("Enter the size of the house in square feet: "))

# Create a dataframe with the user input as a single row
user_input_df = spark.createDataFrame([(bed, bath, acre_lot, house_size, 0.0)], ["bed", "bath", "acre_lot", "house_size", "price"])

# Read in the CSV file with the allowLeadingZeros option set to true
df = spark.read.option("header", True) \
               .option("inferSchema", True) \
               .option("allowLeadingZeros", True) \
               .csv("realtor-data.csv")

# Filter the dataframe to only include houses in Massachusetts
df = df.filter(col("state") == "Massachusetts")

# Drop any rows with missing values
df = df.na.drop()

# Select the relevant columns
df = df.select(col("bed"), col("bath"), col("acre_lot"), col("house_size"), col("price"))

# Create a VectorAssembler to combine the features into a single vector column
assembler = VectorAssembler(inputCols=["bed", "bath", "acre_lot", "house_size"], outputCol="features")
user_input_df = assembler.transform(user_input_df)

# Transform the data to include the features column
data = assembler.transform(df)

# Split the data into training and testing sets
train_data, test_data = data.randomSplit([0.7, 0.3], seed=42)

# Create a DecisionTreeRegressor model and fit it to the training data
dt = DecisionTreeRegressor(labelCol="price", maxDepth=5, maxBins=32)
dt_model = dt.fit(train_data)

# Make predictions on the user input
predictions = dt_model.transform(user_input_df)

# Extract the predicted price from the predictions dataframe
predicted_price = predictions.select("prediction").collect()[0][0]

# Print the predicted price
print("The predicted price of the house in Massachusetts is: ${:,.2f}".format(predicted_price))
