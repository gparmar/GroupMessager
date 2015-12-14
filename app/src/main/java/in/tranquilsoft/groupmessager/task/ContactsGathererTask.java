package in.tranquilsoft.groupmessager.task;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

import in.tranquilsoft.groupmessager.model.Phone;
import in.tranquilsoft.groupmessager.model.impl.Contact;
import in.tranquilsoft.groupmessager.util.Constants;
import in.tranquilsoft.groupmessager.util.Utility;

/**
 * Created by gurdevp on 06/12/15.
 */
public class ContactsGathererTask extends AsyncTask<Void, Integer, Void> {
    private Context context;
    static String TAG = "ContactsGathererTask";
    ProgressDialog dialog;

    public ContactsGathererTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setTitle("Collecting all contacts. Please wait...");
        dialog.setIndeterminate(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        fetchAndSaveContacts(context, context.getContentResolver());

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.PROPERTY_CONTACTS_COLLECTED, true);
        editor.commit();

        new AlertDialog.Builder(context).setTitle("Welcome!")
                .setMessage("Thanks for using GroupMessager.\n\n" +
                        "To get started you need to create contact groups.\n\n" +
                        "For that please click on the + button on the bottom.\n\n" +
                        "Happy Group Messaging!!!")
                .setPositiveButton("OK",null)
                .show();
    }

    //
//    @Override
//    protected void onProgressUpdate(Integer... values) {
//        super.onProgressUpdate(values);
//    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = (values[0] / values[1]) * 100;
        dialog.setProgress(progress);
    }

    public static void fetchAndSaveContacts(Context context,
                                            ContentResolver contentResolver) {
        try {
            final ArrayList<Contact> contactList = new ArrayList<>();

            Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, null, null, null);
            //Log.d(TAG, "phone cursor count:" + phoneCursor.getCount());
            // Loop for every contact in the phone
            if (phoneCursor != null && phoneCursor.getCount() > 0) {
                Log.d(TAG, "Current position:" + phoneCursor.getPosition());
                int count = 0;

                try {
                    while (phoneCursor.moveToNext()) {
                        Phone phone = null;

                        try {
                            phone = new Phone(phoneCursor);
                        } catch (Exception e) {
                            continue;
                        }
                        //If phone is not a mobile then go to the next phone number
                        if (Utility.isEmpty(phone.getPhoneNumber())) {
                            continue;
                        }
                        Contact contact = new Contact().getByPhone(context, phone.getPhoneNumber());

                        boolean newPhone = false;
                        //If we have the same phone number in another contact then we ignore it.
                        if (contact != null) {
                            //Do nothing
                        } else {
                            newPhone = true;
                        }

                        contact = createOrUpdateContact(context, contentResolver, contact, phone, newPhone);
                        if (contact != null)
                            contactList.add(contact);

                    }
                    phoneCursor.close();
                } catch (Exception e) {
                    Log.e(TAG, "Exception while iterating over the phone cursor.");
                }


                // save the contacts
                for (Contact contact : contactList) {
                    if (contact.isNewContact()) {
                        contact.create(context);
                    } else if (contact.isDirty()){
                        contact.update(context);
                    }
                }
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception while initializing ContactDao, EmailDao or PhoneDao", e);
        }
    }

    public static Contact createOrUpdateContact(Context context, ContentResolver contentResolver,
                                                Contact contactFromDb, Phone phone, boolean phoneNewlyCreated) {
        Cursor contactCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " = ?",
                new String[]{phone.getPhoneContactId() + ""}, null);
        //Log.d(TAG, "for phone:" + phone.getPhoneNumber() + " contact cursor count:" + contactCursor.getCount());
        try {
            if (phoneNewlyCreated) {
                //create a new contact
                if (contactCursor.moveToNext()) {
                    String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //String profilePicUri = String.valueOf(contactCursor.getLong(contactCursor.getColumnIndex(ContactsContract.Contacts._ID)));

                    // check if this pic is valid
//                    if (StringUtils.isNotEmpty(profilePicUri)) {
//                        if (!isValidPhotoUrl(context, contentResolver, profilePicUri)) {
//                            profilePicUri = null;
//                        }
//                    }

                    // Query and loop for every email of the contact
//                    Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                            new String[]{phone.getPhoneContactId() + ""}, null);

//                    List<String> emailsCollected = new ArrayList<>();
//                    StringBuilder emailsSB = new StringBuilder("");
//                    int count = 0;
//                    boolean isMe = false;
//                    while (emailCursor.moveToNext()) {
//                        String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                        if (emailsCollected.size() == 0) {
//
//                            emailsSB.append(email);
//                            emailsCollected.add(email.toLowerCase());
//                        } else if (emailsCollected.size() > 0 && !emailsCollected.contains(email)) {
//                            emailsSB.append(",");
//
//
//                            emailsSB.append(email);
//                            emailsCollected.add(email.toLowerCase());
//                        }
//                    }
//                    emailCursor.close();

                    //Create a new contact
                    Contact contact = new Contact();
                    contact.setNewContact(true);
                    contact.setName(name);
                    contact.setPhone(phone.getPhoneNumber());
//                    contact.setProfilePicture(profilePicUri);
//                    contact.setEmails(emailsSB.toString());
//                    contact.setIsMe(isMe);
//                    contact.setDateCreated(System.currentTimeMillis());
//                    contact.setDateModified(System.currentTimeMillis());
                    return contact;
                }
            } else {
                //Update the existing contact with new contact details and emails, if any.
                //boolean contactChanged = false;
                while (contactCursor.moveToNext()) {
                    String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                    String profilePicUri = String.valueOf(contactCursor.getLong(contactCursor.getColumnIndex(ContactsContract.Contacts._ID)));
//
//                    // check if this pic is valid
//                    if (StringUtils.isEmpty(contactFromDb.getProfilePicture()) && StringUtils.isNotEmpty(profilePicUri)) {
//                        if (!isValidPhotoUrl(context, contentResolver, profilePicUri)) {
//                            profilePicUri = null;
//                        }
//                    }

                    if (Utility.isNotEmpty(contactFromDb.getName())) {
                        if (!contactFromDb.getName().equals(name)) {

//                            String aliases = contactFromDb.getAliases();
//                            if (StringUtils.isNotEmpty(aliases)) {
//                                String newAliases = dedupAndAdd(aliases, name);
//                                if (StringUtils.isNotEmpty(newAliases)) {
                            contactFromDb.setName(name);
                            contactFromDb.setDirty(true);

                        } else {
                            //Do nothing
                        }
                    } else {
                        contactFromDb.setName(name);
                        contactFromDb.setDirty(true);
                    }

                    //A non-null profile picture uri will be a valid photo url
//                    if (profilePicUri != null) {
//                        if (StringUtils.isEmpty(contactFromDb.getProfilePicture())) {
//                            contactFromDb.setProfilePicture(profilePicUri);
//                            contactChanged = true;
//                        }
//                    }
//                    // Query and loop for every email of the contact
//                    Cursor emailCursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                            new String[]{phone.getPhoneContactId() + ""}, null);
//
//                    List<String> existingEmails = new ArrayList<>();
//                    if (StringUtils.isNotEmpty(contactFromDb.getEmails())) {
//                        String emailArr[] = contactFromDb.getEmails().split(",");
//                        for (int i = 0; i < emailArr.length; i++) {
//                            if (StringUtils.isNotEmpty(emailArr[i])) {
//                                existingEmails.add(emailArr[i].toLowerCase());
//                            }
//                        }
//                    }
//
//                    StringBuilder emailsSB = new StringBuilder("");
//
//                    int count = 0;
//                    while (emailCursor.moveToNext()) {
//                        String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                        if (existingEmails.size() > 0 && !existingEmails.contains(email.toLowerCase())) {
//                            if (count == 0) {
//                                count++;
//                            } else {
//                                emailsSB.append(",");
//                            }
//
//                            emailsSB.append(email);
//                        }
//
//                    }
//
//                    if (StringUtils.isNotEmpty(emailsSB.toString())) {
//                        if (StringUtils.isNotEmpty(contactFromDb.getEmails())) {
//                            contactFromDb.setEmails(contactFromDb.getEmails() + "," + emailsSB.toString());
//                            contactChanged = true;
//                        } else {
//                            contactFromDb.setEmails(emailsSB.toString());
//                            contactChanged = true;
//                        }
//                    }
//                    emailCursor.close();
                }
                if (contactFromDb.isDirty()) {
                    //contactFromDb.setDateModified(System.currentTimeMillis());
                    return contactFromDb;
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            contactCursor.close();
        }

        return null;
    }
}
