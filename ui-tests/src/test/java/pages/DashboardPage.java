package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import context.TestContext;
import io.qameta.allure.Step;
import utils.ConfigurationReader;

public class DashboardPage {
    private final TestContext context;

    // ==== Виджеты аналитики (Тест №1)
    public final Locator activeTestsCounter;
    public final Locator candidatesCounter;
    public final Locator pendingSessionsCounter;
    public final Locator completedSessionsCounter;

    // ==== Быстрые действия (Тесты №2 и №3)
    public final Locator createTestButton;
    public final Locator addCandidateButton;

    // ==== Поля в модалках (Тесты №2 и №3)
    public final Locator createTestModalNameInput;   // RU: "Название теста"
    public final Locator addCandidateModalNameInput; // RU: "Имя"

    // ==== Недавняя активность (Тест №4)
    public final Locator recentActivityListContainer;
    public final Locator recentActivityListItems;

    public DashboardPage(TestContext context) {
        this.context = context;

        // Аналитика
        this.activeTestsCounter       = context.page.locator("[data-testid=\"active-tests-card\"] .text-2xl");
        this.candidatesCounter        = context.page.locator("[data-testid=\"total-candidates-card\"] .text-2xl");
        this.pendingSessionsCounter   = context.page.locator("[data-testid=\"pending-sessions-card\"] .text-2xl");
        this.completedSessionsCounter = context.page.locator("[data-testid=\"completed-sessions-card\"] .text-2xl");

        // Быстрые действия
        this.createTestButton   = context.page.locator("[data-testid=\"quick-action-create-test\"]");
        this.addCandidateButton = context.page.locator("[data-testid=\"quick-action-add-candidate\"]");

        // Модалки (по label RU; EN — обработаем фолбэком)
        this.createTestModalNameInput   = context.page.getByLabel("Название теста");
        this.addCandidateModalNameInput = context.page.getByLabel("Имя");

        // Недавняя активность
        this.recentActivityListContainer = context.page.locator("[data-testid=\"recent-activity-card\"]");
        this.recentActivityListItems     = context.page.locator("[data-testid=\"recent-activity-list\"] > div");
    }

    @Step("Открыть дашборд")
    public DashboardPage open() {
        String base = ConfigurationReader.get("URL");
        if (!base.endsWith("/")) base = base + "/";
        context.page.navigate(base + "dashboard");
        return this;
    }

    @Step("Дождаться готовности дашборда (ключевой виджет + NETWORKIDLE)")
    public DashboardPage waitUntilReady() {
        activeTestsCounter.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        context.page.waitForLoadState(LoadState.NETWORKIDLE);
        return this;
    }

    /** Достаём целое число из виджета. */
    public int asInt(Locator counter) {
        String digits = counter.textContent().trim();
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    // ========== НОВОЕ: устойчивые ожидания модалок ==========

    @Step("Открыть модалку 'Создать тест' и дождаться поля ввода названия")
    public Locator openCreateTestModalAndWaitInput() {
        createTestButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        createTestButton.click();

        // 1) сначала пробуем RU-лейбл
        if (createTestModalNameInput.count() > 0) {
            createTestModalNameInput.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(10_000));
            return createTestModalNameInput;
        }
        // 2) фолбэк: EN-лейбл
        Locator en = context.page.getByLabel("Test name");
        if (en.count() > 0) {
            en.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(10_000));
            return en;
        }
        // 3) надёжный фолбэк: первый текстовый инпут в любом видимом диалоге
        Locator dialog = context.page.locator("div[role='dialog']").first();
        dialog.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(10_000));
        Locator anyInput = dialog.locator("input, textarea").first();
        anyInput.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(10_000));
        return anyInput;
    }

    @Step("Открыть модалку 'Добавить кандидата' и дождаться поля ввода имени")
    public Locator openAddCandidateModalAndWaitInput() {
        addCandidateButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        addCandidateButton.click();

        // 1) RU-лейбл
        if (addCandidateModalNameInput.count() > 0) {
            addCandidateModalNameInput.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(10_000));
            return addCandidateModalNameInput;
        }
        // 2) EN-лейбл
        Locator en = context.page.getByLabel("Name");
        if (en.count() > 0) {
            en.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE).setTimeout(10_000));
            return en;
        }
        // 3) общий фолбэк
        Locator dialog = context.page.locator("div[role='dialog']").first();
        dialog.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(10_000));
        Locator anyInput = dialog.locator("input, textarea").first();
        anyInput.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(10_000));
        return anyInput;
    }
}
