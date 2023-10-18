package in.reqres.helpers;

import in.reqres.dto.LogIn;
import org.testng.annotations.DataProvider;

public class TestDataProvider {
    @DataProvider(name = "page_numbers")
    public Object[] pageNumbers() {
        return new Object[]{1, 2};
    }

    @DataProvider(name = "xml_tag_count_checker")
    public Object[][] xmlAndTagCountSets() {
        return new Object[][]{{"", 14}};
    }

    @DataProvider(name = "correct_log_ins")
    public Object[] correctLogIns() {
        return new Object[]{new LogIn("eve.holt@reqres.in", "cityslicka")};
    }

    @DataProvider
    public Object[] incorrect_log_ins() {
        return new Object[]{new LogIn("eve.holt@reqres.in", "")};
    }
}
