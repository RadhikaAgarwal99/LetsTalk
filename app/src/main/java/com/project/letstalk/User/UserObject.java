package com.project.letstalk.User;

public class UserObject {

    private String name, phone, uid;

    public UserObject(String uid, String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.uid = uid;
    }

    public String getUid() { return uid; }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
