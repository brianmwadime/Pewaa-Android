package com.fortunekidew.pewaad.models.wishlists;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class EditWishlist {
    private String name;
    private String id;
    private String description;
    private String recipient;
    private String category;


    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecipients() {
        return recipient;
    }

    public void setRecipients(String recipients) {
        this.recipient = recipients;
    }

}
