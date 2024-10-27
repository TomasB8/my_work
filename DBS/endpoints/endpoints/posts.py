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
def get_posts(duration, limit):
    conn = get_db_connection()
    
    cursor = conn.cursor()

    schema = load_schema("schemas/posts.json")      # loading JSONSchema
    posts_list = {
        "items": []
    }

    # SQL query
    query = ("""
                SELECT 
                    id, 
                    creationdate, 
                    viewcount, 
                    lasteditdate, 
                    lastactivitydate, 
                    title, closeddate, 
                    ROUND(EXTRACT(EPOCH FROM posts.closeddate - posts.creationdate) / 60, 2) as duration 
                FROM posts
                WHERE closeddate IS NOT NULL AND EXTRACT(EPOCH FROM posts.closeddate - posts.creationdate) / 60 < %s
                ORDER BY closeddate DESC
                LIMIT %s
    """)

    cursor.execute(query, (duration, limit))        # execution of SQL query
    database_output = cursor.fetchall()

    # filling the dictionary with outputs from database
    for post in database_output:
        post_dict = {
            "id": post[0],
            "creationdate": post[1].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + post[1].strftime('%z')[:3] if post[1] is not None else None,
            "viewcount": post[2],
            "lasteditdate": post[3].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + post[3].strftime('%z')[:3] if post[3] is not None else None,
            "lastactivitydate": post[4].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + post[4].strftime('%z')[:3] if post[4] is not None else None,
            "title": post[5],
            "closeddate": post[6].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + post[6].strftime('%z')[:3] if post[6] is not None else None,
            "duration": post[7]
        }

        posts_list["items"].append(post_dict)
    
    # checking the correct format of the data
    validate(posts_list, schema)

    # return connection to database pool
    cursor.close()
    return_db_connection(conn)

    return posts_list


def get_posts_query(limit, query):
    conn = get_db_connection()
    
    cursor = conn.cursor()

    schema = load_schema("schemas/post-search.json")         # loading JSONSchema
    posts_list = {
        "items": []
    }

    # SQL query
    SQLquery = ("""
                    SELECT 
                        p.id, 
                        p.creationdate, 
                        p.viewcount, 
                        p.lasteditdate,
                        p.lastactivitydate, 
                        p.title, 
                        p.body, 
                        p.answercount,
                        p.closeddate,
                        (
                            SELECT ARRAY_AGG(tags.tagname) 
                            FROM post_tags AS pt
                            JOIN tags ON tags.id = pt.tag_id
                            WHERE pt.post_id = p.id
                        ) AS tag_names
                    FROM posts AS p
                    WHERE UNACCENT(p.body) ILIKE UNACCENT(%s) OR UNACCENT(p.title) ILIKE UNACCENT(%s)
                    ORDER BY p.creationdate DESC
                    LIMIT %s;

    """)

    cursor.execute(SQLquery, (f"%{query}%",f"%{query}%",limit))     # execution of SQL query
    database_output = cursor.fetchall()

    # filling the dictionary with outputs from database
    for post in database_output:
        post_dict = {
            "id": post[0],
            "creationdate": post[1].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + post[1].strftime('%z')[:3] if post[1] is not None else None,
            "viewcount": post[2],
            "lasteditdate": post[3].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + post[3].strftime('%z')[:3] if post[3] is not None else None,
            "lastactivitydate": post[4].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + post[4].strftime('%z')[:3] if post[4] is not None else None,
            "title": post[5],
            "body": post[6],
            "answercount": post[7],
            "closeddate": post[8].strftime('%Y-%m-%dT%H:%M:%S.%f').rstrip('0') + post[8].strftime('%z')[:3] if post[8] is not None else None,
            "tags": post[9]
        }

        posts_list["items"].append(post_dict)

    # checking the correct format of the data
    validate(posts_list, schema)

    # return connection to database pool
    cursor.close()
    return_db_connection(conn)

    return posts_list

def get_posts_thread(postid, limit):
    conn = get_db_connection()
    
    cursor = conn.cursor()

    posts_list = {
        "items": []
    }

    # SQL query
    query = ("""
                SELECT 
                    displayname, 
                    body, 
                    TO_CHAR(posts.creationdate, 'YYYY-MM-DD"T"HH24:MI:SS.MS') || TO_CHAR(posts.creationdate, 'TZH') AS iso_datetime 
                FROM 
                    posts
                JOIN users ON users.id = owneruserid
                WHERE posts.id = %s OR parentid = %s
                ORDER BY posts.creationdate ASC
                LIMIT %s;
    """)

    cursor.execute(query, (postid, postid, limit))        # execution of SQL query
    database_output = cursor.fetchall()

    # filling the dictionary with outputs from database
    for post in database_output:
        post_dict = {
            "displayname": post[0],
            "body": post[1],
            "created_at": post[2]
        }

        posts_list["items"].append(post_dict)

    # return connection to database pool
    cursor.close()
    return_db_connection(conn)

    return posts_list


@router.get("/v2/posts")
async def posts(duration: int = None, limit: int = None, query: str = None):
    if query is not None:
        return get_posts_query(limit, query)
    elif duration is not None:
        return get_posts(duration, limit)
    else:
        return {"error": "Invalid query parameters"}
    
@router.get("/v3/posts/{postid}")
async def posts(postid: int, limit: int):
    return get_posts_thread(postid, limit)