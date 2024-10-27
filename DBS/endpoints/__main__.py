from fastapi import FastAPI

from dbs_assignment.router import router
from dbs_assignment.database import initialize_db_pool, remove_db_pool

app = FastAPI(title="DBS")
app.include_router(router)

# initializing connection pool at the start
@app.on_event("startup")
async def on_startup():
    initialize_db_pool()

# emptying connection pool at the end
@app.on_event("shutdown")
async def on_shutdown():
    remove_db_pool()