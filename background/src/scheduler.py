import asyncio
from apscheduler.schedulers.asyncio import AsyncIOScheduler

from src.logger import logger


scheduler = AsyncIOScheduler()


def get_scheduler():
    return scheduler


def job_listener(job):
    """
    스케줄러에 등록된 job 이 실행되거나 예외를 발생시킬 때를 감지하여 실행된다.
    예외를 발생시킨 경우, 해당 프로세스에 문제가 있다고 판단하여 server status 를 업데이트한다.
    """
    if job.exception:
        body = f"Job {job.job_id} failed with exception: {job.exception}"
        logger.error(body)


        #todo : create_tast() 에 해당 조건에 실행할 메서드 담기
        if job.job_id == "al_id1":
            asyncio.create_task(some_async_task_01)

        elif job.job_id == "al_id02":
            asyncio.create_task(some_async_task_02)

    else:
        if job.job_id == "al_id1":
            asyncio.create_task(some_async_task_01)

        elif job.job_id == "al_id02":
            asyncio.create_task(some_async_task_02)
async def some_async_task_01():
    logger.info("Executing Task 01...")

async def some_async_task_02():
    logger.info("Executing Task 02...")