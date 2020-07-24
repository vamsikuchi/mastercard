package com.mastercard.travel.pojo;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ConnectionRequestParams {

    @NonNull
    private String origin;

    @NonNull
    private String city;

    public ConnectionRequestParams() {
    }

    @NonNull
    public String getOrigin() {
        return origin;
    }

    public void setOrigin(@NonNull String origin) {
        this.origin = origin;
    }

    @NonNull
    public String getCity() {
        return city;
    }

    public void setCity(@NonNull String city) {
        this.city = city;
    }
}
