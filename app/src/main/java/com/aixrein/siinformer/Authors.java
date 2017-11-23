package com.aixrein.siinformer;

/**
 * Created by aixre on 21.11.2017.
 */

public class Authors {
    private long id;
    private String AuthorName;
    private String AuthorLink;
    private String AuthorDescription;
    private int AuthorCategory;
    private String UpdDate;
    private String UpdTime;
    private int isUpdated;

    // ID Operations
    public long getID() {
        return id;
    }

    public void setID(long id) {
        this.id = id;
    }

    // AuthorsName Operations
    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String AuthorsName) {
        this.AuthorName = AuthorsName;
    }

    // AuthorsLink OPerations
    public String getAuthorLink() {
        return AuthorLink;
    }

    public void setAuthorLink(String AuthorLink) {
        this.AuthorLink = AuthorLink;
    }

    // AuthorsDescription Operations
    public String getAuthorsDescription() {
        return AuthorDescription;
    }

    public void setAuthorsDescription(String AuthorDescription) {
        this.AuthorDescription = AuthorDescription;
    }

    // AuthorsDescription Operations
    public int getAuthorCategory() {
        return AuthorCategory;
    }

    public void setAuthorCategory(int AuthorCategory) {
        this.AuthorCategory = AuthorCategory;
    }

    // UpdDate Operations
    public String getUpdDate() {
        return UpdDate;
    }

    public void setUpdDate(String UpdDate) {
        this.UpdDate = UpdDate;
    }

    // UpdTime Operations
    public String getUpdTime() {
        return UpdTime;
    }

    public void setUpdTime(String UpdTime) {
        this.UpdTime = UpdTime;
    }

    public int getIsUpdated() {
        return isUpdated;
    }

    public void setIsUpdated(int isUpdated) {
        this.isUpdated = isUpdated;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return AuthorName;
    }

}
