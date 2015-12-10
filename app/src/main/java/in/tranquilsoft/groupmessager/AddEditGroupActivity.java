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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    ListView contacts;

    boolean addMode = true;
    long selectedGroupId = -1;
    ContactGroup selectedGroup;
    Map<Long, CheckBox> contactIdVsCheckbox = new HashMap<>();

    boolean addModeBeingConvertedToEditMode = false;
    long selectedContactId = -1;
    ProgressDialog dialog;
    List<Long> selectedContactIds = new ArrayList<>();
    LinearLayout sendMsgLayout;
    EditText sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_group);
        String groupNameStr = null;
        try {
            addMode = getIntent().getExtras().getBoolean(Constants.ADD_MODE, true);
            groupNameStr = getIntent().getExtras().getString(Constants.GROUP_NAME, null);
            selectedGroupId = getIntent().getExtras().getLong(Constants.GROUP_ID, -1);
        } catch (Exception e) {

        }

        groupName = (EditText) findViewById(R.id.groupName);
        container = (LinearLayout) findViewById(R.id.container);
        contacts = (ListView) findViewById(R.id.contacts);
        sendMsgLayout = (LinearLayout)findViewById(R.id.sendMessageLayout);
        sms = (EditText)findViewById(R.id.sms);
        Button sendBtn = (Button)findViewById(R.id.sendBtn);
        Button closeBtn = (Button)findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsgLayout.setVisibility(View.GONE);
            }
        });



        if (!addMode && groupNameStr != null) {
            groupName.setText(groupNameStr);
            new QueryByIdSqliteTask(this, this, new ContactGroup(), selectedGroupId, QUERY_GROUP_BY_ID_REQUEST)
                    .execute();
        }
        setTitle("Add Group");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
            grp.setGroupName(groupName.getText().toString());
            new InsertSqliteTask(this, this, grp, INSERT_GROUP_REQUEST).execute();
        } else {
            selectedGroup.setGroupName(groupName.getText().toString());
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
                gcj.setContactId(selectedContactId);
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
            contacts.setAdapter(new ContactsAdapter(this, R.layout.contacts_list_item, entities));
            dialog.dismiss();
        } else if (requestType == QUERY_GROUP_CONTACT_JUNCTIONS_CONTACT_REQUEST) {
            if (entities != null) {
                selectedContactIds.removeAll(selectedContactIds);
                for (Object gcj: entities) {
                    selectedContactIds.add(((GroupContactJunction)gcj).getContactId());
                    Log.e(TAG, "selected contact id:"+((GroupContactJunction)gcj).getContactId());
                }
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
            LinearLayout cbxL = (LinearLayout)row.findViewById(R.id.chkbxLayout);
            cbxL.removeAllViews();
            CheckBox cbx = new CheckBox(AddEditGroupActivity.this);
            cbxL.addView(cbx);
            Log.e(TAG, "selected contactsids:" + selectedContactIds);
            cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e(TAG, "setOnCheckedChangeListener.isChecked:"+isChecked+", contact:"
                            +contact.getId()+", IC:"+initCompleted[0]);
                    if (initCompleted[0]) {
                        if (isChecked) {
                            if (Utility.isEmpty(groupName.getText().toString())) {
                                buttonView.setChecked(false);
                                new AlertDialog.Builder(AddEditGroupActivity.this).
                                        setMessage("First ,please enter the name of the group.")
                                        .show();
                            } else {
                                if (selectedGroupId != -1) {
                                    Log.e(TAG, "Creating a new junction.");
                                    final GroupContactJunction gcj = new GroupContactJunction();
                                    gcj.setContactGroupId(selectedGroupId);
                                    gcj.setContactId(contact.getId());
                                    new AsyncTask<Void, Void, Void>(){

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            gcj.create(AddEditGroupActivity.this);
                                            return null;
                                        }
                                    }.execute();

                                } else {
                                    ContactGroup grp = new ContactGroup();
                                    grp.setGroupName(groupName.getText().toString());
                                    addModeBeingConvertedToEditMode = true;
                                    selectedContactId = contact.getId();
                                    new InsertSqliteTask(AddEditGroupActivity.this, AddEditGroupActivity.this,
                                            grp, INSERT_GROUP_REQUEST).execute();

                                }
                                selectedContactIds.add(contact.getId());
                            }
                        } else if (!isChecked) {
                            Log.e(TAG, "Removing a junction."+contact.getId());
                            new AsyncTask<Void,Void,Void>(){

                                @Override
                                protected Void doInBackground(Void... params) {
                                    new GroupContactJunction().deleteByContactGroupIdContactId(AddEditGroupActivity.this,
                                            selectedGroupId, contact.getId());
                                    selectedContactIds.remove(new Long(contact.getId()));
                                    return null;
                                }
                            }.execute();

                        }
                    } else {
                        initCompleted[0] = true;
                    }
                }
            });
            name.setText(contact.getName());
            phone.setText(contact.getPhone());

            if (selectedContactIds.contains(contact.getId())) {
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
}
