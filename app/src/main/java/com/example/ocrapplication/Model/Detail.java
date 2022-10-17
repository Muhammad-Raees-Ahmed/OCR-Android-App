package com.example.ocrapplication.Model;

public class Detail {
    public String id;
    public String name;
    public String videopath;
    public String videourl;
    public long createdDate;

    public Detail() {
        //public no-arg constructor needed
    }

    public Detail(String id, String name, String videopath, String videourl, long createdDate) {
        this.id = id;
        this.name = name;
        this.videopath = videopath;
        this.videourl =videourl;
        this.createdDate = createdDate;
    }

    // getters are the key  in firestore document
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public String getVideopath() {
        return videopath;
    }

    public String getVideourl() {
        return videourl;
    }
}
