package com.getparkit.parkit.Classes;


import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.OffsetDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserAccess implements Parcelable {


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(userId);
        out.writeDouble(ttl);
        out.writeString(accessToken);
        out.writeString(refreshToken);
        out.writeString(currentRole);
        out.writeString(at_created.toString());
        if (roles != null) {
            out.writeString(TextUtils.join(",", roles));
        } else {
            out.writeString("");
        }
    }

    public static final Parcelable.Creator<UserAccess> CREATOR
            = new Parcelable.Creator<UserAccess>() {
        public UserAccess createFromParcel(Parcel in) {
            return new UserAccess(in);
        }

        public UserAccess[] newArray(int size) {
            return new UserAccess[size];
        }
    };

    private UserAccess(Parcel in) {
        userId = in.readDouble();
        ttl = in.readDouble();
        accessToken = in.readString();
        refreshToken = in.readString();
        currentRole = in.readString();
        at_created = OffsetDateTime.parse(in.readString());
        roles = Arrays.asList(TextUtils.split(in.readString(),","));

    }

    public UserAccess() {

    }

    private Integer id;
    private Double userId, ttl;
    private String accessToken, refreshToken, currentRole;
    private OffsetDateTime at_created;
    private List<String> roles;

    // Set UserID
    public void setId(Integer id) {
        this.id = id;
    }

    // Get UserID
    public Integer getId() {
        return this.id;
    }

    // below: define setters and getters
    // Set UserID
    public void setUserId(Double userId) {
        this.userId = userId;
    }

    // Get UserID
    public Double getUserId() {
        return this.userId;
    }

    // Set TTL
    public void setTtl(Double ttl) {
        this.ttl = ttl;
    }

    // Get TTL
    public Double getTtl() {
        return this.ttl;
    }


    // Set Access Token
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    //  Get Access Token
    public String getAccessToken() {
        return this.accessToken;
    }

    // Set Refresh Token
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    //  Get Refresh Token
    public String getRefreshToken() {
        return this.refreshToken;
    }

    // Set TTL
    public void setCreated(OffsetDateTime at_created) {
        this.at_created = at_created;
    }

    // Get TTL
    public OffsetDateTime getCreated() {
        return this.at_created;
    }

    // Set Role
    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }

    //  Get Role
    public String getCurrentRole() {
        return this.currentRole;
    }

    // Set Role
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    //  Get Role
    public List<String> getRoles() {
        return this.roles;
    }

    public UserAccess refreshToken(String refreshToken) {
        return this;
    }

    public UserAccess (JSONObject jObj) throws JSONException {
        try {
            this.accessToken = new JSONObject(jObj.get("accessToken").toString()).get("id").toString();
            this.ttl = Double.valueOf(new JSONObject(jObj.get("accessToken").toString()).get("ttl").toString());
            this.at_created = OffsetDateTime.parse(new JSONObject(jObj.get("accessToken").toString()).get("created").toString());
            this.refreshToken = new JSONObject(jObj.get("refreshToken").toString()).get("id").toString();
            this.userId = Double.valueOf(new JSONObject(jObj.get("user").toString()).get("id").toString());
            this.roles = new ArrayList<>();
            this.currentRole = "";

            if (jObj.has("currentRole")) {
                this.currentRole = String.valueOf(jObj.get("currentRole"));
            }

            JSONArray roleArray = new JSONArray(jObj.get("roles").toString());

            if (roleArray != null) {
                for (int i=0;i<roleArray.length();i++){
                    this.roles.add(roleArray.getString(i));
                }
            }
            Log.d("","");
        } catch (JSONException e) {
            e.printStackTrace();
            throw e;
        }
    }

}
