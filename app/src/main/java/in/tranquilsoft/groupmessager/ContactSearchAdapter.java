package in.tranquilsoft.groupmessager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import in.tranquilsoft.groupmessager.model.impl.Contact;

/**
 * Created by gurdevp on 06/05/15.
 */
public class ContactSearchAdapter extends ArrayAdapter<Contact> implements Filterable {
    public static final String TAG = "ContctSrchAdapter";

    
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Contact> contacts = null;
            contacts = getContacts(constraint != null ? constraint.toString() : "");

            FilterResults filterResults = new FilterResults();
            filterResults.values = contacts;
            filterResults.count = contacts != null ? contacts.size() : 0;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {


                addAll((List<Contact>) results.values);
                //notifyDataSetChanged();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        private List<Contact> getContacts(String query) {
            StringBuffer s = new StringBuffer();

            List<Contact> result = new ArrayList<Contact>();
//            try {
//                if (noDataFragmentSelectContact.getContactsList() != null) {
//                    for (Contact contact : noDataFragmentSelectContact.getContactsList()) {
//                        if (contact.getName().toLowerCase().startsWith(query.toLowerCase())) {
//                            Contact clonedContact = contact.clone();
//                            if (!result.contains(clonedContact)) {
//                                result.add(clonedContact);
//                            }
//                            if (clonedContact.getName().equals("Other")) {
//                                boolean foundContact = false;
//                                for (Contact b : result) {
//                                    if (b.getName().equals("Other")) {
//                                        foundContact = true;
//                                        break;
//                                    }
//                                }
//                                if (!foundContact) {
//                                    result.add(contact);
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (Exception ex) {
//                // log it
//                Log.e(TAG, "", ex);
//            }

            return result;
        }
    };

    public ContactSearchAdapter(Context context, int selectionType) {
        super(context, 0, new ArrayList<Contact>());

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.d(TAG, "Getting the view for position:" + position);

        final Contact contact = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = convertView != null && convertView.getTag().equals("item") ? convertView :
                inflater.inflate(R.layout.contacts_list_item,
                        parent, false);

//        TextView contactName = (TextView) row.findViewById(R.id.contactName);
//
//        contactName.setText(contact.getName() + " Contact");
//
//        ImageView contactLogo = (ImageView) row.findViewById(R.id.contactLogo);
//        Utils.setContactIcon(getContext(), contactLogo, null, contact);
//
//
//        row.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (NoDataFlowActivity.BANK_SELECTION_TYPE_SMS_BANKING_NOT_PRESENT == selectionType) {
//                    Utils.putBoolean(getContext(), Utils.PROPERTY_NODATA_FLOW_SMS_BANKING_REGISTRATION_SMS_NOT_PRESENT, true);
//                    if (StringUtils.isEmpty(contact.getPrimarySmsContact()) &&
//                            StringUtils.isEmpty(contact.getPrimarySmsContactShort())) {
//
//                        //Do stuff for when no contact account present
//                        Intent summaryIntent = new Intent(getContext(), SummaryActivity.class);
//                        summaryIntent.putExtra(Utils.PROPERTY_NODATA_FLOW_SMS_BANKING_REGISTRATION_SMS_NOT_PRESENT, true);
//                        summaryIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        getContext().startActivity(summaryIntent);
//
//                        // finish the activity
//                        ((BaseActivity) getContext()).finish();
//                        return;
//
//                    } else {
//                        //Log event
//                        ((BaseActivity) getContext()).logEvent(Events.EVENT_NO_DATA_SMS_BANKING_REGISTERED);
//                    }
//                }
//
//                Utils.putString(getContext(), Utils.PROPERTY_NODATAFLOW_BANK, contact.getName());
//                Bundle args = new Bundle();
//                args.putString(Events.ATTR_SELECTED_BANK, contact.getName());
//                ((BaseActivity) getContext()).logEvent(Events.EVENT_NO_DATA_SEARCH_BANK_SELECTED, args);
//
//                SmsContactingCommand smsContactingBalanceCommand = null;
//                if (NoDataFlowActivity.BANK_SELECTION_TYPE_SMS_BANKING_PRESENT == selectionType) {
//                    smsContactingBalanceCommand =
//                            new SmsContactingBalanceCommand((BaseActivity) getContext());
//                } else if (NoDataFlowActivity.BANK_SELECTION_TYPE_SMS_BANKING_NOT_PRESENT == selectionType) {
//                    smsContactingBalanceCommand =
//                            new SmsContactingRegistrationCommand((BaseActivity) getContext());
//                }
//                smsContactingBalanceCommand.execute(contact.getId());
//
//
//            }
//        });


        return row;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


}
