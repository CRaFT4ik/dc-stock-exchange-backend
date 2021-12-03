# Stock exchange 
## Distributed computing 2021

This is the backend part of the whole project, which works with the Spring framework. Android app you can [find here](https://github.com/CRaFT4ik/dc-stock-exchange-android). 

### Task
Electronic stock exchange. Available operations:
- put a lot for sale at the price `Psl`
- register request to buy at the price of `Ppr`
- get a list of offers
- to make a deal (sell or buy)

Transactions are performed if there are offers with `Psl` <= `Ppr`