package in.reqres.dto;

import java.util.List;

public class ResourceColorStyle extends Resource{
    private List<ColorStyle> data;

    public List<ColorStyle> getData() {
        return data;
    }

    public void setData(List<ColorStyle> data) {
        this.data = data;
    }
}
