# ABC InvestBot
***
## Пререквизиты

* Java версии не ниже 11
* Для запуска бота нужно быть клиентом Тинькофф Инвестиций


***
## Описание 

Реализует <b>gRPC</b> интерфейс для взаимодействия с торговой платформой <a href="https://www.tinkoff.ru/invest/">Тинькофф инвестиций</a>, путем <a href="https://github.com/Tinkoff/investAPI/ ">API</a>.

Тестово покупает акции Сбера, если последняя сделка в стакане дешевле <b>250</b> руб за 1 акцию и продает выше <b>270</b> руб.

***
## Запуск и Конфигурация
Для запуска бота необходим token:
* sandbox-token - токен для доступа к InvestAPI

* sandbox-account - аккаунт для торговли в песочнице

Используется встроенная зависимость SpringBoot для запуска локального <b>Tomcat</b> сервера

***

## Функциональные возможности

* Отображение портфеля
* Покупка/продажа акции по маркету
* Отображение открытых ордеров
* Отображение сведений по ордер ID
* Пополнение счета в ```RUB```

***


## Endpoint`ы
```
1. GET:/sandbox/portfolio - Получение профиля
2. GET:/sandbox/receipt/{sum} - Проверка баланса
3. GET:/sandbox/orders - Получение всех открытых заявок
4. GET:/sandbox/orders/{orderId} - Получение статуса заявки по ID
```
***


## Пример конфигурации
```yaml
app:
  config:
    token: ${TOKEN:t.43M1YGrSecmJYP80pK3Ve0btXZuuKRNul5pwF7HLCCKv4fz-zpE_WPn1ABstEk6-RUu39qNTi4ihTCdMyBjFFw}
    sandbox-mode: ${SANDBOX:true}
    account-id: ${ACCOUNT_ID:78ce85df-2883-4043-a605-6980b0f71c92}
  trading:
    quantity: ${QUANTITY:10}
    figi:
      SBER: ${SBER:BBG004730N88}
```


