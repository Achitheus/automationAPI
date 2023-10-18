package in.reqres.helpers;

import io.qameta.allure.Step;

public class Assert {
    @Step("Проверяю, что нет ошибки: \"{message}\"")
    public static void assertEquals(int actural, Integer expected, String message) {
        org.testng.Assert.assertEquals(actural, expected, message);
    }

    @Step("Проверяю, что нет ошибки: \"{message}\"")
    public static void assertTrue(boolean condition, String message) {
        org.testng.Assert.assertTrue(condition, message);
    }

    @Step("Проверяю, что нет ошибки: \"{message}\"")
    public static void assertFalse(boolean condition, String message) {
        org.testng.Assert.assertFalse(condition, message);
    }
}
