package com.example.firstapp;

public class SearchResult {
    private String title;
    private String category;
    private String telePhone;
    private String link;
    private String address;
    private int mapx;
    private int mapy;
    SearchResult (){}
    SearchResult (String title, String category, String telePhone, String link, String address, String mapx, String mapy) {
        this.title = title;
        this.category = category.equals("") ? "미분류" : category;
        this.telePhone = telePhone.equals("") ? "전화번호 없음" : telePhone;
        this.link = link.equals("") ? "링크 없음" : link;
        this.address = address.equals("") ? "주소 없음" : address;
        this.mapx = Integer.parseInt(mapx);
        this.mapy = Integer.parseInt(mapy);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTelePhone() {
        return telePhone;
    }

    public void setTelePhone(String telePhone) {
        this.telePhone = telePhone;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMapx() {
        return mapx;
    }

    public void setMapx(int mapx) {
        this.mapx = mapx;
    }

    public int getMapy() {
        return mapy;
    }

    public void setMapy(int mapy) {
        this.mapy = mapy;
    }
}
