package org.ponking.gih.server.config;

/**
 * @Author ponking
 * @Date 2021/5/4 13:25
 */
public class CardMessage {

    private String title;

    private String description;

    private String url;

    private String btntxt;

    public CardMessage() {
    }

    public CardMessage(String title, String description, String url, String btntxt) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.btntxt = btntxt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getBtntxt() {
        return btntxt;
    }

    public void setBtntxt(String btntxt) {
        this.btntxt = btntxt;
    }
}
