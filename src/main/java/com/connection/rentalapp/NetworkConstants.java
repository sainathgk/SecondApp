package com.connection.rentalapp;

/**
 * Created by Sainath on 14-08-2015.
 */
public class NetworkConstants {

    public static final String SAVE_ITEM = "http://192.168.0.151:8080/C2CReach/item/saveItem";
    public static final String GET_ITEMS = "http://192.168.0.151:8080/C2CReach/item/items";
    public static final String GET_ITEM = "http://192.168.0.151:8080/C2CReach/item/getItem/"; //Item id parameter should append

    public static final String SAVE_USER = "http://192.168.0.151:8080/C2CReach/user/saveUser";
    public static final String GET_USER = "http://192.168.0.151:8080/C2CReach/user/getUser/"; //username parameter should append

    public static final String SAVE_USER_ADDRESS = "http://192.168.0.151:8080/C2CReach/user/saveAddress";
}
