from fastapi import APIRouter
import json
from jsonschema import validate

from dbs_assignment.database import get_db_connection, return_db_connection

router = APIRouter()

# function that loads a JSONSchema
def load_schema(schema_path):
    with open(schema_path, "r") as schema_file:
        return json.load(schema_file)

# function that returns objects from database
def get_stats(tagname):
    conn = get_db_connection()
    
    cursor = conn.cursor()

    schema = load_schema("schemas/posts-load.json")     # loading JSONSchema
    stats_list = {
        "result": {
            "monday": None,
            "tuesday": None,
            "wednesday": None,
            "thursday": None,
            "friday": None,
            "saturday": None,
            "sunday": None
        }
    }

    # SQL query
    query = ("""
                    SELECT  TO_CHAR(posts.creationdate, 'day') AS day_of_week,
                            ROUND(
                                (COUNT(CASE WHEN tags.tagname = %s THEN 1 END) * 100.0) / COUNT(DISTINCT posts.id), 2
                            ) AS percentage
                    FROM posts
                    JOIN post_tags ON posts.id = post_tags.post_id
                    JOIN tags ON post_tags.tag_id = tags.id
                    GROUP BY day_of_week
    """)

    cursor.execute(query, (tagname, ))      # execution of SQL query
    database_output = cursor.fetchall()

    # filling the dictionary with outputs from database
    for stat in database_output:
        stats_list["result"][stat[0].strip()] = stat[1]

    # checking the correct format of the data
    validate(stats_list, schema)

    # return connection to database pool
    cursor.close()
    return_db_connection(conn)

    return stats_list


@router.get("/v2/tags/{tagname}/stats")
async def stats(tagname: str):
    return get_stats(tagname)
