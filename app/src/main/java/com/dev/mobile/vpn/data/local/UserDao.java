package com.dev.mobile.vpn.data.local;

import com.dev.mobile.vpn.data.model.UserResponse;
import com.dev.mobile.vpn.util.Constant;

import io.paperdb.Paper;

import static io.paperdb.Paper.book;

public class UserDao {

    private static UserDao userDao = null;
    public static final  String USER_KEY = "user_key";

    public UserDao() {
    }


    public static UserDao getInstance(){
        if (userDao == null){
            userDao = new UserDao();
        }
        return  userDao;
    }

    public void saveUser(UserResponse u){
        book(Constant.DB_NAME).write(USER_KEY,u);
    }

    public UserResponse getUser(){
        return  book(Constant.DB_NAME).read(USER_KEY,null);
    }

    public void dropUser() { Paper.book(Constant.DB_NAME).delete(USER_KEY);}

}
