package in.reqres;

import com.google.common.collect.Comparators;
import in.reqres.dto.ColorStyle;
import in.reqres.dto.LogIn;
import in.reqres.dto.Resource;
import in.reqres.dto.UserData;
import in.reqres.helpers.TestDataProvider;
import io.restassured.common.mapper.TypeRef;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static in.reqres.specifications.Specification.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiTests {
    @BeforeMethod
    public void beforeEach() {
        deleteSpec();
    }

    /**
     * Используя сервис <a href="https://reqres.in/">reqres.in</a> получает список пользователей
     * со страницы {@code pageNumber} и
     * убеждается, что имена файлов аватаров пользователей уникальны.
     */
    @Test(dataProvider = "page_numbers", dataProviderClass = TestDataProvider.class)
    public void checkUniqueAvatarFileNames(Integer pageNumber) {
        Resource<UserData> resource =
                given()
                        .spec(requestSpec())
                        .when()
                        .get("api/users?page=" + pageNumber)
                        .then()
                        .spec(responseSpec())
                        .extract().body().as(new TypeRef<>() {
                        });

        Set<String> uniqueAvatarFileNames = resource.getData().stream()
                .map(UserData::getAvatar)
                .map(ava -> ava.replaceAll("^.+/|\\..+", ""))
                .collect(Collectors.toSet());
        Assert.assertEquals(resource.getData().size(), uniqueAvatarFileNames.size(),
                "(стр. №" + pageNumber + ") Количество объектов списка должно совпадать" +
                        " с количеством уникальных объектов этого же списка");
    }

    /**
     * Убеждается в том, что тест {@link #checkUniqueAvatarFileNames(Integer)} работает корректно
     * и падает, если присутствует хотя бы один повтор какого-либо имени файла.
     */
    @Test
    public void notUniqueAvatarFileNamesDetecting() {
        installSpec(requestSpec(), responseSpec());

        Resource<UserData> resource =
                given()
                        .when()
                        .get("api/users?page=1")
                        .then()
                        .extract().body().as(new TypeRef<>() {
                        });

        List<UserData> notUniqueList = resource.getData();
        Assert.assertTrue(notUniqueList.size() > 1,
                "Чтобы проверять объекты на (не)уникальность, их должно быть больше 1");
        notUniqueList.get(0).setAvatar(notUniqueList.get(notUniqueList.size() - 1).getAvatar());
        Set<String> uniqueAvatarFileNames = notUniqueList.stream()
                .map(UserData::getAvatar)
                .map(avatar -> avatar.replaceAll("^.+/|\\..+", ""))
                .collect(Collectors.toSet());
        Assert.assertTrue(notUniqueList.size() > uniqueAvatarFileNames.size(),
                "Уникальных объектов должно быть меньше их общего количества");
    }

    /**
     * Используя сервис <a href="https://reqres.in/">reqres.in</a> тестирует
     * авторизацию пользователя в системе на успешный логин данными из
     * {@code logIn}.
     */
    @Test(dataProvider = "correct_log_ins", dataProviderClass = TestDataProvider.class)
    public void successfulAuthorization(LogIn logIn) {
        installSpec(requestSpec(), responseSpec());
        given()
                .body(logIn)
                .when()
                .post("api/login")
                .then()
                .assertThat().body("token", not(emptyOrNullString()));
    }

    /**
     * Используя сервис <a href="https://reqres.in/">reqres.in</a> тестирует авторизацию пользователя в системе
     * на логин с ошибкой из-за не введённого пароля.
     */
    @Test
    public void unsuccessfulAuthorization() {
        installSpec(requestSpec());
        given()
                .body(new LogIn("eve.holt@reqres.in", ""))
                .when()
                .post("api/login")
                .then()
                .assertThat().statusCode(400)
                .and().body("error", not(emptyOrNullString()));
    }

    /**
     * Используя сервис <a href="https://reqres.in/">reqres.in</a> убеждается,
     * что операция LIST<RESOURCE> возвращает данные отсортированные по годам.
     */
    @Test
    public void resourceSortedByYears() {
        installSpec(requestSpec(), responseSpec());

        Resource<ColorStyle> resource =
                given()
                        .when()
                        .get("api/unknown")
                        .then()
                        .assertThat().body("data.year", not(hasItem(nullValue())))
                        .extract().body().as(new TypeRef<>() {
                        });
        boolean isInOrder = Comparators.isInOrder(resource.getData(), Comparator.comparingLong(ColorStyle::getYear));
        Assert.assertTrue(isInOrder, "Элементы данных должны быть упорядочены по возрастанию");
    }

    /**
     * Используя сервис <a href="https://gateway.autodns.com/">gateway.autodns.com</a> убедиться,
     * что xml-ресурс {@code resourcePath} содержит количество тегов равное {@code  tagCount}.
     */
    @Test(dataProvider = "xml_tag_count_checker", dataProviderClass = TestDataProvider.class)
    public void checkTagCount(String resourcePath, Integer tagCount) {
        String xml =
                given()
                        .when()
                        .get("https://gateway.autodns.com/" + resourcePath)
                        .then()
                        .statusCode(200)
                        .extract().body().asString();
        Pattern tagPattern = Pattern.compile("<[^!?/][^<]+>");
        Matcher matcher = tagPattern.matcher(xml);
        List<String> tags = matcher.results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
        Assert.assertEquals(tags.size(), tagCount,
                "Ожидалось " + tagCount + " тегов, а реально " + tags.size());
    }

}
