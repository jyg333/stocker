import asyncio
import os
import signal
from src.connection_pool import init_db, close_db
from src.logger import logger
from dotenv import load_dotenv
from src.scheduler import get_scheduler
from src.al_process.al_process import  altypeOne,request2FMP
from datetime import datetime, time, timedelta

load_dotenv()

async def main():
    await init_db()
    logger.info("Database initialized")

    try:
        logger.info("Initializing process")
        sched = get_scheduler()
        sched.add_job(
            altypeOne,
            "interval",
            # hours=int(os.getenv("AL_CYCLE")),
            hours=int(os.getenv("AL_CYCLE")),
            id="al_id1",
            next_run_time=datetime.now()+ timedelta(minutes=2),
            # next_run_time=datetime.now(),
            misfire_grace_time=60,
        )
        # 두 번째 작업: 매일 특정 시간에 실행
        specific_time = time(15, 30)  # 오후 3시 30분 실행
        sched.add_job(
            request2FMP,
            "cron",
            hour=specific_time.hour,
            minute=specific_time.minute,
            id="al_id2",
            next_run_time=datetime.now(),
            misfire_grace_time=60,
        )

        sched.start()
        logger.info("Scheduler started successfully")

        # 스케줄러가 백그라운드에서 동작하도록 유지
        while True:
            await asyncio.sleep(1)
    except asyncio.CancelledError:
        logger.info("Main task cancelled")
    except Exception as e:
        logger.error(f"An error occurred: {e}")
    finally:
        if sched.running:
            sched.shutdown()
            logger.info("Scheduler stopped.")
        await close_db()
        logger.info("Database connection pool closed.")

def handle_exit(loop, sched):
    """종료 신호를 처리하여 스케줄러와 데이터베이스 연결을 정리"""
    for task in asyncio.all_tasks(loop):
        task.cancel()
    loop.stop()



if __name__ == '__main__':
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    sched = get_scheduler()

    # 종료 신호 처리
    for sig in (signal.SIGINT, signal.SIGTERM):
        loop.add_signal_handler(sig, handle_exit, loop, sched)

    try:
        loop.run_until_complete(main())
    except (KeyboardInterrupt, SystemExit):
        pass
    # finally:
    #     loop.close()
    #     logger.info("Event loop closed.")
