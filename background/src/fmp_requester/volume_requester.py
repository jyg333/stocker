#!/usr/bin/env python
import os
from dotenv import load_dotenv

load_dotenv()
api_key = os.getenv("fmp_api")

import requests
start_data = "2024-12-01"
end_date = "2024-12-05"
url = f"https://financialmodelingprep.com/api/v3/historical-price-full/AAPL?apikey={api_key}&from={start_data}&to={end_date}"
response = requests.get(url)


if response.status_code == 200:
    data = response.json()
    print(data)
else:
    print("Error:", response.status_code)

# print(get_jsonparsed_data(url))
