package database;

import utils.Date;

public abstract class TimelineObject<T> implements DBObject<T> {
    int ownerID;
    Date startDate = new Date();
    Date endDate = new Date();
    Date creationDate;

    public abstract int getID();

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getDescription();

    public abstract void setDescription(String name);
}
