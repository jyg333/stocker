import logging
import logging.handlers
import sys
from pathlib import Path


class CustomFormatter(logging.Formatter):
    def format(self, record):
        # Combine pathname and lineno into a new attribute
        record.pathname_lineno = f"{record.pathname}:{record.lineno}"
        return super().format(record)


# 절대 경로를 특정 루트 디렉토리를 기준으로 상대 경로로 변환하는 포매터
class CustomPathFormatter(logging.Formatter):
    def __init__(self, *args, root_dir="src", **kwargs):
        super().__init__(*args, **kwargs)
        self.root_dir = root_dir

    def format(self, record):
        # 절대 경로를 상대 경로로 변환
        path = Path(record.pathname)
        if self.root_dir in path.parts:
            # 루트 디렉토리부터 상대 경로 계산
            record.pathname = str(Path(*path.parts[path.parts.index(self.root_dir):]))
        return super().format(record)


# 로그 설러 팔레트 정의
class LogColors:
    RED = "\033[31m"
    GREEN = "\033[32m"
    YELLOW = "\033[33m"
    BLUE = "\033[34m"
    SKY = "\033[94m"
    RESET = "\033[0m"  # 컬러 코드의 끝을 알리는 코드


# 로그 레벨 별로 색 정의
class ColorFormatter(CustomFormatter):
    COLOR_MAP = {
        logging.INFO: LogColors.GREEN,
        logging.WARNING: LogColors.YELLOW,
        logging.ERROR: LogColors.RED,
        logging.DEBUG: LogColors.BLUE,
    }

    def format(self, record):
        # Call the parent class to combine pathname and lineno
        super().format(record)

        # Apply color to level name
        color = self.COLOR_MAP.get(record.levelno, "")
        record.levelname = f"{color}{record.levelname}{LogColors.RESET}"

        # Apply blue color to the combined pathname and line number
        record.pathname_lineno = (
            f"{LogColors.SKY}{record.pathname_lineno}{LogColors.RESET}"
        )

        # Format the message
        message = super(CustomFormatter, self).format(record)

        return message


def setting_FileHandler(file_dir: str, interval_day: int):
    _file_handler = logging.handlers.TimedRotatingFileHandler(
        filename=Path(__file__).parent.parent.joinpath(file_dir),
        when="midnight",
        interval=interval_day,
        encoding="utf-8",
        backupCount=1,
    )
    _file_handler.doRollover = lambda: None
    return _file_handler


# get logger
logger = logging.getLogger("basic")
main_logger = logging.getLogger("main")

# formatter
color_formatter = ColorFormatter(  # 코드 로그: 콘솔 출력용
    fmt="%(asctime)s | %(levelname)s: %(message)s | %(pathname_lineno)s",
    datefmt="%Y/%m/%d %H:%M:%S",
)
path_formatter = CustomPathFormatter(  # 파일 로그: 파일용
    fmt="%(asctime)s | %(levelname)s: %(message)s | %(pathname)s:%(lineno)d",
    datefmt="%Y/%m/%d %H:%M:%S",
    root_dir="src",  # 상대 경로 기준 디렉토리 설정
)

# handlers
stream_handler = logging.StreamHandler(sys.stdout)  # 코드 로그: 콘솔 출력용 핸들러
main_handler = setting_FileHandler("logs/logging.log", 1)
main_handler.suffix = "_%Y%m%d.log"

# set formatter
stream_handler.setFormatter(color_formatter)  # 콘솔 출력용 핸들러는 색상 포매터 사용
main_handler.setFormatter(path_formatter)  # 파일 핸들러는 경로 포매터 사용

# set handler
logger.handlers = [stream_handler]
main_logger.handlers = [main_handler]

# set log level
logger.setLevel(logging.INFO)
main_logger.setLevel(logging.INFO)
