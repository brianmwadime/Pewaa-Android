package com.fortunekidew.pewaad.helpers;

import com.fortunekidew.pewaad.models.wishlists.WishlistsModel;

import java.util.Comparator;

/**
 * Created by Abderrahim El imame on 6/24/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class StringDateComparatorConversations implements Comparator<WishlistsModel> {

    public int compare(WishlistsModel app1, WishlistsModel app2) {

//        String date1 = app1.getMessageDate();
//        String date2 = app2.getMessageDate();
//        return date2.compareTo(date1);
            return 1;
    }
    /* try {
            Collections.sort(messagesModelList, new StringDateComparatorMessages());
        } catch (Exception e) {
            AppHelper.LogCat("gifts compare " + e.getMessage());
        }*/
}

