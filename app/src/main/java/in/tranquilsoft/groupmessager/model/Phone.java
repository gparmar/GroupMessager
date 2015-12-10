package in.tranquilsoft.groupmessager.model;

import android.database.Cursor;
import android.provider.ContactsContract;

import in.tranquilsoft.groupmessager.util.Utility;

/**
 * Created by gurdevp on 06/12/15.
 */
public class Phone {
    public static final String HOME = "Home";
    public static final String WORK = "Work";
    public static final String MOBILE = "Mobile";
    public static final String COMPANY_MAIN = "CompanyMain";
    public static final String WORK_MOBILE = "WorkMobile";

    private String phoneNumber;

    private String type;

    private int isMe = 0;
    boolean synced = false;

    //    @DatabaseField(columnName = "phone_contact_id")
    private int phoneContactId;

    public Phone() {
    }

    public Phone(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Phone(Cursor phoneCursor) {
        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        phoneNumber = Utility.getNormalizedIndianPhoneNumber(phoneNumber);

        if (phoneNumber == null) {
            throw new RuntimeException("Normalized phone was null.");
        }
        int pType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
        switch (pType) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                type = HOME;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                type = WORK;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                type = MOBILE;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                type = WORK_MOBILE;
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                type = COMPANY_MAIN;
                break;
        }
        phoneContactId = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
    }

    public boolean isMobile() {
        if (type != null && !type.isEmpty() && (type.equals(MOBILE) || type.equals(WORK_MOBILE))) {
            return true;
        }
        return false;
    }

    public boolean isPhone() {
        if (type != null && !type.isEmpty() && (type == WORK || type == HOME || type == COMPANY_MAIN)) {
            return true;
        }
        return false;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public boolean isSynced() {
        return synced;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public int getPhoneContactId() {
        return phoneContactId;
    }

    public void setPhoneContactId(int phoneContactId) {
        this.phoneContactId = phoneContactId;
    }


    public int getIsMe() {
        return isMe;
    }

    public void setIsMe(int isMe) {
        this.isMe = isMe;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", type='" + type + '\'' +
                ", isMe=" + isMe +
                ", synced=" + synced +
                ", phoneContactId=" + phoneContactId +
                '}';
    }



}
