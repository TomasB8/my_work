from psycopg2 import pool
from dbs_assignment.config import settings

# global variable representing database connection pool
database_pool = None

# initializing connection pool, with 1-10 connections
def initialize_db_pool():
    global database_pool
    database_pool = pool.SimpleConnectionPool(
        minconn=1,
        maxconn=10,
        user=settings.DATABASE_USER,
        password=settings.DATABASE_PASSWORD,
        host=settings.DATABASE_HOST,
        port=settings.DATABASE_PORT,
        database=settings.DATABASE_NAME,
    )

# emptying connection pool
def remove_db_pool():
    global database_pool
    if database_pool:
        database_pool.closeall()

# function that returns a connection to database
def get_db_connection():
    connection = database_pool.getconn()
    return connection

# function that returns connection to database
def return_db_connection(connection):
    database_pool.putconn(connection)