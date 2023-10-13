package in.reqres;

import com.google.common.collect.Comparators;
import in.reqres.dto.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiTests {
    @Test
    public void checkUniqueAvatarFileNames() {
        ResourceUserData resource =
                given()
                        .contentType("application/json")
                        .when()
                        .get("https://reqres.in/api/users?page=2")
                        .then()
                        .statusCode(200)
                        .extract().body().as(ResourceUserData.class);
        Set<String> uniqueAvatarFileNames = resource.getData().stream()
                .map(UserData::getAvatar)
                .map(ava -> ava.replaceAll("^.+/|\\..+", ""))
                .collect(Collectors.toSet());
        Assert.assertEquals(resource.getData().size(), uniqueAvatarFileNames.size(),
                "Количество объектов списка должно совпадать с количеством уникальных объектов этого же списка");
    }

    @Test
    public void avatarFileNamesNotUnique() {
        ResourceUserData resource =
                given()
                        .when()
                        .get("https://reqres.in/api/users?page=2")
                        .then()
                        .statusCode(200)
                        .extract().body().as(ResourceUserData.class);
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
        LogIn login = new LogIn("eve.holt@reqres.in", "cityslicka");
        given()
                .contentType("application/json")
                .body(login)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .assertThat().statusCode(200).and().body("token", not(emptyOrNullString()));
    }

    @Test
    public void unsuccessfulAuthorization() {
        LogIn login = new LogIn("eve.holt@reqres.in", "");
        given()
                .contentType("application/json")
                .body(login)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .assertThat().statusCode(400).and().body("error", not(emptyOrNullString()));
    }

    @Test
    public void resourceSortedByYears() {
        List<ColorStyle> colorStyles =
                given()
                        .when()
                        .get("https://reqres.in/api/unknown")
                        .then()
                        .assertThat().statusCode(200).and().body("data.year", not(hasItem(nullValue())))
                        .extract().body().as(ResourceColorStyle.class).getData();
        boolean isInOrder = Comparators.isInOrder(colorStyles, Comparator.comparingLong(ColorStyle::getYear));
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
