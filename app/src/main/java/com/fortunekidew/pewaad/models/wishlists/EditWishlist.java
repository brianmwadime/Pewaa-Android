package com.fortunekidew.pewaad.models.wishlists;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class EditWishlist {
    private String name;
    private String id;
    private String description;
    private String recipient;
    private String permissions;
    private String category;
    private Boolean flagged;
    private String flagged_description;

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public Boolean getFlagged() {
        return flagged;
    }

    public String getFlagged_description() {
        return flagged_description;
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

    public void setFlagged_description(String flagged_description) {
        this.flagged_description = flagged_description;
    }

    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
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
