from src.scheduler import get_scheduler
from src.logger import logger
import os
from datetime import datetime

def typeName():

    try:
        logger.info("Init process")
        get_scheduler().add_job(
            altypeOne,
            "interval",
            minutes=int(os.getenv("CHECK_CYCLE")),
            id="parse",
            next_run_time=datetime.now(),
        )
        logger.info("Successfully Parsing Auto-Process-Server Database")
    except Exception as e:
        logger.error("Fail to parse Auto-Process-Server Database")
        raise e