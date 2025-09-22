package tests;

import org.junit.jupiter.api.BeforeEach;
import utils.GetAuthCookie;

public class UiAuthBaseTest extends UiBaseTest {
    @BeforeEach
    void addAuthCookie() {
        // Получаем куку и добавляем её в контекст — ЭТО базовый класс для тестов, которым нужна активная сессия.
        context.browserContext.addCookies(GetAuthCookie.getAuthCookie(context));
    }
}