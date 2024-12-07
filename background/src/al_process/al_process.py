
def typeName():
    # 설정 시간 마다 자동처리 서버 DB를 확인하여 new, update, delete 할 대상을 확인
    try:
        logger.info("Parsing Auto-Process-Server Database")
        get_scheduler().add_job(
            parsePRCD,
            "interval",
            minutes=int(os.getenv("CHECK_CYCLE")),
            id="parse",
            next_run_time=datetime.now(),
        )
        logger.info("Successfully Parsing Auto-Process-Server Database")
    except Exception as e:
        logger.error("Fail to parse Auto-Process-Server Database")
        raise e