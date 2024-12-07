import os
from typing import Optional
import aiomysql
from src.logger import logger

from dotenv import load_dotenv

load_dotenv()
database_host = os.getenv("database_hostname")
database_port = int(os.getenv("database_port"))
database_username = os.getenv("database_username")
database_password = os.getenv("database_password")
database_name = os.getenv("database_name")

class MariadbConnectionPool:
    _instance: Optional["MySQLConnectionPool"] = None

    def __init__(self, minsize: int, maxsize: int, loop=None):
        self.minsize = minsize
        self.maxsize = maxsize
        self.loop = loop
        self.pool = None

    @classmethod
    def get_instance(cls, minsize: int = 1, maxsize: int = 10, loop=None):
        if cls._instance is None:
            cls._instance = cls(minsize, maxsize, loop)
        return cls._instance

    async def init_pool(self):
        try:
            if self.pool is None:
                self.pool = await aiomysql.create_pool(
                    host=database_host,
                    port=database_port,
                    user=database_username,
                    password=database_password,
                    db=database_name,
                    minsize=self.minsize,
                    maxsize=self.maxsize,
                    loop=self.loop,
                    autocommit=True,
                )
            logger.info("Pool initialized successfully.")
        except aiomysql.Error as e:
            logger.error("Error initializing pool: %s", e)
            raise

    async def close_pool(self):
        if self.pool:
            self.pool.close()
            await self.pool.wait_closed()
            logger.info("Database connection pool closed.")

    async def fetch_rows(self, query: str, params=None):
        params = params or ()
        try:
            async with self.pool.acquire() as conn:
                async with conn.cursor(aiomysql.DictCursor) as cur:
                    await cur.execute(query, params)
                    return await cur.fetchall()
        except aiomysql.Error as e:
            logger.error("Error fetching rows: %s", e)
            raise

    async def execute(self, query: str, params=None):
        params = params or ()
        try:
            async with self.pool.acquire() as conn:
                async with conn.cursor() as cur:
                    await cur.execute(query, params)
                    await conn.commit()
                    return cur.rowcount # 유사도 삭제(0 -> 1)시 영향 받은 행 표시 위함
        except aiomysql.Error as e:
            logger.error("Error executing query: %s", e)
            raise

    async def executeMany(self, query: str, params=None):
        params = params or ()
        try:
            async with self.pool.acquire() as conn:
                async with conn.cursor() as cur:
                    await cur.executemany(query, params)
                    await conn.commit()
                    return cur.lastrowid
        except aiomysql.Error as e:
            logger.error("Error executing many queries: %s", e)
            raise


# Create a connection pool instance for singleton pattern
database_instance = MariadbConnectionPool.get_instance()


async def init_db():
    logger.info("Database initialization.")
    await database_instance.init_pool()
    logger.info("Database initialization complete.")


async def close_db():
    logger.info("Closing database connection pool.")
    await database_instance.close_pool()
