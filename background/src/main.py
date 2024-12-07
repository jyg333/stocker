import os

from src.logger import logger, main_logger
import time
from dotenv import load_dotenv

load_dotenv()
database_host = os.getenv("database_hostname")
def main():
    logger.info("start")
    main_logger.info("start %s",database_host)
    time.sleep(1)
    logger.info("start2")
    main_logger.info("start2")
    time.sleep(1)
    logger.info("start3")
    main_logger.info("start3")

if __name__ == '__main__':
    main()