package com.dinilbositha.smartcity360.dto;

public class AnnouncementDTO {
    private Long id;
    private String icon;
    private String title;
    private String body;
    private String publishedAt;

    public AnnouncementDTO() {}

    public AnnouncementDTO(Long id, String icon, String title, String body, String publishedAt) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.body = body;
        this.publishedAt = publishedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
}
