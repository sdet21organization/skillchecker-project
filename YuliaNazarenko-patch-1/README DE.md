# 🏆 Projekt zur Testautomatisierung von Skillchecker.tech

## 📌 Allgemeine Informationen
Dieses Projekt ist in zwei unabhängige Module unterteilt:
- **api-tests** — Automatisierung der REST-API-Tests  
- **ui-tests** — Automatisierung der Benutzeroberflächen-Tests (UI)  

Jedes Modul hat seine eigene `pom.xml` und Konfiguration.

---

## 💻 Systemanforderungen

- [Java JDK 21](https://adoptium.net)  
- [Apache Maven 3.9+](https://maven.apache.org)  
- Git  
- [Allure Commandline](https://docs.qameta.io/allure/)  
- Browser: Chromium, Firefox, WebKit (über Playwright)  

⚠️ **Wichtig:**  
Nach dem Klonen des Projekts müssen die Maven-Skripte geladen werden.  
Klicken Sie dazu einfach auf die Schaltfläche in der unteren rechten Ecke der Popup-Nachricht:  
<img width="467" height="207" alt="maven scripts" src="https://github.com/user-attachments/assets/b6d899a9-5ba2-4f9a-9d55-bd6695c8529d" />

---

## ⚙️ Abhängigkeiten (können angepasst werden)

### 📡 API-Tests (`api-tests`)
- JUnit 5.13.4  
- Rest-Assured 5.5.5  
- Allure 2.28.1 (`allure-junit5`, `allure-rest-assured`)  
- AspectJ 1.9.24  
- Jackson Databind 2.19.0  
- Lombok 1.18.30  
- JavaFaker 1.0.2  
- SLF4J (simple) 2.0.17  
- Rest-Assured JSON Schema Validator 5.5.5  

### 🎭 UI-Tests (`ui-tests`)
- JUnit 5.13.4  
- Playwright 1.54.0  
- Allure 2.28.1 (`allure-junit5`)  
- AspectJ 1.9.24  
- SLF4J (simple) 2.0.17

---

## 📂 Projektstruktur

**api-tests**  
```
src
└── test
    ├── java
    │   ├── dto        # DTO-Klassen für API-Authentifizierung
    │   ├── helpers    # Hilfsklassen *
    │   ├── tests      # API-Testfälle
    │   └── wrappers   # Wrapper für RestAssured und Services
    └── resources
        ├── schemas        # JSON-Schemas zur Validierung von Antworten
        ├── tpl            # Vorlagen für API-Anfragen und -Antworten
        ├── allure.properties
        ├── config.properties
        └── junit-platform.properties
```

**ui-tests**  
```
src
└── test
    ├── java
    │   ├── context   # Testkontext
    │   ├── pages     # Page Object Model **
    │   ├── tests     # UI-Testfälle
    │   └── utils     # Hilfsklassen ***
    └── resources
        ├── allure.properties
        ├── config.properties
        └── junit-platform.properties
```

<br>
* In diesem Ordner befindet sich auch die Klasse **GetAuthCookie**, um Authentifizierungs-Cookies zu erhalten und in API-Tests wiederzuverwenden.  
Zur Verwendung des Cookies muss der Parameter `cookie` bei der Anfrage übergeben werden (siehe Beispiel):  
(https://github.com/user-attachments/assets/fe6430dd-97b8-414c-83af-4c8614e8e4b1)
<br>
** In `pages` werden **Seitenklassen** ->> `[PageName]Page` und **Locatoren** ->> `[PageName]PageElements` separat gespeichert. 
<br> 
*** In `utils` gibt es ebenfalls die Klasse **GetAuthCookie** für die Wiederverwendung in UI-Tests.

---

## 🚀 Testausführung und Berichtsgenerierung

### API-Tests
```bash
mvn clean test -f api-tests/pom.xml
```

### UI-Tests
```bash
mvn clean test -f ui-tests/pom.xml
```

### Kombination von API- und UI-Tests
```bash
mvn clean test -f api-tests/pom.xml; mvn clean test -f ui-tests/pom.xml```


Alternativ in den Ordner wechseln: `cd [Ordnername]`  
und Tests ausführen: `mvn clean test`

### Berichtserstellung

📄 Allure-Bericht generieren:
```bash
mvn allure:report
```

🌐 Bericht im Browser öffnen:
```bash
mvn allure:serve
```

Alternativ können Sie in den `target`-Ordner wechseln und dort generieren und öffnen:
```bash
cd target
allure generate
allure open
```

---

## Standards für Annotationen in automatisierten Tests
Verwenden Sie Allure-Annotationen, um die Berichterstattung und Visualisierung zu verbessern.

| Annotation     | Zweck                                       | Beispiel                                        |
|----------------|--------------------------------------------|------------------------------------------------|
| `@Epic`        | Hauptfunktionalität oder Modul des Tests  | `@Epic("Authentifizierung")`                   |
| `@Owner`       | Verantwortliche Person für den Test       | `@Owner("Test Testovich")`                     |
| `@DisplayName` | Lesbarer Name des Tests                    | `@DisplayName("Überprüfung erfolgreicher Login")` |
| `@Step`        | Testschritt mit Beschreibung der Aktion   | `@Step("Eingabe Benutzername: {username}")`    |

---

## 🛠 Git-Empfehlungen beim Schreiben von Tests

### 1. Branches
- Jede neue Funktion oder Änderung erfolgt in einem **eigenen Branch**.  
- **Branch-Name** entspricht der Aufgaben-ID, z. B.: `SS-123`  
> Beispiel: `git checkout -b SS-123`

### 2. Commits
- Fügen Sie eine aussagekräftige Commit-Nachricht hinzu, die die Änderungen beschreibt.

### 3. Pull Request (PR)
- Alle Änderungen über **Pull Requests** einreichen.  
- PR muss **mindestens von einem Teammitglied und Teamleiter genehmigt** werden, bevor er gemergt wird.

> 🔄 Prozess:  
> 1. Branch mit Aufgaben-ID erstellen  
> 2. Commits durchführen  
> 3. Pull Request öffnen  
> 4. Genehmigung eines Kollegen einholen (#skillchecker Chat oder direkt)  
> 5. Änderungen in main/master mergen  
> ⚠️ **Wichtig:** Nie direkt in main/master mergen ohne Pull Request und Genehmigung.

