package com.aixrein.siinformer;

/**
 * Created by aixre on 21.11.2017.
 */

public class Work {
    private long id;
    private String WorkName;
    private String WorkLink;
    private int WorkType;
    private String WorkDescription;
    private String AuthorID;
    private String WorkDigest;
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

    // WorkName Operations
    public String getWorkName() {
        return WorkName;
    }

    public void setWorkName(String WorkName) {
        this.WorkName = WorkName;
    }

    // WorkLink Operations
    public String getWorkLink() {
        return WorkLink;
    }

    public void setWorkLink(String WorkLink) {
        this.WorkLink = WorkLink;
    }

    // WorkType Operations
    public int getWorkType() {
        return WorkType;
    }

    public void setWorkType(int WorkType) {
        this.WorkType = WorkType;
    }

    // WorkDescription Operations
    public String getWorkDescription() {
        return WorkDescription;
    }

    public void setWorkDescription(String WorkDescription) {
        this.WorkDescription = WorkDescription;
    }

    // AuthorID Operations
    public String getAuthorID() {
        return AuthorID;
    }

    public void setAuthorID(String AuthorID) {
        this.AuthorID = AuthorID;
    }

    // WorkDigest Operations
    public String getWorkDigest() {
        return WorkDigest;
    }

    public void setWorkDigest(String WorkDigest) {
        this.WorkDigest = WorkDigest;
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
        return WorkName;
    }
}
