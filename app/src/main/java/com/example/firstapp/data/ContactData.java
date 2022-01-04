package com.example.firstapp.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

//ContactData.java

public class ContactData {
    private long portraitSrc, contact_id;
    private String name, phoneNum, description;
    public ContactData(){};
    public ContactData(long portraitSrc, String name, String phoneNum, String description, long contact_id) {
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

    public long getContact_id() {
        return contact_id;
    }

    public void setContact_id(long contact_id) {
        this.contact_id = contact_id;
    }
}
