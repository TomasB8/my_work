from fastapi import APIRouter

from dbs_assignment.database import get_db_connection, return_db_connection

router = APIRouter()

# Funtion that connects to database and returns a database version
def get_version():
    conn = get_db_connection()
    
    cursor = conn.cursor()
    cursor.execute("SELECT VERSION();")     #SQL query

    version = cursor.fetchone()[0]
    cursor.close()
    return_db_connection(conn)

    return version

@router.get("/v1/status")
async def status():
    return {
        'version': get_version()
    }
