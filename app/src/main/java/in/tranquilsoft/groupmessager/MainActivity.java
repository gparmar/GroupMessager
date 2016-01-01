package in.tranquilsoft.groupmessager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import in.tranquilsoft.groupmessager.consumer.MultiResultSqliteConsumer;
import in.tranquilsoft.groupmessager.consumer.SingleResultSqliteConsumer;
import in.tranquilsoft.groupmessager.model.DefaultEntity;
import in.tranquilsoft.groupmessager.model.impl.ContactGroup;
import in.tranquilsoft.groupmessager.model.impl.MessageSentStatus;
import in.tranquilsoft.groupmessager.task.ContactsGathererTask;
import in.tranquilsoft.groupmessager.task.QueryForAllGroupsSqliteTask;
import in.tranquilsoft.groupmessager.task.QueryForAllIdSqliteTask;
import in.tranquilsoft.groupmessager.util.Constants;

public class MainActivity extends DefaultActivity implements SingleResultSqliteConsumer,
        MultiResultSqliteConsumer {
    public static final String CONTACT_PHONE_NUMBER = "CONTACT_PHONE_NUMBER";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String PHONE = "PHONE";
    public static final String CONTACT_GROUP_ID = "CONTACT_GROUP_ID";
    public static final String HISTORY_ID = "HISTORY_ID";
    public static final int QUERY_BY_ID_FOR_GROUP = 101;
    public static final int QUERY_FOR_ALL_GROUP = 102;
    public static final String SENT_ACTION = "SENT";
    public static final String DELIVERED_ACTION = "DELIVERED";
    String TAG = "MainActivity";

    MySentBroadcastReceiver sentBroadcastReceiver = new MySentBroadcastReceiver();
    MyDeliveryBroadcastReceiver deliveryBroadcastReceiver = new MyDeliveryBroadcastReceiver();

    ListView groups;
    List<ContactGroup> contactGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Group Messager");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addGroup = new Intent(MainActivity.this, AddEditGroupActivity.class);
                startActivity(addGroup);
            }
        });

        groups = (ListView) findViewById(R.id.groups);
        groups.setClickable(true);

        groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "in onItemClick");
                ContactGroup grp = contactGroups.get(position);
                Intent addGroup = new Intent(MainActivity.this, AddEditGroupActivity.class);
                addGroup.putExtra(Constants.GROUP_ID, grp.getGroupId());
                addGroup.putExtra(Constants.GROUP_NAME, grp.getGroupName());
                addGroup.putExtra(Constants.CONTACT_COUNT, grp.getContactsCount());
                addGroup.putExtra(Constants.ADD_MODE, false);
                startActivity(addGroup);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Register the Broadcast recievers for sent and delivered smses
        registerReceiver(sentBroadcastReceiver, new IntentFilter(SENT_ACTION));
        registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED_ACTION));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean contactsCollected = prefs.getBoolean(Constants.PROPERTY_CONTACTS_COLLECTED, false);

        if (!contactsCollected) {
            Log.e(TAG, "Contacts were not collected. Doing it now...");
            new ContactsGathererTask(this).execute();
        } else {
            Log.e(TAG, "Contacts were collected. Skipping it now...");
        }

        new QueryForAllGroupsSqliteTask(this, this, new ContactGroup(), QUERY_FOR_ALL_GROUP).execute();
    }

    @Override
    public boolean showSaveMenuOption() {
        return false;
    }


    @Override
    public void performActionOnQueryResult(int requestType, DefaultEntity entity) {

    }

//    @Override
//    public void performActionOnResult(int requestType, List entities) {
//
//    }

    @Override
    public void performActionOnMultiRowQueryResult(int requestType, List entities) {
        if (requestType == QUERY_FOR_ALL_GROUP && entities != null) {
            contactGroups = entities;
            List<String> groupNames = new ArrayList<>();
            for (ContactGroup ent : contactGroups) {
                ContactGroup cg = (ContactGroup) ent;
                groupNames.add(cg.getGroupName() + " ("+cg.getContactsCount()+")");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    groupNames);
            groups.setAdapter(adapter);
        }
    }

    @Override
    public boolean showEditMenuOption() {
        return false;
    }

    public class MySentBroadcastReceiver extends BroadcastReceiver {
        public MySentBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long historyId = intent.getLongExtra(HISTORY_ID, -1);

            List<MessageSentStatus> msses = new MessageSentStatus().getByHistoryId(context, historyId);
            //MessageSentStatus mss = new MessageSentStatus().getByHistoryIdAndPhone(context, historyId, phone);
            String result = null;
            switch (getResultCode()) {

                case Activity.RESULT_OK:
                    result = "Transmission successful";
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    result = "Transmission failed";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    result = "Radio off";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    result = "No PDU defined";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    result = "No service";
                    break;
            }
            if (msses != null && msses.size()>0) {
                for (MessageSentStatus mss: msses) {
                    if (result.equals("Transmission successful")) {
                        mss.setSentStatus(Constants.TRUE);
                    } else {
                        mss.setSentStatus(Constants.FALSE);
                    }
                    mss.update(context);
                }
            }

        }
    }

    public class MyDeliveryBroadcastReceiver extends BroadcastReceiver {
        public MyDeliveryBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            long historyId = intent.getLongExtra(HISTORY_ID, -1);

//            MessageSentStatus mss = new MessageSentStatus().getByHistoryIdAndPhone(context, historyId, phone);
            List<MessageSentStatus> msses = new MessageSentStatus().getByHistoryId(context, historyId);
            if (msses != null && msses.size()>0) {
                for (MessageSentStatus mss : msses) {
                    mss.setDeliveredAt(System.currentTimeMillis());
                    mss.setDeliveryStatus(Constants.TRUE);
                    mss.update(context);
                }
            }
        }
    }
}
