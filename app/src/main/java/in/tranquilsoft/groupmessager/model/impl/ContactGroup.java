package in.tranquilsoft.groupmessager.model.impl;

import in.tranquilsoft.groupmessager.model.AbstractContactGroup;

public class ContactGroup extends AbstractContactGroup {
    static String TAG = "ContactGroup";
    int contactsCount = 0;

    public int getContactsCount() {
        return contactsCount;
    }

    public void setContactsCount(int contactsCount) {
        this.contactsCount = contactsCount;
    }

    @Override
    public String toString() {
        return "ContactGroup{" +
                "contactsCount=" + contactsCount +
                '}';
    }
}