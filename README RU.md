# 🏆 Проект автоматизации тестирования Skillchecker.tech

## 📌 Общие сведения
Проект разделён на два независимых модуля:
- **api-tests** — автоматизация тестирования REST API  
- **ui-tests** — автоматизация тестирования пользовательского интерфейса (UI)  

Каждый модуль имеет свой `pom.xml` и конфигурацию.

---

## 💻 Системные требования

- [Java JDK 21](https://adoptium.net)  
- [Apache Maven 3.9+](https://maven.apache.org)  
- Git  
- [Allure Commandline](https://docs.qameta.io/allure/)  
- Браузеры: Chromium, Firefox, WebKit (через Playwright)  

⚠️ **Важно:**
после клонирования проекта необходимо загрузить Maven scripts
для этого необходимо просто нажать кнопку на всплывающем сообщение справа снизу
<img width="467" height="207" alt="maven scripts" src="https://github.com/user-attachments/assets/b6d899a9-5ba2-4f9a-9d55-bd6695c8529d" />

---

## ⚙️ Программные зависимости (могут быть изменены)

### 📡 API-тесты (`api-tests`)
- JUnit 5.13.4  
- Rest-Assured 5.5.5  
- Allure 2.28.1 (`allure-junit5`, `allure-rest-assured`)  
- AspectJ 1.9.24  
- Jackson Databind 2.19.0  
- Lombok 1.18.30  
- JavaFaker 1.0.2  
- SLF4J (simple) 2.0.17  
- Rest-Assured JSON Schema Validator 5.5.5  

### 🎭 UI-тесты (`ui-tests`)
- JUnit 5.13.4  
- Playwright 1.54.0  
- Allure 2.28.1 (`allure-junit5`)  
- AspectJ 1.9.24  
- SLF4J (simple) 2.0.17
  
---

## 📂 Структура проекта
```text
api-tests
└── src
    └── test
        ├── java
        │   ├── dto # DTO-классы для работы с API авторизации
        │   ├── helpers # Вспомогательные утилиты *
        │   ├── tests # Набор тестов API
        │   └── wrappers # Обёртки для RestAssured и сервисов
        └── resources
            ├── schemas # JSON-схемы для валидации ответов
            ├── tpl # Шаблоны для запросов и ответов API
            ├── allure.properties 
            ├── config.properties
            └── junit-platform.properties

ui-tests
└── src
    └── test
        ├── java
        │   ├── context # Контекст
        │   ├── pages # Page Object Model **
        │   ├── tests # Набор UI-тестов
        │   └── utils # Утилитарные классы ***
        └── resources
            ├── allure.properties
            ├── config.properties
            └── junit-platform.properties
```
<br>
* в этой папке также есть класс **GetAuthCookie** для получения куки авторизации и переиспользования ее в API тестах,<br>
  для использования куки необходимо передавать параметр cookie при написании запроса (см. пример ниже)<br>
[Посмотреть изображение](https://github.com/user-attachments/assets/fe6430dd-97b8-414c-83af-4c8614e8e4b1)<br>
** в этой папке отдельно сохраняем **страници** ->> [НазваниеПейджи]Page и **локаторы** ->> [НазваниеПейджи]PageElements<br>
*** в этой папке есть класс **GetAuthCookie** для получения куки авторизации и переиспользования ее в UI тестах<br>
  
---

## 🚀 Запуск тестов и генерация отчета
### API-тесты
`mvn clean test -pl api-tests` 
### UI-тесты
`mvn clean test -pl ui-tests`
### API и UI комбо
`mvn -f api-tests clean test && mvn -f ui-tests clean test`

можно также использовать более привычные команды: 
преходить в папку 'cd [название паки]'
и запускать тесты 'mvn clean test' 

### Генерация отчета 
📄Сгенерировать отчет Allure:
`mvn allure:generate`

🌐 Открыть отчет в браузере:
`mvn allure:serve`

можно также переходить в папу target и генерировать и заускать отчеты из нее 
`cd target`
`allure geenerate` 
`allure open`

---

## Стандарты аннотаций в автотестах
Использовать аннотации Allure для улучшения отчетности и визуализации в Allure Report.

| Аннотация | Назначение | Пример использования |
|-----------|------------|-------------------|
| `@Epic` | Основная функциональность или модуль, к которому относится тест | `@Epic("Авторизация")` |
| `@Owner` | Ответственный за тест | `@Owner("Test Testovich")` |
| `@DisplayName` | Читаемое название теста | `@DisplayName("Проверка успешного входа пользователя")` |
| `@Step` | Шаг теста с описанием действий | `@Step("Ввод логина: {username}")` |

---

## 🛠 Пожелания по работе с Git при написании автотестов
При работе с автотестами необходимо строго соблюдать следующие правила работы с Git:

### 1. Ветки
- Каждый новый автотест или изменение должно выполняться в **отдельной ветке**.
- **Имя ветки** должно соответствовать **номеру задачи** (например: `SS-123`).
> Пример: `git checkout -b SS-123`
###  2. Коммиты
- не стоит забывать о добавлении сообщения к коммиту в котором указать кратко какие изменеия были выполнены  
### 3. Pull Request (PR)
- Все изменения **добавляются через Pull Request**.
- PR должен быть **одобрен хотя бы одним участником команды** перед слиянием.
    
> 🔄 Процесс:  
> 1. Создать ветку с номером задачи  
> 2. Сделать коммиты  
> 3. Открыть Pull Request  
> 4. Получить одобрение коллеги (для получения одобрения можно написать в чат #skillchecker или в ЛС)  
> 5. Слить изменения в main/master
⚠️ **Важно:**  Не мержить ветку напрямую в main/master без Pull Request и одобрения коллеги.
