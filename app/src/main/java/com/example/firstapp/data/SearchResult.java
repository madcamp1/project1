package com.example.firstapp.data;

import com.naver.maps.geometry.LatLng;

public class SearchResult {
    private String title;
    private String category;
    private String telePhone;
    private String link;
    private String address;
    private LatLng coordinate;
    public SearchResult(){}
    SearchResult (String title, String category, String telePhone, String link, String address, LatLng latLng) {
        this.title = title;
        this.category = category.equals("") ? "미분류" : category;
        this.telePhone = telePhone.equals("") ? "전화번호 없음" : telePhone;
        this.link = link.equals("") ? "링크 없음" : link;
        this.address = address.equals("") ? "주소 없음" : modifyAddress(address);
        this.coordinate = latLng;
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
        this.category = category.equals("") ? "미분류" : category;
    }

    public String getTelePhone() {
        return telePhone;
    }

    public void setTelePhone(String telePhone) {
        this.telePhone = telePhone.equals("") ? "전화번호 없음" : telePhone;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link.equals("") ? "링크 없음" : link;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address.equals("") ? "주소 없음" : modifyAddress(address);
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLng coordinate) {
        this.coordinate = coordinate;
    }

    private String modifyAddress (String address) {
        String toReturn = "";
        int contentLength = 15;
        int currentStartIndex = 0;
        while (currentStartIndex < address.length()){
            int endPoint = Math.min(address.length(), currentStartIndex + contentLength);
            toReturn += address.substring(currentStartIndex, endPoint);
            toReturn += "\n";
            if (endPoint == address.length()) break;
            currentStartIndex += contentLength;
        }
        return toReturn.substring(0, toReturn.length()-1);
    }
}
