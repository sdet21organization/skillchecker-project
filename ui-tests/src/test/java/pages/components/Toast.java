package pages.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/** Мини-обёртка над toast-уведомлением */
public class Toast {
    private final Page page;

    public Toast(Page page) {
        this.page = page;
    }

    /** Ждём пока откроется любой toast */
    public Toast waitOpen() {
        page.waitForSelector("li[role='status'][data-state='open']");
        return this;
    }

    public String titleText() {
        return page.locator("li[role='status'][data-state='open'] .text-sm.font-semibold")
                .first().innerText().trim();
    }

    public String bodyText() {
        return page.locator("li[role='status'][data-state='open'] .text-sm.opacity-90")
                .first().innerText().trim();
    }

    public void closeIfPresent() {
        Locator closeBtn = page.locator("li[role='status'][data-state='open'] button[toast-close]").first();
        if (closeBtn.isVisible()) closeBtn.click();
    }
}