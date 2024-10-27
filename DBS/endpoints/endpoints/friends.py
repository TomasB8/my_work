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
def get_friends(user_id):
    conn = get_db_connection()
    
    cursor = conn.cursor()

    schema = load_schema("schemas/users.json")      # loading JSONSchema
    users_list = {
        "items": []
    }

    # SQL query
    query = ("""
                SELECT DISTINCT users.* 
                FROM posts
                JOIN comments ON comments.postid = posts.id
                JOIN users ON comments.userid = users.id
                WHERE posts.owneruserid = %s OR posts.id IN (
                    SELECT comments.postid FROM comments
                    WHERE comments.userid = %s
                )
                ORDER BY users.creationdate ASC;
    """)

    cursor.execute(query, (user_id, user_id))       # execution of SQL query
    database_output = cursor.fetchall()

    # filling the dictionary with outputs from database
    for user in database_output:
        user_dict = {
            "id": user[0],
            "reputation": user[1],
            "creationdate": user[2].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + user[2].strftime('%z')[:3],
            "displayname": user[3],
            "lastaccessdate": user[4].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + user[4].strftime('%z')[:3],
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

@router.get("/v2/users/{user_id}/friends")
async def friends(user_id: int):
    return get_friends(user_id)
