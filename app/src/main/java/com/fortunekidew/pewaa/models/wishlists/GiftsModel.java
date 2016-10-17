package com.fortunekidew.pewaa.models.wishlists;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mwakima on 10/6/16.
 */

public class GiftsModel extends RealmObject {
    @PrimaryKey
    private String id;

    private String name;

    private float price;

    private String wishlist_id;

    private  String description;

    private  String avatar;

    private String code;

    private Date created_on;

    private Date updated_on;
}
