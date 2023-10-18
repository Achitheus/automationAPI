package in.reqres;

import io.qameta.allure.restassured.AllureRestAssured;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.Collections;

import static in.reqres.specifications.Specification.deleteSpec;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.filters;
import static io.restassured.RestAssured.replaceFiltersWith;

public class BaseTest {
    @BeforeClass
    public void beforeClass() {
        step("Устанавливаю REST Assured фильтр (для allure)",
                () -> filters(new AllureRestAssured())
        );
    }

    @BeforeMethod
    public void beforeEach() {
        deleteSpec();
    }

    @AfterClass
    public void afterClass() {
        step("Очищаю список REST Assured фильтров",
                () -> replaceFiltersWith(Collections.emptyList())
        );
    }
}
