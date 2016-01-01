package in.tranquilsoft.groupmessager;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import in.tranquilsoft.groupmessager.consumer.InsertDBConsumer;
import in.tranquilsoft.groupmessager.model.impl.Contact;
import in.tranquilsoft.groupmessager.model.impl.ContactGroup;
import in.tranquilsoft.groupmessager.model.impl.GroupContactJunction;
import in.tranquilsoft.groupmessager.task.InsertSqliteTask;
import in.tranquilsoft.groupmessager.util.Utility;

/**
 * Created by gurdevp on 06/05/15.
 */
public class ContactSearchAdapter extends ArrayAdapter<Contact> implements Filterable, InsertDBConsumer {
    public static final String TAG = "ContctSrchAdapter";
    private static final int INSERT_GROUP_REQUEST = 100;
    long contactGroupId;
    //String groupName;
    EditText groupNameET;
    private String selectedPhone;
    AddEditGroupActivity addEditGroupActivity;

    //List<Contact> contacts = null;

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (Utility.isEmpty((String)constraint)) {
                FilterResults filterResults = new FilterResults();
                filterResults.values = new ArrayList<>();
                filterResults.count = 0;
                return filterResults;
            }
            List<Contact> contacts = new Contact().getByContactByStartingPhrase(getContext(), (String)constraint);
            List<String> selectedPhones = new GroupContactJunction()
                    .getByContactGroup(getContext(), contactGroupId);

            for (Contact contact : contacts) {
                if (selectedPhones != null && selectedPhones.contains(contact.getPhone())) {
                    contact.setIsSelected(true);
                } else {
                    contact.setIsSelected(false);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = contacts;
            filterResults.count = contacts != null ? contacts.size() : 0;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addEditGroupActivity.makeCloseSearchIconVisible();
            if (results != null && results.count > 0) {
                addAll((List<Contact>) results.values);
                //notifyDataSetChanged();
            } else {
                addEditGroupActivity.makeSearchIconVisible();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    };


    public ContactSearchAdapter(Context context, long contactGroupId, EditText groupNameET) {
        super(context, 0, new ArrayList<Contact>());
        this.contactGroupId = contactGroupId;
        //this.groupName = groupName;
        this.groupNameET = groupNameET;
        this.addEditGroupActivity = (AddEditGroupActivity)context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d(TAG, "Getting the view for position:" + position);

        final Contact contact = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = convertView != null ? convertView :
                inflater.inflate(R.layout.contacts_list_item,
                        parent, false);
        final boolean[] initCompleted = new boolean[1];
        TextView name = (TextView) row.findViewById(R.id.name);
        TextView phone = (TextView) row.findViewById(R.id.phone);
        LinearLayout cbxL = (LinearLayout) row.findViewById(R.id.chkbxLayout);
        cbxL.removeAllViews();
        CheckBox cbx = new CheckBox(getContext());
        cbxL.addView(cbx);
        cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.e(TAG, "setOnCheckedChangeListener.isChecked:" + isChecked + ", contact:"
                        + contact.getPhone() + ", IC:" + initCompleted[0]);
                if (initCompleted[0]) {
                    if (isChecked) {
                        if (Utility.isEmpty(groupNameET.getText().toString().trim())) {
                            buttonView.setChecked(false);
                            new AlertDialog.Builder(getContext()).
                                    setMessage("First, please enter the name of the group.")
                                    .setPositiveButton("OK", null)
                                    .show();
                        } else {
                            if (contactGroupId != -1) {
                                Log.e(TAG, "Creating a new junction.");
                                final GroupContactJunction gcj = new GroupContactJunction();
                                gcj.setContactGroupId(contactGroupId);
                                gcj.setPhone(contact.getPhone());
                                new AsyncTask<Void, Void, Void>() {

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        gcj.create(getContext());
                                        return null;
                                    }
                                }.execute();
                                addEditGroupActivity.updateListView();
                            } else {
                                ContactGroup grp = new ContactGroup();
                                grp.setGroupName(groupNameET.getText().toString().trim());
                                selectedPhone = contact.getPhone();
                                new InsertSqliteTask(getContext(), ContactSearchAdapter.this,
                                        grp, INSERT_GROUP_REQUEST).execute();
                            }

                        }
                    } else if (!isChecked) {
                        Log.e(TAG, "Removing a junction." + contact.getPhone());
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                new GroupContactJunction().deleteByContactGroupIdAndPhone(getContext(),
                                        contactGroupId, contact.getPhone());
                                //selectedPhones.remove(new Long(contact.getPhone()));
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

        if (contact.isSelected()) {
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void performOnInsertResult(int requestType, Long generatedId, int result, String error) {
        if (requestType == INSERT_GROUP_REQUEST) {
            if (result == FAILED) {
                Toast.makeText(getContext(), "Save failed because " + error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_LONG).show();
            }
            contactGroupId = generatedId;
            GroupContactJunction gcj = new GroupContactJunction();
            gcj.setContactGroupId(contactGroupId);
            gcj.setPhone(selectedPhone);
            gcj.create(getContext());
            addEditGroupActivity.updateListView();
        }
    }


}
