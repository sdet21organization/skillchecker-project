package tests.managetests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TestsPage;
import tests.BaseTest;

@DisplayName("Создание теста через UI")
public class CreateTestUiTests extends BaseTest {

    @Test
    @DisplayName("Открытие страницы 'Тесты'")
    void openTestsPage() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();
    }

    @Test
    @DisplayName("Кнопка 'Создать тест' отображается")
    void createTestButtonIsVisible() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyCreateTestButtonIsVisible();
    }

    @Test
    @DisplayName("Модальное окно 'Создать тест' открывается после клика")
    void createTestModalAppearsAfterClick() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.openTestsPage();
        testsPage.verifyCreateTestButtonIsVisible();
        testsPage.clickCreateTestButton();
        testsPage.verifyCreateTestModalIsVisible();
    }

    @Test
    @DisplayName("Успешное создание теста с уникальным именем")
    void createTest() {
        TestsPage testsPage = new TestsPage(context);
        testsPage.clickCreateTestButton();
        testsPage.verifyCreateTestModalIsVisible();

        String testName = "Autotest_" + System.currentTimeMillis();

        testsPage.enterTestName(testName);
        testsPage.enterTestDescription("description");
        testsPage.submitCreateTest();

        testsPage.waitTestCreatedAndHeaderIs(testName);
    }

    @Test
    @DisplayName("Создание теста с пустым именем показывает ошибку")
    void createTestWithEmptyName() {
        TestsPage testsPage = new TestsPage(context);

        testsPage.openTestsPage();
        testsPage.verifyTestsPageIsOpened();
        testsPage.clickCreateTestButton();
        testsPage.verifyCreateTestModalIsVisible();

        testsPage.enterTestName("");
        testsPage.enterTestDescription("description");
        testsPage.submitCreateTest();
        testsPage.verifyEmptyNameErrorVisible();
    }
}
