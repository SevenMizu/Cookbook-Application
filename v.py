import mysql.connector

# Connect to the MySQL database
conn = mysql.connector.connect(
    host="viaduct.proxy.rlwy.net",
    port=57127,
    database="railway",
    user="root",
    password="itnAabOeXLHeGbaZTaHxsnDSKpauEPLy"
)

# Create a cursor object
cursor = conn.cursor()

# Function to get schema of a table
def get_table_schema(table_name):
    cursor.execute(f"DESCRIBE {table_name}")
    return cursor.fetchall()

# Get schema for each table
tables = ["User", "Comment", "Ingredient", "Tag", "UserRecipeStar", "RecipeIngredient", "RecipeTag"]
for table in tables:
    print(f"Schema for table {table}:")
    schema = get_table_schema(table)
    for field in schema:
        print(field)

# Close the cursor and connection
cursor.close()
conn.close()
