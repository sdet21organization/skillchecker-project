package pages.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class Toast {
    private final Page page;

    // Локаторы вынесены в поля
    private final Locator rootOpen;
    private final Locator title;
    private final Locator body;
    private final Locator closeBtn;

    public Toast(Page page) {
        this.page = page;
        this.rootOpen = page.locator("li[role='status'][data-state='open']").first();
        this.title    = rootOpen.locator(".text-sm.font-semibold").first();
        this.body     = rootOpen.locator(".text-sm.opacity-90").first();
        this.closeBtn = rootOpen.locator("button[toast-close]").first();
    }

    /** Ждём открытый toast */
    public Toast waitOpen() {
        page.waitForSelector("li[role='status'][data-state='open']");
        return this;
    }

    public String titleText() {
        return title.innerText().trim();
    }

    public String bodyText() {
        return body.innerText().trim();
    }

    public void closeIfPresent() {
        if (closeBtn.isVisible()) {
            closeBtn.click();
        }
    }
}