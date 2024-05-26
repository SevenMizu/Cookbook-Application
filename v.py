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

# Function to delete all data from a table
def clear_table(table_name):
    cursor.execute(f"DELETE FROM {table_name}")
    conn.commit()

# Delete data from tables in the correct order
tables_in_delete_order = [
    "UserRecipeStar",
    "RecipeIngredient",
    "RecipeTag",
    "Comment",
    "Recipe",
    "Ingredient",
    "User",
    "Tag"
]

for table in tables_in_delete_order:
    print(f"Clearing table {table}...")
    clear_table(table)
    print(f"Table {table} cleared.")

# Close the cursor and connection
cursor.close()
conn.close()
