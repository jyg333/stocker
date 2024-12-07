from src.connection_pool import database_instance
from src.logger import logger, main_logger
from src.connection_pool import init_db, close_db

import asyncio
import sys

# if sys.platform == 'win32':
#     asyncio.set_event_loop_policy(asyncio.WindowsSelectorEventLoopPolicy())
async def fetch_all_al_trades():
    """
    Fetch all rows from al_trade table.
    :return: List of all rows in the al_trade table.
    """
    query = "SELECT * FROM stocker.al_trade"
    try:
        rows = await database_instance.fetch_rows(query)

        return rows
    except Exception as e:
        logger.error("Failed to fetch all al_trade rows: %s", e)
        raise

async def main():
    await init_db()

    # Fetch all rows
    all_trades = await fetch_all_al_trades()
    logger.info("All Trades: %s", all_trades)


    await close_db()

# Run the main function
if __name__ == "__main__":

    asyncio.run(main())

