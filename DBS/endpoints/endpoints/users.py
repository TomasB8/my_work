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
def get_users(post_id):
    conn = get_db_connection()
    
    cursor = conn.cursor()

    schema = load_schema("schemas/users.json")      # loading JSONSchema
    users_list = {
        "items": []
    }

    # SQL query
    query = ("""
                SELECT * FROM (
                    SELECT DISTINCT ON(users.id) users.*, comments.creationdate AS com_c FROM comments
                    JOIN users ON comments.userid = users.id
                    WHERE postid = %s
                    ORDER BY users.id, comments.creationdate DESC
                ) AS u
                ORDER BY com_c DESC;
    """)

    cursor.execute(query, (post_id,))       # execution of SQL query
    database_output = cursor.fetchall()

    # filling the dictionary with outputs from database
    for user in database_output:
        user_dict = {
            "id": user[0],
            "reputation": user[1],
            "creationdate": user[2].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + user[2].strftime('%z')[:3] + ":" + user[2].strftime('%z')[3:],
            "displayname": user[3],
            "lastaccessdate": user[4].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + user[4].strftime('%z')[:3] + ":" + user[4].strftime('%z')[3:],
            "websiteurl": user[5],
            "location": user[6],
            "aboutme": user[7],
            "views": user[8],
            "upvotes": user[9],
            "downvotes": user[10],
            "profileimageurl": user[11],
            "age": user[12],
            "accountid": user[13],
        }

        users_list["items"].append(user_dict)

    # checking the correct format of the data  
    validate(users_list, schema)

    # return connection to database pool
    cursor.close()
    return_db_connection(conn)

    return users_list


@router.get("/v2/posts/{post_id}/users")
async def users(post_id: int):
    return get_users(post_id)
