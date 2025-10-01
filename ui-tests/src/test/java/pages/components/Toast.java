package pages.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

public class Toast {
    private final Page page;

    public Toast(Page page) {
        this.page = page;
    }

    private Locator lastToast() {
        return page.locator("li[role='status']").last();
    }

    public Toast waitAppear(int timeoutMs) {
        lastToast().waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(timeoutMs));
        return this;
    }

    public String titleText() {
        Locator title = lastToast().locator("div.text-sm.font-semibold").first();
        if (title.count() > 0) return title.innerText().trim();
        return "";
    }

    public String bodyText() {
        List<String> texts = lastToast().locator("div.text-sm").allInnerTexts();
        StringBuilder body = new StringBuilder();
        for (String t : texts) {
            String s = t.trim();
            if (s.isEmpty()) continue;
            if ("Ошибка входа".equalsIgnoreCase(s) || "Ошибка регистрации".equalsIgnoreCase(s) || "Код отправлен".equalsIgnoreCase(s)) {
                continue;
            }
            if (body.length() > 0) body.append(" ");
            body.append(s);
        }
        return body.toString().trim();
    }

    public boolean containsAny(String... needles) {
        String t = (titleText() + " " + bodyText()).toLowerCase();
        for (String n : needles) if (t.contains(n.toLowerCase())) return true;
        return false;
    }
}