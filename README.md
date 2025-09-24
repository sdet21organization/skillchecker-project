Maven Tests with Allure - GitHub Actions Workflow
Этот workflow автоматизирует запуск тестов для API и UI с использованием Maven, 
собирает результаты с помощью Allure и публикует отчёты на GitHub Pages.

🔹 Workflow триггеры
Workflow запускается при:
Push в ветки:
main
feature/** (любая feature-ветка)
Pull Request на ветку main

🔹 Jobs
1. API Tests (api-tests)
Использует Ubuntu и Java 21 (Temurin)
Кеширует Maven зависимости (~/.m2/repository) для ускорения сборки
Запускает тесты:
mvn clean test -f api-tests/pom.xml -Dmaven.test.failure.ignore=true
Загружает результаты тестов в артефакт allure-results-api
2. UI Tests (ui-tests)
Аналогично API-тестам:
Maven + Java 21
Кеш Maven зависимостей
Запуск UI тестов:
mvn clean test -f ui-tests/pom.xml -Dmaven.test.failure.ignore=true
Загружает артефакт allure-results-ui
3. Allure Report (allure-report)
Выполняется после api-tests и ui-tests
Шаги:
Скачивание артефактов (allure-results-api и allure-results-ui)
Объединение результатов в одну папку
Восстановление истории предыдущих запусков Allure
Генерация отчёта с использованием simple-elf/allure-report-action
Сохранение истории текущей ветки для последующих запусков
Публикация отчёта на GitHub Pages
URL отчёта:
https://<owner>.github.io/<repo>/<branch>/
Комментарий в Pull Request
Автоматически добавляет ссылку на отчёт Allure в PR

🔹 Особенности и рекомендации
История Allure
Поддерживается сохранение истории результатов для каждой ветки
Имена веток с / преобразуются в - для совместимости с файловой системой и URL
Maven тесты не останавливают workflow
Параметр -Dmaven.test.failure.ignore=true позволяет собрать все результаты, даже если есть падения тестов
Кэширование Maven
Кэшируются только зависимости в ~/.m2/repository
Ключ кэша формируется по хэшам всех pom.xml файлов
Совместимость с ветками
Workflow корректно работает с feature-ветками и main
Отчёты публикуются в отдельные директории на GitHub Pages, соответствующие имени ветки
