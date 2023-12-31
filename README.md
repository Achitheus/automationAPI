## Содержание
- [Build](#build)
- [Allure отчет](#allure-отчет)
- [Тест-кейсы](#тест-кейсы)
- [Требования](#требования)
  - [Стек](#стек)
  - [Прочее](#прочее)

## Build
В окружении должна быть установлена (и прописана в PATH) java не ниже 11 версии
и добавлена переменная среды JAVA_HOME.
#### Запуск:

    ./mvnw verify

#### Посмотреть отчет:
В первый раз:

    ./mvnw allure:install
    ./mvnw allure:serve
В последующие разы:

    ./mvnw allure:serve

## Allure отчет
Ознакомиться с отчетом можно перейдя по
[этой ссылке](https://achitheus.github.io/automationAPI/).  
По каждому тесту отчеты выглядят примерно так:
<p align="center">
<img src="markdown-resources/allure-report.gif" width="400" alt="report-gif">
</p>

## Тест-кейсы

#### № 1
Используя сервис [reqres.in](https://reqres.in/) получить список пользователей со второй страницы.
Убедится что  имена файлов аватаров пользователей уникальны


#### № 2
Используя сервис [reqres.in](https://reqres.in/) протестировать авторизацию пользователя в системе.
Необходимо создание двух тестов на успешный логин и логин с ошибкой из-за не введённого пароля

#### № 3
Используя сервис [reqres.in](https://reqres.in/) убедится что операция LIST <RESOURCE> возвращает
данные отсортированные по годам

#### № 4
Используя сервис [gateway.autodns.com](https://gateway.autodns.com/) убедиться, что количество тегов равно 14.


## Требования
### Стек
Java, testNG, restAssured.
### Прочее
- [x] Помник параметризован
- [x] Хотя бы в одном тесте используются спецификации
- [x] В тестах [1](#-1), [2](#-2) и [3](#-3) используются дата-классы
- [x] Разбивка на степы по желанию
- [x] Каждый тест должен проверять условия, согласно заданию, следовательно содержать ассерт
- [x] Если тест содержит параметры, тест должен быть параметризован. Допустима параметризация
стандартными средствами TestNG: датапровайдором, либо xml-файлом
- [x] Пожалуйста внимательно читайте задание!
- [x] Пожалуйста проверяйте свои проверки! Тест должен корректно проверять функционал, при
любом ответе сервера! Условие не соблюдается - ассерт выдаёт ошибку
