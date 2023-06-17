# Описание
![LividSelfassuredFairybluebird-size_restricted](https://github.com/13fragment/InvestBot/assets/74821169/1883f2c6-bf4d-4196-8147-ac06519b730f)
<h4>InvestBot - торговый робот, реализующий <b>gRPC</b> интерфейс для взаимодействия с торговой платформой <a href="https://www.tinkoff.ru/invest/">Тинькофф инвестиций</a>, путем <a href="https://github.com/Tinkoff/investAPI/ ">API</a>.</h3>

# Пререквизиты
* Java версии не ниже 11
* Для запуска бота нужно быть клиентом Тинькофф Инвестиций

***
## Описание стратегии
<b>RSI (relative strength index)</b> - индекс относительной силы. Индикатор показывается насколько сильно актив перекуплен или перепродан относительно средних значений.
Если RSI высоко, то это сигнал к продаже актива, если низко - к покупке.

<h4>Формула расчета RSI выглядит так:</h3>

```
RSI = 100 — 100 / (1 + Upeman/Downeman)
```

- <b>Upeman</b> — средний рост цены за период n;
- <b>Downeman</b> — среднее снижение цены за период n.

Классическими являются отсечки 30 и 70 (значение индикатора выше 70 - продавать, ниже 30 - покупать).

<b>При торговле в long:</b>
- Если цена выросла на 15% (значение в конфиге takeProfit) - выставляется рыночная заявка на продажу для фиксирования прибыли
- Если цена упала на 5% (значение в конфиге stopLoss) - выставляется рыночная заявка на продажу для фиксирования убытка

<b>При торговле в short:</b>
- Если цена упала на 15% (значение в конфиге takeProfit) - выставляется рыночная заявка на покупку для фиксирования прибыли
- Если цена выросла на 5% (значение в конфиге stopLoss) - выставляется рыночная заявка на покупку для фиксирования убытка

<h5>Пример работы стратегии:<a href="https://imgur.com/a/b6mpjE2"> клик</a>.</h4>

***

## Запуск и Конфигурация
Для запуска бота необходимо получить токен с полным уровнем доступа.
Для запуска робота следует изменить токен на свой и выбрать аккаунт в конфигурационном файле:

* ```sandbox-token``` - токен для доступа к функционалу песочницы;

* ```sandbox-account``` - аккаунт для торговли в песочнице

Используется встроенная зависимость SpringBoot для запуска локального <b>Tomcat</b> сервера

***

## Функциональные возможности

* Отображение портфеля;
* Отображение открытых ордеров;
* Отображение сведений по ордер ID;
* Пополнение счета в ```RUB```;
* Реализована ```RSI``` стратегия.

***


## Endpoint`ы

1. ```GET:/sandbox/portfolio``` - Получение профиля;
2. ```GET:/sandbox/receipt/{sum}``` - Проверка баланса;
3. ```GET:/sandbox/orders``` - Получение всех открытых заявок;
4. ```GET:/sandbox/orders/{orderId}``` - Получение статуса заявки по ID;
5. ```POST:/sandbox/strategies/rsi``` - Передача параметров для запуска робота.
***


## Пример конфигурации
```json5
[
  {
    "figi": [
      "BBG004TC84Z8",
      "BBG004730ZJ9",
      "BBG004731354",
      "BBG000QJW156",
      "BBG00475KKY8"
    ],
    "upperRsiThreshold": 70,
    "lowerRsiThreshold": 30,
    "takeProfit": 0.15,
    "stopLoss": 0.05,
    "rsiPeriod": 14
  }
]
```
- ```figi``` Идентификатор инструмента
- ```upperRsiThreshold``` Не обязательный параметр. Значение по-умолчанию 70. Верхнее значение RSI, после которого будет дан сигнал на покупку в short
- ```lowerRsiThreshold``` Не обязательный параметр. Значение по-умолчанию 30. Нижнее значение RSI, после которого будет дан сигнал на покупку в long
- ```takeProfit``` Не обязательный параметр. Значение по-умолчанию 0.15 (15%). Значение для take profit
- ```stopLoss``` Не обязательный параметр. Значение по-умолчанию 0.05 (5%). Значение для stop loss
- ```rsiPeriod``` Не обязательный параметр. Значение по-умолчанию 14. Количество последних свечей, по которым будет рассчитан RSI. 
