import asyncio

from src.connection_pool import database_instance
from src.scheduler import get_scheduler
from src.logger import logger, main_logger
import requests

import os
from dotenv import load_dotenv
from datetime import datetime, timedelta

load_dotenv()
api_key = os.getenv("fmp_api")

async def altypeOne():
    """
        Fetch all rows from al_trade table.
        :return: List of all rows in the al_trade table.
        """
    current_time = datetime.now()
    cur_date :str = str(current_time.date())

    # Subtract one day
    # DB에서 조회한 데이터가 오늘보다 1회 이전의 값을 포함하고 있음을 보장

    query = "SELECT * FROM stocker.al_trade WHERE activation is true AND al_id=1"
    volume_query = "SELECT vol_date, change_ratio FROM stock_volume WHERE symbol=%s AND vol_date <= %s ORDER BY vol_date DESC LIMIT 5"
    rows : list
    try:
        rows :list = await database_instance.fetch_rows(query)
        for row in rows:
            symbol : str = row["symbol"]
            params = (symbol, cur_date,)
            # print(params)
            vol_data = await database_instance.fetch_rows(volume_query,params=params)
            # print(vol_data)
            if not vol_data:
                logger.debug("Check algorithm trade process, There is nothing to execute")
            else:

                # 최근 3일간 change_ratio가 모두 양수인지 확인
                recent_3_days = vol_data[:3]  # 최근 3일 (리스트의 첫 3개 요소)
                recent_2_days = vol_data[:2]  # 최근 3일 (리스트의 첫 3개 요소)

                # 모든 change_ratio가 양수인지 확인
                all_positive = all(r3d["change_ratio"] > 0 for r3d in recent_3_days)
                two_negative = all(r2d["change_ratio"] < 0 for r2d in recent_2_days)

                main_logger.info(f"positive negative result : positive : {all_positive}, nagative : {two_negative}")
                trade_type: int

                idx = row['idx']
                cur_amount = row["cur_amount"]
                buy_amount = row['buy_amount']
                init_amount = row["init_amount"]
                main_logger.info(f"Exist buy amount : {buy_amount}")
                profit_loss = 0

                #이미 구매한 경우 hold : 3
                if all_positive and buy_amount:
                    trade_type = 3#hold
                    profit_loss = (cur_amount + buy_amount) // init_amount
                    result_params = (idx, symbol, trade_type, None, None, profit_loss)
                # 기존거래 없음 및 구매조건 true
                elif all_positive and not buy_amount:

                    trade_type = 1 #hold
                    #기존에 거래가 없기 때문에 거래 로직 시작
                    cur_price = await requestCurprice(symbol)

                    buy_amount :float = (cur_amount // cur_price) * cur_price
                    cur_amount = cur_amount - buy_amount
                    main_logger.info(f"Buy amount : {buy_amount} || cur_amount : {cur_amount}")
                    update_params = (cur_amount, buy_amount, row['idx'])
                    update_query = "UPDATE al_trade set cur_amount = %s, buy_amont = %s WHERE idx=%s"
                    await database_instance.execute(update_query, update_params)

                    profit_loss = ((cur_amount+ buy_amount) / init_amount) * 100
                    result_params = (idx, symbol, trade_type, sell_amount, cur_price, profit_loss)


                # 판매 조건 true and 기존거래 존재
                elif two_negative and  buy_amount:
                    # 판매 로직 구현
                    trade_type = 2
                    cur_price = await requestCurprice(symbol)
                    cur_amount = row["cur_amount"]

                    sell_amount: float = (buy_amount // cur_price) * cur_price
                    cur_amount = cur_amount + sell_amount
                    main_logger.info(f"Sell amount : {sell_amount} || cur_amount : {cur_amount}")
                    update_query = "UPDATE al_trade set cur_amount = %s, buy_amount = %s WHERE idx=%s"
                    update_params = (cur_amount, None, row['idx'])
                    await database_instance.execute(update_query, update_params)
                    profit_loss = (cur_amount / init_amount) * 100

                    result_params = (idx, symbol, trade_type, sell_amount, cur_price, profit_loss)

                else:
                    trade_type = 3.
                    profit_loss = (cur_amount / init_amount) * 100
                    result_params = (idx, symbol, trade_type, None, None, profit_loss)
                    logger.info("Stay Condition")


                result_query = "INSERT INTO al_trade_result (trade_idx, symbol,trade_type, trade_amount, trade_price,profit_loss)" \
                               "values (%s, %s,%s,%s,%s,%s)"
                await database_instance.execute(result_query, params=result_params)
                main_logger.info(f"Execute al_id1 : {idx}")




        return rows
    except Exception as e:
        logger.error("Failed to fetch all al_trade rows: %s", e)
        raise

async def request2FMP():
    logger.info("request2FMP is excuted")
    query = "SELECT * FROM stocker.al_trade WHERE activation is true AND al_id=1"
    volume_query = "SELECT vol_date, change_ratio FROM stock_volume WHERE symbol=%s AND vol_date <= %s ORDER BY vol_date DESC LIMIT 5"
    current_time = datetime.now()

    try:
        cur_date: str = str(current_time.date())
        rows :list = await database_instance.fetch_rows(query)

        for row in rows:
            symbol : str = row["symbol"]
            params = (symbol, cur_date,)
            # print(params)
            vol_data = await database_instance.fetch_rows(volume_query,params=params)

            current_time = datetime.now()

            # Subtract one day
            one_day_ago = current_time - timedelta(days=1)
            ten_day_ago = current_time - timedelta(days=10)
            start_data = one_day_ago.date()
            logger.info("start date : %s", start_data)
            end_date = ten_day_ago.date()
            logger.info("end date : %s", end_date)
            url = f"https://financialmodelingprep.com/api/v3/historical-price-full/{symbol}?apikey={api_key}&from={end_date}&to={start_data}"

            # print(url2)
            response = requests.get(url)

            if response.status_code == 200:
                api_data = response.json()

            else:
                logger.error("FMP Request Error:", response.status_code)

            # print(vol_data)
            if not vol_data:
                await saveVolume(api_data)
                logger.info("save volume")
            else:
                # Get the current datetime
                date_list = [d["vol_date"] for d in vol_data]
                for d in api_data["historical"]:
                    if d['date'] in date_list:
                        pass
                    else:
                        await saveVolume(api_data)


        return rows
    except Exception as e:
        logger.error("Failed to fetch all al_trade rows: %s", e)
        raise

# 거래 조건 성립 시 API 요청하여 현재가격으로 매매
async def requestCurprice(symbol : str) -> float:
    current_price_url = f"https://financialmodelingprep.com/api/v3/quote/{symbol}?apikey={api_key}"

    try:

        response = requests.get(current_price_url)

        if response.status_code == 200:
            api_data = response.json()

            current_price = api_data[0]["price"]

            logger.info(f"current_price is : {current_price}")
            return current_price
        else:
            main_logger.error(f"Error get current price with status code : {response.status_code}")
    except requests.exceptions.Timeout:
            main_logger.error(f"Request timed out for symbol: {symbol}")
    except requests.exceptions.ConnectionError:
        main_logger.error(f"Connection error occurred for symbol: {symbol}")
    except requests.exceptions.HTTPError as http_err:
        main_logger.error(f"HTTP error occurred for symbol: {symbol} - {http_err}")
    except requests.exceptions.RequestException as req_err:
        main_logger.error(f"Request error occurred for symbol: {symbol} - {req_err}")
    except Exception as e:
        main_logger.error(f"An unexpected error occurred for symbol: {symbol} - {e}")


async def saveVolume(api_data : list):
    symbol = api_data["symbol"]
    historic_data = api_data["historical"]
    # query = "INSERT INTO stock_volume (symbol, volume, vol_date, change_ratio) VALUES (%s, %s, %s, %s)"
    # Query with ON DUPLICATE KEY UPDATE
    query = """
        INSERT INTO stock_volume (symbol, volume, vol_date, change_ratio)
        VALUES (%s, %s, %s, %s)
        ON DUPLICATE KEY UPDATE
            volume = VALUES(volume),
            change_ratio = VALUES(change_ratio),
            updated_at = CURRENT_TIMESTAMP
        """
    for i in range(0, 5):
        target = historic_data[i]
        before = historic_data[i+1]
        vol_date = target["date"]
        volume = target["volume"]
        change_ratio: float = round(((volume - before["volume"]) / volume * 100),2)
        params = (symbol, volume, vol_date, change_ratio)

        try:
            await database_instance.execute(query, params=params)
            main_logger.info(f"Inserted: {symbol} - {vol_date}")
        except Exception as e:
            logger.error(e)
