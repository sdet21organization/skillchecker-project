package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import context.TestContext;
import tests.BaseUiTest;

public class RegistrationPage {
    private final Page page;
    private final Locator registerButton;
    private final Locator modalRoot;
    private final Locator fullNameInput;
    private final Locator emailInput;
    private final Locator organizationInput;
    private final Locator passwordInput;
    private final Locator submitButton;

    public final Locator emailError;
    public final Locator nameError;
    public final Locator orgError;
    public final Locator passwordError;

    public RegistrationPage(TestContext context) {
        this.page = context.page;
        this.registerButton = page.locator("button[aria-haspopup='dialog']");
        this.modalRoot = page.locator("div[role='dialog']");
        this.fullNameInput     = page.locator("div[role='dialog'] input[name='fullName']");
        this.emailInput        = page.locator("div[role='dialog'] input[name='email']");
        this.organizationInput = page.locator("div[role='dialog'] input[name='organizationName']");
        this.passwordInput     = page.locator("div[role='dialog'] input[name='password']");
        this.submitButton      = page.locator("div[role='dialog'] button[type='submit']");
        this.nameError     = page.locator("div[role='dialog'] input[name='fullName'] ~ p.text-xs.text-red-500");
        this.emailError    = page.locator("div[role='dialog'] input[name='email'] ~ p.text-xs.text-red-500");
        this.orgError      = page.locator("div[role='dialog'] input[name='organizationName'] ~ p.text-xs.text-red-500");
        this.passwordError = page.locator("div[role='dialog'] input[name='password'] ~ p.text-xs.text-red-500");
    }

    public RegistrationPage open() {
        registerButton.first().click();
        modalRoot.waitFor();
        return this;
    }

    public boolean isModalOpen() {
        return modalRoot.isVisible();
    }

    public RegistrationPage setFullName(String fullName) {
        fullNameInput.fill(fullName);
        return this;
    }

    public RegistrationPage setEmail(String email) {
        emailInput.fill(email);
        return this;
    }

    public RegistrationPage setOrganization(String organization) {
        organizationInput.fill(organization);
        return this;
    }

    public RegistrationPage setOrganizationAndBlur(String organization) {
        organizationInput.fill(organization);
        organizationInput.press("Tab");
        return this;
    }

    public RegistrationPage setPassword(String password) {
        passwordInput.fill(password);
        return this;
    }

    public RegistrationPage submit() {
        submitButton.click();
        return this;
    }

    public Locator modalRoot() {
        return modalRoot;
    }

    public Locator emailInput() {
        return emailInput;
    }

    public Locator orgInput() {
        return organizationInput;
    }

    public RegistrationPage fillDefaults(String email) {
        return setFullName(BaseUiTest.DEF_NAME)
                .setEmail(email)
                .setOrganization(BaseUiTest.DEF_ORG)
                .setPassword(BaseUiTest.DEF_PWD);
    }

    public RegistrationPage fillDefaultsWithoutPassword(String email) {
        return setFullName(BaseUiTest.DEF_NAME)
                .setEmail(email)
                .setOrganization(BaseUiTest.DEF_ORG);
    }

    public RegistrationPage fillNameEmail(String email) {
        return setFullName(BaseUiTest.DEF_NAME)
                .setEmail(email);
    }

    public RegistrationPage fillNameEmailPassword(String email) {
        return setFullName(BaseUiTest.DEF_NAME)
                .setEmail(email)
                .setPassword(BaseUiTest.DEF_PWD);
    }

    public boolean looksInvalid(Locator input, Locator inlineError) {
        try {
            inlineError.waitFor(new Locator.WaitForOptions().setTimeout(1500));
            if (inlineError.isVisible()) return true;
        } catch (RuntimeException ignored) { }
        String aria = input.getAttribute("aria-invalid");
        if ("true".equalsIgnoreCase(aria)) return true;
        String cls = input.getAttribute("class");
        if (cls == null) cls = "";
        if (cls.contains("border-red") || cls.contains("ring-red") || cls.contains("outline-red")) return true;
        return false;
    }

    public boolean isCodeModalOpen() {
        return page.locator("div[role='dialog'] h2")
                .filter(new Locator.FilterOptions().setHasText("Подтверждение email"))
                .isVisible();
    }

    public RegistrationPage enterVerificationCode(String code) {
        Locator dialog = page.locator("div[role='dialog']");
        Locator sixInputs = dialog.locator("input").filter(new Locator.FilterOptions().setHasText("")).locator("[inputmode='numeric'],[maxlength='1']");
        if (sixInputs.count() >= 6 && code.length() >= 6) {
            for (int i = 0; i < 6; i++) {
                sixInputs.nth(i).fill(String.valueOf(code.charAt(i)));
            }
            return this;
        }
        Locator single = dialog.locator("input[name='code'], input[autocomplete='one-time-code'], input[type='tel']").first();
        if (single.count() > 0) {
            single.fill(code);
        }
        return this;
    }

    public RegistrationPage confirmVerification() {
        Locator dialog = page.locator("div[role='dialog']");
        Locator btn = dialog.locator("button:has-text('Подтвердить'), button[type='submit']").last();
        btn.click();
        return this;
    }
}