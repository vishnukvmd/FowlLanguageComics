package tech.vishnu.fowllaguagecomics;

import com.google.gson.annotations.SerializedName;

public class Comic {
    @SerializedName("id") public final int id;
    @SerializedName("flc_id") public final int flcId;
    @SerializedName("title") public final String title;
    @SerializedName("url") public final String url;
    @SerializedName("img_comic_url") public final String imageUrl;
    @SerializedName("img_bonus_url") public final String bonusPanelUrl;
    @SerializedName("keywords") public final String keywords;
    @SerializedName("date") public final String date;

    public Comic(int id, int flcId, String title, String url, String imageUrl, String bonusPanelUrl,
                 String keywords, String date) {
        this.id = id;
        this.flcId = flcId;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.bonusPanelUrl = bonusPanelUrl;
        this.keywords = keywords;
        this.date = date;
    }
}
