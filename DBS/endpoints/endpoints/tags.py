from fastapi import APIRouter
import json

from dbs_assignment.database import get_db_connection, return_db_connection

router = APIRouter()

# function that loads a JSONSchema
def load_schema(schema_path):
    with open(schema_path, "r") as schema_file:
        return json.load(schema_file)

# function that returns objects from database
def get_comments(tagname, position, limit):
    conn = get_db_connection()
    
    cursor = conn.cursor()

    comments_list = {
        "items": []
    }

    # SQL query
    query = ("""
                SELECT * FROM (
                    SELECT 
                        comments.id, 
                        displayname, 
                        body, 
                        text, 
                        comments.score,
                        RANK() OVER (PARTITION BY posts.id ORDER BY posts.creationdate, comments.creationdate) AS position	
                    FROM comments
                    JOIN posts ON comments.postid = posts.id
                    JOIN post_tags ON post_tags.post_id = posts.id
                    JOIN tags ON post_tags.tag_id = tags.id
                    JOIN users ON comments.userid = users.id
                    WHERE tagname = %s
                    ORDER BY posts.creationdate, comments.creationdate
                ) AS subquery
                WHERE position = %s
                LIMIT %s
             
    """)
    
    cursor.execute(query, (tagname, position, limit))      # execution of SQL query
    database_output = cursor.fetchall()

    # filling the dictionary with outputs from database
    for comment in database_output:
        comment_dict = {
            "id": comment[0],
            "displayname": comment[1],
            "body": comment[2],
            "text": comment[3],
            "score": comment[4],
            "position": comment[5]
        }

        comments_list["items"].append(comment_dict)

    # return connection to database pool
    cursor.close()
    return_db_connection(conn)

    return comments_list


def get_count_comments(tagname, count):
    conn = get_db_connection()
    
    cursor = conn.cursor()

    comments_list = {
        "items": []
    }

    # SQL query
    query = ("""
                SELECT 
                    postid, 
                    title, 
                    displayname, 
                    text, 
                    TO_CHAR(post_created_at, 'YYYY-MM-DD"T"HH24:MI:SS.MS') || TO_CHAR(post_created_at, 'TZH'), 
                    TO_CHAR(creationdate, 'YYYY-MM-DD"T"HH24:MI:SS.MS') || TO_CHAR(creationdate, 'TZH'), 
                    TRIM(trailing '0' FROM diff::text),
                    TRIM(trailing '0' FROM AVG(diff) OVER (PARTITION BY postid ORDER BY creationdate ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)::text) AS average
                FROM(
                    SELECT 
                        postid, 
                        title, 
                        displayname, 
                        text, 
                        post_created_at, 
                        creationdate,
                        CASE
                            WHEN ROW_NUMBER() OVER (PARTITION BY postid ORDER BY creationdate) = 1
                                THEN (creationdate - post_created_at)::interval
                                ELSE (creationdate - LAG(creationdate) OVER (PARTITION BY postid ORDER BY creationdate))::interval 
                        END AS diff
                    FROM (
                        SELECT 
                            postid, 
                            title, 
                            displayname, 
                            comments.text, 
                            posts.creationdate AS post_created_at, 
                            comments.creationdate,
                            COUNT(postid) OVER (PARTITION BY postid) AS comments_num
                        FROM posts
                        LEFT JOIN comments ON comments.postid = posts.id
                        LEFT JOIN users ON comments.userid = users.id
                        LEFT JOIN post_tags ON post_tags.post_id = posts.id
                        LEFT JOIN tags ON tags.id = post_tags.tag_id
                        WHERE tagname = %s
                        ORDER BY comments.creationdate ASC
                    ) AS tab
                    WHERE comments_num > %s
                ) AS subquery

    """)

    cursor.execute(query, (tagname, count))      # execution of SQL query
    database_output = cursor.fetchall()

    # filling the dictionary with outputs from database
    for comment in database_output:
        comment_dict = {
            "post_id": comment[0],
            "title": comment[1],
            "displayname": comment[2],
            "text": comment[3],
            "post_created_at": comment[4],
            "created_at": comment[5],
            "diff": comment[6],
            "avg": comment[7]
        }

        comments_list["items"].append(comment_dict)

    # return connection to database pool
    cursor.close()
    return_db_connection(conn)

    return comments_list


@router.get("/v3/tags/{tagname}/comments/{position}")
async def comments(tagname: str, position: int, limit: int):
    return get_comments(tagname, position, limit)

@router.get("/v3/tags/{tagname}/comments")
async def count_comments(tagname: str, count: int):
    return get_count_comments(tagname, count)
