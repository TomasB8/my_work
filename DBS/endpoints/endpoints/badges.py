from fastapi import APIRouter
import json

from dbs_assignment.database import get_db_connection, return_db_connection

router = APIRouter()


def get_user_badges(user_id):
    conn = get_db_connection()
    
    cursor = conn.cursor()

    badges_list = {
        "items": []
    }

    # SQL query
    query = ("""
                SELECT 
                    id,
                    title,
                    type,
                    TO_CHAR(created_at, 'YYYY-MM-DD"T"HH24:MI:SS.MS') || TO_CHAR(created_at, 'TZH'),
                    ROW_NUMBER() OVER (PARTITION BY type ORDER BY created_at) AS position
                FROM (
                    SELECT 
                        id,
                        title,
                        type,
                        created_at,
                        LAG(type) OVER (ORDER BY created_at) AS prev_type,
                        LEAD(type) OVER (ORDER BY created_at) AS next_type
                    FROM (
                        SELECT 
                            p.id,
                            p.title,
                            'post' AS type,
                            p.creationdate AS created_at
                        FROM 
                            posts p
                        WHERE 
                            p.owneruserid = %s
                        UNION ALL
                        SELECT 
                            b.id,
                            b.name,
                            'badge' AS type,
                            b.date AS created_at
                        FROM 
                            badges b
                        WHERE 
                            b.userid = %s
                    ) AS subq
                ) AS data
                WHERE 
                    (type = 'badge' AND prev_type = 'post')
                    OR
                    (type = 'post' AND next_type = 'badge')
                ORDER BY
                    created_at;
    """)

    cursor.execute(query, (user_id, user_id))      # execution of SQL query
    database_output = cursor.fetchall()

    # filling the dictionary with outputs from database
    for data in database_output:
        badges_dict = {
            "id": data[0],
            "title": data[1],
            "type": data[2],
            "created_at": data[3],
            "position": data[4]
        }

        badges_list["items"].append(badges_dict)

    # return connection to database pool
    cursor.close()
    return_db_connection(conn)

    return badges_list


@router.get("/v3/users/{user_id}/badge_history")
async def comments(user_id: int):
    return get_user_badges(user_id)
