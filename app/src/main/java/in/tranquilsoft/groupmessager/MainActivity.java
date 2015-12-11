package in.tranquilsoft.groupmessager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
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
import in.tranquilsoft.groupmessager.task.ContactsGathererTask;
import in.tranquilsoft.groupmessager.task.QueryForAllIdSqliteTask;
import in.tranquilsoft.groupmessager.util.Constants;

public class MainActivity extends DefaultActivity implements SingleResultSqliteConsumer,
        MultiResultSqliteConsumer {
    public static final int QUERY_BY_ID_FOR_GROUP = 101;
    public static final int QUERY_FOR_ALL_GROUP = 102;
    String TAG = "MainActivity";

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
                addGroup.putExtra(Constants.ADD_MODE, false);
                startActivity(addGroup);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean contactsCollected = prefs.getBoolean(Constants.PROPERTY_CONTACTS_COLLECTED, false);

        if (!contactsCollected) {
            Log.e(TAG, "Contacts were not collected. Doing it now...");
            new ContactsGathererTask(this).execute();
        } else {
            Log.e(TAG, "Contacts were collected. Skipping it now...");
        }

        new QueryForAllIdSqliteTask<ContactGroup>(this, this, new ContactGroup(), QUERY_FOR_ALL_GROUP).execute();
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
                groupNames.add(((ContactGroup) ent).getGroupName());
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
}
