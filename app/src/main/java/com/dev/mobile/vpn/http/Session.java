package com.dev.mobile.vpn.http;

import com.dev.mobile.vpn.data.model.UserResponse;

public interface Session {
    boolean isLoggedIn();

    void saveUser(UserResponse userResponse);

    UserResponse getUser();

    String getToken();

    void invalidate();

}
