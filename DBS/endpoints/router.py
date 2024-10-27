from fastapi import APIRouter

from dbs_assignment.endpoints import friends, stats, status, users, posts, tags, badges

router = APIRouter()
router.include_router(status.router, tags=["status"])
router.include_router(users.router, tags=["users"])
router.include_router(friends.router, tags=["friends"])
router.include_router(stats.router, tags=["stats"])
router.include_router(posts.router, tags=["posts"])
router.include_router(tags.router, tags=["tags"])
router.include_router(badges.router, tags=["badges"])