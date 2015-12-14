package in.tranquilsoft.groupmessager;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import in.tranquilsoft.groupmessager.consumer.MultiResultSqliteConsumer;
import in.tranquilsoft.groupmessager.model.impl.MessageSentStatus;
import in.tranquilsoft.groupmessager.model.impl.MessagingHistory;
import in.tranquilsoft.groupmessager.task.QueryForAllIdSqliteTask;
import in.tranquilsoft.groupmessager.util.Constants;

/**
 * Created by gurdevp on 11/12/15.
 */
public class HistoryActivity extends DefaultActivity implements MultiResultSqliteConsumer<MessagingHistory> {
    public static final int QUERY_ALL_MESSAGING_HISTORY_REQUEST = 0;
    SimpleDateFormat shortDate = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat longDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private ExpandableListView expandableListView;
    int prev=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (prev != -1) {
                    expandableListView.collapseGroup(prev);
                }
                prev = groupPosition;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        new QueryForAllIdSqliteTask<MessagingHistory>(this, this, new MessagingHistory(),
                QUERY_ALL_MESSAGING_HISTORY_REQUEST).execute();
    }

    @Override
    public boolean showSaveMenuOption() {
        return false;
    }

    @Override
    public boolean showEditMenuOption() {
        return false;
    }

    @Override
    public void performActionOnMultiRowQueryResult(int requestType, List<MessagingHistory> entities) {
        if (entities != null) {
            if (requestType == QUERY_ALL_MESSAGING_HISTORY_REQUEST) {
                expandableListView.setAdapter(new MyExpandableListAdapter(this, entities));
            }
        }
    }

    class MyExpandableListAdapter implements ExpandableListAdapter {
        List<MessagingHistory> data;
        Context context;

        public MyExpandableListAdapter(Context context, List<MessagingHistory> objects) {
            data = objects;
            this.context = context;
        }


        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getGroupCount() {
            if (data != null) {
                return data.size();
            }
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View row = convertView != null ? convertView :
                    HistoryActivity.this.getLayoutInflater().inflate(R.layout.history_elv_item, parent, false);

            MessagingHistory mh = data.get(groupPosition);
            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            TextView date = (TextView) row.findViewById(R.id.dateTV);
            TextView groupName = (TextView) row.findViewById(R.id.groupName);
            TextView sms = (TextView) row.findViewById(R.id.sms);
            date.setText(shortDate.format(new Date(mh.getSentTime())));
            groupName.setText(mh.getGroup().getGroupName());
            sms.setText(mh.getSmsMessage());

            if (isExpanded) {
                icon.setImageDrawable(getResources().getDrawable(R.drawable.minus));
            } else {
                icon.setImageDrawable(getResources().getDrawable(R.drawable.plus));
            }
            return row;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT);
            MessagingHistory mh = data.get(groupPosition);
            TableLayout table = new TableLayout(HistoryActivity.this);
            table.setLayoutParams(params);
            TableRow header = (TableRow)
                    getLayoutInflater().inflate(R.layout.history_status_header, parent, false);
            table.addView(header);
            List<MessageSentStatus> msss = new MessageSentStatus().getHistoryId(context, mh.getId());
            for(MessageSentStatus mss: msss) {
                TableRow item = (TableRow)
                        getLayoutInflater().inflate(R.layout.history_status_item, parent, false);
                ImageView sentIcon = (ImageView) item.findViewById(R.id.sentIcon);
                ImageView deliveredIcon = (ImageView) item.findViewById(R.id.deliveredIcon);
                TextView name = (TextView) item.findViewById(R.id.name);
                TextView phone = (TextView) item.findViewById(R.id.phone);
                TextView sentOn= (TextView) item.findViewById(R.id.sentOn);
                TextView deliveredOn= (TextView) item.findViewById(R.id.deliveredOn);
                if (mss.getSentStatus()== Constants.TRUE) {
                    sentIcon.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                } else {
                    sentIcon.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_delete));
                }
                if (mss.getDeliveryStatus()== Constants.TRUE) {
                    deliveredIcon.setImageDrawable(getResources().getDrawable(R.drawable.tick));
                } else {
                    deliveredIcon.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_delete));
                }
                name.setText(mss.getContactName());
                phone.setText(mss.getPhone());
                if (mss.getSentAt() != -1) {
                    sentOn.setText(longDate.format(new Date(mss.getSentAt())));
                } else {
                    sentOn.setText("");
                }
                if (mss.getDeliveredAt() != -1) {
                    deliveredOn.setText(longDate.format(new Date(mss.getDeliveredAt())));
                } else {
                    deliveredOn.setText("");
                }
                table.addView(item);
            }
            return table;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {

        }

        @Override
        public void onGroupCollapsed(int groupPosition) {

        }

        @Override
        public long getCombinedChildId(long groupId, long childId) {
            return 0;
        }

        @Override
        public long getCombinedGroupId(long groupId) {
            return 0;
        }
    }

    class MyListAdapter extends ArrayAdapter<MessageSentStatus> {

        public MyListAdapter(Context context, int resource, List<MessageSentStatus> objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView != null ? convertView :
                    null;
            if (row == null) {
                row = getLayoutInflater().inflate(R.layout.history_status_item, parent, false);
            }
            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            TextView name = (TextView) row.findViewById(R.id.name);
            TextView phone = (TextView) row.findViewById(R.id.phone);

            MessageSentStatus mss = getItem(position);

            return row;
        }
    }
}
