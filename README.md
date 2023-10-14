## Содержание
[Задания](#задания)
- [Задание 1](#задание-1)
- [Задание 2](#задание-2)
- [Задание 3](#задание-3)
- [Задание 4](#задание-4)

[Требования](#требования)
- [Стек](#стек)
- [Прочее](#прочее)

## Задания

### Задание 1
Используя сервис https://reqres.in/ получить список пользователей со второй страницы.
Убедится что  имена файлов аватаров пользователей уникальны


### Задание 2
Используя сервис https://reqres.in/ протестировать авторизацию пользователя в системе.
Необходимо создание двух тестов на успешный логин и логин с ошибкой из-за не введённого пароля

### Задание 3
Используя сервис https://reqres.in/ убедится что операция LIST <RESOURCE> возвращает
данные отсортированные по годам

### Задание 4
Используя сервис https://gateway.autodns.com/ убедиться, что количество тегов равно 14.


## Требования
### Стек
Java, testNG, restAssured.
### Прочее
- [x] Помник параметризован
- [x] Хотя бы в одном тесте используются спецификации
- [x] В тестах [1](#задание-1), [2](#задание-2) и [3](#задание-3) используются дата-классы
- [ ] Разбивка на степы по желанию
- [ ] Каждый тест должен проверять условия, согласно заданию, следовательно содержать ассерт
- [ ] Если тест содержит параметры, тест должен быть параметризован. Допустима параметризация
стандартными средствами TestNG: датапровайдором, либо xml-файлом
- [ ] Пожалуйста внимательно читайте задание!
- [ ] Пожалуйста проверяйте свои проверки! Тест должен корректно проверять функционал, при
любом ответе сервера! Условие не соблюдается - ассерт выдаёт ошибку
