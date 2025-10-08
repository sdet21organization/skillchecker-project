package tests;

import com.smartbear.zephyrscale.junit.annotation.TestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZephyrSanityCheck {

    @Test
    @DisplayName("Простой проверочный тест для интеграции с Zephyr")
    @TestCase(key = "SS-T2") // Используем ключ существующего тест-кейса
    void simpleZephyrCheck() {
        assertTrue(true, "Этот тест должен всегда проходить");
    }
}