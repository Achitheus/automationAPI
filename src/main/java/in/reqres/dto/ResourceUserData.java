package in.reqres.dto;

import java.util.List;

public class ResourceUserData extends Resource {
    private List<UserData> data;

    public ResourceUserData() {
    }

    public ResourceUserData(int page, int per_page, int total, int total_pages, Support support, List<UserData> data) {
        super(page, per_page, total, total_pages, support);
        this.data = data;
    }

    public List<UserData> getData() {
        return data;
    }

    public void setData(List<UserData> data) {
        this.data = data;
    }
}
