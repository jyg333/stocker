#!/usr/bin/env python
import os
from dotenv import load_dotenv

load_dotenv()
api_key = os.getenv("fmp_api")
try:
    # For Python 3.0 and later
    from urllib.request import urlopen
except ImportError:
    # Fall back to Python 2's urllib2
    from urllib2 import urlopen

import certifi
import json

def get_jsonparsed_data(url):
    response = urlopen(url, cafile=certifi.where())
    data = response.read().decode("utf-8")
    return json.loads(data)

url = (f"https://financialmodelingprep.com/api/v3/historical-price-full/AAPL?apikey={api_key}&from=2024-12-01&to=2024-12-05")
# url = (f"https://financialmodelingprep.com/api/v3/historical-price-full/AAPL?apikey=mhT6cNwBql2KcSZSnhMlrS1RbSwPTKsD&from=2024-12-02&to=2024-12-05")

# import requests
#
# url = "https://financialmodelingprep.com/api/v3/historical-price-full/AAPL"
# params = {
#     "from": "2023-01-01",
#     "to": "2023-12-31",
#     "limit": 50
# }
# response = requests.get(url, params=params)
#
# if response.status_code == 200:
#     data = response.json()
#     print(data)
# else:
#     print("Error:", response.status_code)

print(get_jsonparsed_data(url))
