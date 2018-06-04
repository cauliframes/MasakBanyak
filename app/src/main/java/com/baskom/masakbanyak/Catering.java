package com.baskom.masakbanyak;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Castor on 4/5/2018.
 */

public class Catering implements Serializable{
    @SerializedName("_id")
    private String catering_id;
    @SerializedName("name")
    private String name;
    @SerializedName("address")
    private String address;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("ratings")
    private ArrayList<Rating> ratings;
    @SerializedName("avatar")
    private String avatar;

    public Catering(String catering_id, String name, String address, String phone, String email, ArrayList<Rating> ratings, String avatar) {
        this.catering_id = catering_id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.ratings = ratings;
        this.avatar = avatar;
    }

    public String getCatering_id() {
        return catering_id;
    }

    public void setCatering_id(String catering_id) {
        this.catering_id = catering_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(ArrayList<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
