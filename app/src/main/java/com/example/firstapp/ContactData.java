package com.example.firstapp;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class ContactData {
    private long portraitSrc;
    private String name, phoneNum, description, contact_id;
    public ContactData(){};
    public ContactData(long portraitSrc, String name, String phoneNum, String description, String contact_id) {
        this.portraitSrc = portraitSrc;
        this.name = name;
        this.phoneNum = phoneNum;
        this.description = description;
        this.contact_id = contact_id;
    }

    public long getPortraitSrc() {
        return portraitSrc;
    }

    public void setPortraitSrc(long portraitSrc) {
        this.portraitSrc = portraitSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContact_id() {
        return contact_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }
}
