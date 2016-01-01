package in.tranquilsoft.groupmessager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.tranquilsoft.groupmessager.consumer.InsertDBConsumer;
import in.tranquilsoft.groupmessager.consumer.MultiResultSqliteConsumer;
import in.tranquilsoft.groupmessager.consumer.SingleResultSqliteConsumer;
import in.tranquilsoft.groupmessager.consumer.UpdateDBConsumer;
import in.tranquilsoft.groupmessager.model.DefaultEntity;
import in.tranquilsoft.groupmessager.model.impl.Contact;
import in.tranquilsoft.groupmessager.model.impl.ContactGroup;
import in.tranquilsoft.groupmessager.model.impl.GroupContactJunction;
import in.tranquilsoft.groupmessager.task.InsertSqliteTask;
import in.tranquilsoft.groupmessager.task.QueryByIdSqliteTask;
import in.tranquilsoft.groupmessager.task.QueryForAllIdSqliteTask;
import in.tranquilsoft.groupmessager.task.QueryForGrpContactJunctionsByGrpIdSqliteTask;
import in.tranquilsoft.groupmessager.task.SMSSenderTask;
import in.tranquilsoft.groupmessager.task.UpdateSqliteTask;
import in.tranquilsoft.groupmessager.util.Constants;
import in.tranquilsoft.groupmessager.util.Utility;

/**
 * Created by gurdevp on 05/12/15.
 */
public class AddEditGroupActivity extends DefaultActivity implements InsertDBConsumer
        , SingleResultSqliteConsumer, UpdateDBConsumer, MultiResultSqliteConsumer<Contact> {
    public static final String TAG = "AddEditGroupActivity";
    public static final int INSERT_GROUP_REQUEST = 201;
    public static final int QUERY_GROUP_BY_ID_REQUEST = 202;
    public static final int UPDATE_GROUP_REQUEST = 203;
    public static final int QUERY_GROUP_FOR_ALL_CONTACT_REQUEST = 204;
    public static final int QUERY_GROUP_CONTACT_JUNCTIONS_CONTACT_REQUEST = 205;
    EditText groupName;
    LinearLayout container;
    AutoCompleteTextView searchBx;
    ListView contacts;
    List<Contact> allContacts;

    boolean addMode = true;
    long selectedGroupId = -1;
    ContactGroup selectedGroup;
    Map<Long, CheckBox> contactIdVsCheckbox = new HashMap<>();

    boolean addModeBeingConvertedToEditMode = false;
    String selectedPhone = null;
    ProgressDialog dialog;
    List<String> selectedPhones = new ArrayList<>();
    LinearLayout sendMsgLayout;
    EditText sms;
    private ImageView closeSearchIcon;
    private ImageView searchIcon;
    private boolean searchMode;
    int contactsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group);
        String groupNameStr = null;
        try {
            addMode = getIntent().getExtras().getBoolean(Constants.ADD_MODE, true);
            groupNameStr = getIntent().getExtras().getString(Constants.GROUP_NAME, null);
            selectedGroupId = getIntent().getExtras().getLong(Constants.GROUP_ID, -1);
            contactsCount = getIntent().getExtras().getInt(Constants.CONTACT_COUNT, 0);
        } catch (Exception e) {

        }

        groupName = (EditText) findViewById(R.id.groupName);
        container = (LinearLayout) findViewById(R.id.container);
        contacts = (ListView) findViewById(R.id.contacts);
        sendMsgLayout = (LinearLayout) findViewById(R.id.sendMessageLayout);
        sms = (EditText) findViewById(R.id.sms);
        Button sendBtn = (Button) findViewById(R.id.sendBtn);
        Button closeBtn = (Button) findViewById(R.id.closeBtn);
        searchBx = (AutoCompleteTextView)findViewById(R.id.searchBx);
        searchBx.setThreshold(1);
        searchBx.setAdapter(new ContactSearchAdapter(this, selectedGroupId, groupName));
        closeSearchIcon = (ImageView)findViewById(R.id.closeSearchIcon);
        searchIcon = (ImageView)findViewById(R.id.searchIcon);


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgLayout.setVisibility(View.GONE);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SMSSenderTask(selectedGroupId, sms.getText().toString(), AddEditGroupActivity.this)
                        .execute();
                sms.setText("");
            }
        });

        closeSearchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBx.setText(null);
                closeSearchIcon.setVisibility(View.GONE);
                searchIcon.setVisibility(View.VISIBLE);
            }
        });

        if (!addMode && groupNameStr != null) {
            groupName.setText(groupNameStr);
            new QueryByIdSqliteTask<Long>(this, this, new ContactGroup(), selectedGroupId, QUERY_GROUP_BY_ID_REQUEST)
                    .execute();
        }


        setTitle("Add Group");


    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        Log.e(TAG, "onResume. Setting initCompleted to false");
        dialog = new ProgressDialog(this);
        dialog.setTitle("Collecting all contacts. Please wait...");
        dialog.setIndeterminate(false);
        dialog.show();
        //initCompleted = false;
        new QueryForGrpContactJunctionsByGrpIdSqliteTask(AddEditGroupActivity.this,
                AddEditGroupActivity.this, new GroupContactJunction()
                , QUERY_GROUP_CONTACT_JUNCTIONS_CONTACT_REQUEST, selectedGroupId).execute();
        Utility.hideKeyboard(this, groupName);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean showSaveMenuOption() {
        return true;
    }

    @Override
    public void saveClicked() {
        if (addMode) {
            ContactGroup grp = new ContactGroup();
            grp.setGroupName(groupName.getText().toString().trim());
            new InsertSqliteTask(this, this, grp, INSERT_GROUP_REQUEST).execute();
        } else {
            selectedGroup.setGroupName(groupName.getText().toString().trim());
            new UpdateSqliteTask(this, this, selectedGroup, UPDATE_GROUP_REQUEST).execute();
        }
    }

    @Override
    public void performOnInsertResult(int requestType, Long generatedId, int result, String error) {
        if (requestType == INSERT_GROUP_REQUEST) {
            if (result == FAILED) {
                Toast.makeText(this, "Save failed because " + error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Saved!", Toast.LENGTH_LONG).show();
            }
            if (!addModeBeingConvertedToEditMode) {
                finish();
            } else {
                addModeBeingConvertedToEditMode = false;
                selectedGroupId = generatedId;
                GroupContactJunction gcj = new GroupContactJunction();
                gcj.setContactGroupId(selectedGroupId);
                gcj.setPhone(selectedPhone);
                gcj.create(AddEditGroupActivity.this);
                //selectedContactId = -1;
                addMode = false;
            }
        }
    }

    @Override
    public void performActionOnQueryResult(int requestType, DefaultEntity entity) {
        if (requestType == QUERY_GROUP_BY_ID_REQUEST) {
            selectedGroup = (ContactGroup) entity;
        }
    }

    @Override
    public void performOnUpdateResult(int requestType, int result, String error) {
        if (requestType == UPDATE_GROUP_REQUEST) {
            if (result == FAILED) {
                Toast.makeText(this, "Update failed because " + error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Updated!", Toast.LENGTH_LONG).show();
            }
            finish();
        }
    }

    @Override
    public void performActionOnMultiRowQueryResult(int requestType, List entities) {
        if (requestType == QUERY_GROUP_FOR_ALL_CONTACT_REQUEST) {
            dialog.dismiss();
            allContacts = entities;
            List<Contact> selectedContacts = new ArrayList<>();
            if (selectedPhones != null & selectedPhones.size() > 0) {
                for (Contact c: allContacts) {
                    if (selectedPhones.contains(c.getPhone())) {
                        selectedContacts.add(c);
                    }
                }
                if (selectedContacts.size()>0) {
                    for (Contact c: selectedContacts){
                        entities.remove(c);
                    }
                }
                entities.addAll(0, selectedContacts);
            }
            contacts.setAdapter(new ContactsAdapter(this, R.layout.contacts_list_item, entities));

        } else if (requestType == QUERY_GROUP_CONTACT_JUNCTIONS_CONTACT_REQUEST) {
            if (entities != null) {
                selectedPhones = entities;
            }
            Log.e(TAG, "requestType == QUERY_GROUP_CONTACT_JUNCTIONS_CONTACT_REQUEST. initCompleted to true");
            new QueryForAllIdSqliteTask<Contact>(this, this, new Contact(),
                    QUERY_GROUP_FOR_ALL_CONTACT_REQUEST).execute();
        }
    }

    class ContactsAdapter extends ArrayAdapter<Contact> {

        public ContactsAdapter(Context context, int resource, List<Contact> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Contact contact = getItem(position);
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = convertView != null ? convertView :
                    inflater.inflate(R.layout.contacts_list_item, parent, false);
            final boolean[] initCompleted = new boolean[1];
            TextView name = (TextView) row.findViewById(R.id.name);
            TextView phone = (TextView) row.findViewById(R.id.phone);
            LinearLayout cbxL = (LinearLayout) row.findViewById(R.id.chkbxLayout);
            cbxL.removeAllViews();
            CheckBox cbx = new CheckBox(AddEditGroupActivity.this);
            cbxL.addView(cbx);
            Log.e(TAG, "selected contactsids:" + selectedPhones);
            cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e(TAG, "setOnCheckedChangeListener.isChecked:" + isChecked + ", contact:"
                            + contact.getPhone() + ", IC:" + initCompleted[0]);
                    if (initCompleted[0]) {
                        if (isChecked) {
                            if (Utility.isEmpty(groupName.getText().toString().trim())) {
                                buttonView.setChecked(false);
                                new AlertDialog.Builder(AddEditGroupActivity.this).
                                        setMessage("First, please enter the name of the group.")
                                        .setPositiveButton("OK", null)
                                        .show();
                            } else {
                                if (selectedGroupId != -1) {
                                    Log.e(TAG, "Creating a new junction.");
                                    final GroupContactJunction gcj = new GroupContactJunction();
                                    gcj.setContactGroupId(selectedGroupId);
                                    gcj.setPhone(contact.getPhone());
                                    new AsyncTask<Void, Void, Void>() {

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            gcj.create(AddEditGroupActivity.this);
                                            return null;
                                        }
                                    }.execute();

                                } else {
                                    ContactGroup grp = new ContactGroup();
                                    grp.setGroupName(groupName.getText().toString().trim());
                                    addModeBeingConvertedToEditMode = true;
                                    selectedPhone = contact.getPhone();
                                    new InsertSqliteTask(AddEditGroupActivity.this, AddEditGroupActivity.this,
                                            grp, INSERT_GROUP_REQUEST).execute();

                                }
                                selectedPhones.add(contact.getPhone());
                                contact.setIsSelected(true);
                            }
                        } else if (!isChecked) {
                            Log.e(TAG, "Removing a junction." + contact.getPhone());
                            new AsyncTask<Void, Void, Void>() {

                                @Override
                                protected Void doInBackground(Void... params) {
                                    new GroupContactJunction().deleteByContactGroupIdAndPhone(AddEditGroupActivity.this,
                                            selectedGroupId, contact.getPhone());
                                    selectedPhones.remove(new Long(contact.getPhone()));
                                    return null;
                                }
                            }.execute();
                            contact.setIsSelected(false);
                        }
                    } else {
                        initCompleted[0] = true;
                    }
                }
            });
            name.setText(contact.getName());
            phone.setText(contact.getPhone());

            if (selectedPhones.contains(contact.getPhone())) {
                initCompleted[0] = false;
                cbx.setChecked(true);
            } else {
                if (cbx.isChecked()) {
                    initCompleted[0] = false;
                    cbx.setChecked(false);
                } else {
                    initCompleted[0] = true;
                }
            }


            return row;
        }
    }

    @Override
    public boolean showEditMenuOption() {
        return true;
    }

    @Override
    public void editClicked() {
        sendMsgLayout.setVisibility(View.VISIBLE);
    }

    public void makeCloseSearchIconVisible(){
        searchMode = true;
        closeSearchIcon.setVisibility(View.VISIBLE);
        searchIcon.setVisibility(View.GONE);
    }
    public void makeSearchIconVisible(){
        searchMode = false;
        closeSearchIcon.setVisibility(View.GONE);
        searchIcon.setVisibility(View.VISIBLE);
        init();
    }

    @Override
    public void onBackPressed() {
        if (searchMode) {
            Utility.hideKeyboard(this, searchBx);
        } else {
            super.onBackPressed();
        }
    }

    public void updateListView(){
        ((ArrayAdapter)contacts.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean showDeleteMenuOption() {
        return true;
    }

    @Override
    public void deleteClicked() {
        if (selectedGroup != null) {
            selectedGroup.delete(this);

        } else {
            new ContactGroup().delete(this);
        }
        super.onBackPressed();
    }
}
