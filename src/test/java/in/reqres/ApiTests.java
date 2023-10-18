package in.reqres;

import com.google.common.collect.Comparators;
import in.reqres.dto.ColorStyle;
import in.reqres.dto.LogIn;
import in.reqres.dto.Resource;
import in.reqres.dto.UserData;
import in.reqres.helpers.Assert;
import in.reqres.helpers.TestDataProvider;
import io.restassured.common.mapper.TypeRef;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static in.reqres.helpers.Assert.assertFalse;
import static in.reqres.helpers.Assert.assertTrue;
import static in.reqres.specifications.Specification.*;
import static io.qameta.allure.Allure.addAttachment;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiTests extends BaseTest {

    /**
     * Используя сервис <a href="https://reqres.in/">reqres.in</a> получает список пользователей
     * со страницы {@code pageNumber} и
     * убеждается, что имена файлов аватаров пользователей уникальны.
     */
    @Test(description = "Проверка уникальности имен файлов аватаров пользователей",
            dataProvider = "page_numbers", dataProviderClass = TestDataProvider.class)
    public void checkUniqueAvatarFileNames(Integer pageNumber) {
        Resource<UserData> resource = given()
                .spec(requestSpec())
                .when()
                .get("api/users?page=" + pageNumber)
                .then()
                .spec(responseSpec())
                .extract().body().as(new TypeRef<>() {});
        List<String> avatarFilePaths = resource.getData().stream()
                .map(UserData::getAvatar).collect(Collectors.toList());
        assertFalse(avatarFilePaths.isEmpty(), "Список путей к файлам аватаров пуст");
        addAttachment("Полученные пути к файлам аватаров ("+ avatarFilePaths.size() + " шт.):",
                "text/plain", avatarFilePaths.toString().replaceAll(",", "\n"), ".txt");

        Set<String> uniqueAvatarFileNames = avatarFilePaths.stream()
                .map(ava -> ava.replaceAll("^.+/|\\..+", ""))
                .collect(Collectors.toSet());
        addAttachment("Уникальные названия файлов аватаров ("+ uniqueAvatarFileNames.size() + " шт.):",
                "text/plain", uniqueAvatarFileNames.toString(), ".txt");

        Assert.assertEquals(resource.getData().size(), uniqueAvatarFileNames.size(),
                "В наборе присутствуют не уникальные имена файлов " +
                        "аватаров пользователей  (стр. №" + pageNumber + ")");
    }

    /**
     * Убеждается в том, что тест {@link #checkUniqueAvatarFileNames(Integer)} работает корректно
     * и падает, если присутствует хотя бы один повтор какого-либо имени файла.
     */
    @Test(description = "Проверка проверки. Обнаруживает наличие неуникального назавания файла аватара")
    public void notUniqueAvatarFileNamesDetecting() {
        installSpec(requestSpec(), responseSpec());
        Resource<UserData> resource = given()
                .when()
                .get("api/users?page=1")
                .then()
                .extract().body().as(new TypeRef<>() {});
        final List<UserData> notUniqueList = resource.getData();
        assertFalse(notUniqueList.size() < 2,
                "Число объектов < 2. Проверка на (не)уникальность невозможна");
        step("Помещаю дубликат в список.", () -> {
            notUniqueList.get(0).setAvatar(notUniqueList.get(notUniqueList.size() - 1).getAvatar());
        });
        step("Убеждаюсь, что проверка обнаруживает наличие дубликата .", () -> {
            Set<String> uniqueAvatarFileNames = notUniqueList.stream()
                    .map(UserData::getAvatar)
                    .map(avatar -> avatar.replaceAll("^.+/|\\..+", ""))
                    .collect(Collectors.toSet());
            assertTrue(notUniqueList.size() > uniqueAvatarFileNames.size(),
                    "Проверка не обнаружила заведомо имеющийся дубликат");
        });
    }

    /**
     * Используя сервис <a href="https://reqres.in/">reqres.in</a> тестирует
     * авторизацию пользователя в системе на успешный логин данными из
     * {@code logIn}.
     */
    @Test(dataProvider = "correct_log_ins", dataProviderClass = TestDataProvider.class,
            description = "Проверка авторизации пользователя на успешный логин")
    public void successfulAuthorization(LogIn logIn) {
        installSpec(requestSpec(), responseSpec());
        step("Логинимся корректными данными и получаем токен", () -> {
            given()
                    .body(logIn)
                    .when()
                    .post("api/login")
                    .then()
                    .assertThat().body("token", not(emptyOrNullString()));

        });
    }

    /**
     * Используя сервис <a href="https://reqres.in/">reqres.in</a> тестирует авторизацию пользователя в системе
     * на логин с ошибкой из-за не введённого пароля.
     */
    @Test(dataProvider = "incorrect_log_ins", dataProviderClass = TestDataProvider.class,
            description = "Проверка авторизации пользователя на логин с ошибкой из-за не введенного пароля")
    public void unsuccessfulAuthorization(LogIn incorrectLogIn) {
        installSpec(requestSpec());
        step("Получаем отказ при попытке залогиниться неверными данными", () -> {
            given()
                    .body(incorrectLogIn)
                    .when()
                    .post("api/login")
                    .then()
                    .assertThat().statusCode(400)
                    .and().body("error", not(emptyOrNullString()));
        });
    }

    /**
     * Используя сервис <a href="https://reqres.in/">reqres.in</a> убеждается,
     * что операция LIST<RESOURCE> возвращает данные отсортированные по годам.
     */
    @Test(description = "Проверка, что возвращаемые данные отсортированы по годам")
    public void resourceSortedByYears() {
        installSpec(requestSpec(), responseSpec());
        Resource<ColorStyle> resource =
                given()
                        .when()
                        .get("api/unknown")
                        .then()
                        .assertThat().body("data.year", not(hasItem(nullValue())))
                        .extract().body().as(new TypeRef<>() {});
        List<ColorStyle> colorStyles = resource.getData();
        assertFalse(colorStyles.isEmpty(), "Список элементов данных пуст");
        boolean isInOrder = Comparators.isInOrder(colorStyles,
                Comparator.comparingInt(ColorStyle::getYear));
        assertTrue(isInOrder, "Элементы данных не упорядочены по возрастанию");
    }

    /**
     * Используя сервис <a href="https://gateway.autodns.com/">gateway.autodns.com</a> убедиться,
     * что xml-ресурс {@code resourcePath} содержит количество тегов равное {@code  tagCount}.
     */
    @Test(description = "Проверка XML-тела ответа на количество тегов",
            dataProvider = "xml_tag_count_checker", dataProviderClass = TestDataProvider.class)
    public void checkXmlTagCount(String resourcePath, Integer tagCount) {
        String xml =
                given()
                        .when()
                        .get("https://gateway.autodns.com/" + resourcePath)
                        .then()
                        .statusCode(200)
                        .extract().body().asString();
        assertFalse(xml.isEmpty(), "Полученный XML-файл пуст");
        addAttachment("Полученный xml:", "text/xml", xml, ".xml");
        Matcher matcher = Pattern.compile("<[^!?/][^<]+>").matcher(xml);
        List<String> tags = matcher.results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
        assertFalse(tags.isEmpty(), "Список выделенных RegEx-ом тегов пуст");
        addAttachment("Выделенные из xml теги:", "text/plain", tags.toString(), ".txt");
        Assert.assertEquals(tags.size(), tagCount,
                "Ожидалось " + tagCount + " тегов, а реально " + tags.size());
    }

}
