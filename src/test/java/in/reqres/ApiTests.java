package in.reqres;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Comparators;
import in.reqres.dto.ColorStyle;
import in.reqres.dto.LogIn;
import in.reqres.dto.Resource;
import in.reqres.dto.UserData;
import io.qameta.allure.Step;
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

    @Test
    public void checkUniqueAvatarFileNames() {
        JavaType type = TypeFactory.defaultInstance()
                .constructParametricType(Resource.class, UserData.class);
        Resource<UserData> resource =
                given()
                        .spec(requestSpec())
                        .when()
                        .get("api/users?page=2")
                        .then()
                        .spec(responseSpec())
                        .extract().body().as(type);

        Set<String> uniqueAvatarFileNames = resource.getData().stream()
                .map(UserData::getAvatar)
                .map(ava -> ava.replaceAll("^.+/|\\..+", ""))
                .collect(Collectors.toSet());
        Assert.assertEquals(resource.getData().size(), uniqueAvatarFileNames.size(),
                "Количество объектов списка должно совпадать с количеством уникальных объектов этого же списка");
    }

    @Test
    public void avatarFileNamesNotUnique() {
        installSpec(requestSpec(), responseSpec());
        JavaType type = TypeFactory.defaultInstance()
                .constructParametricType(Resource.class, UserData.class);

        Resource<UserData> resource =
                given()
                        .when()
                        .get("api/users?page=2")
                        .then()
                        .extract().body().as(type);

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

    @Test
    public void successfulAuthorization() {
        installSpec(requestSpec(), responseSpec());
        LogIn login = new LogIn("eve.holt@reqres.in", "cityslicka");
        given()
                .body(login)
                .when()
                .post("api/login")
                .then()
                .assertThat().body("token", not(emptyOrNullString()));
    }

    @Test
    public void unsuccessfulAuthorization() {
        installSpec(requestSpec());
        LogIn login = new LogIn("eve.holt@reqres.in", "");
        given()
                .body(login)
                .when()
                .post("api/login")
                .then()
                .assertThat().statusCode(400).and().body("error", not(emptyOrNullString()));
    }
@Step
    @Test
    public void resourceSortedByYears() {
        installSpec(requestSpec(), responseSpec());
        JavaType type = TypeFactory.defaultInstance()
                .constructParametricType(Resource.class, ColorStyle.class);
        Resource<ColorStyle> resource =
                given()
                        .when()
                        .get("api/unknown")
                        .then()
                        .assertThat().body("data.year", not(hasItem(nullValue())))
                        .extract().body().as(type);
        boolean isInOrder = Comparators.isInOrder(resource.getData(), Comparator.comparingLong(ColorStyle::getYear));
        Assert.assertTrue(isInOrder);
    }

    @Test
    public void checkTagCount() {
        int expectedTagCount = 14;
        String xml =
                given()
                        .when()
                        .get("https://gateway.autodns.com/")
                        .then()
                        .statusCode(200)
                        .extract().body().asString();
        Pattern tagPattern = Pattern.compile("<\\w+>");
        Matcher matcher = tagPattern.matcher(xml);
        List<String> tags = matcher.results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
        Assert.assertEquals(tags.size(), expectedTagCount,
                "Ожидалось " + expectedTagCount + " тегов, а реально " + tags.size());
    }

}
