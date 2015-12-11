package in.tranquilsoft.groupmessager.util;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Created by gurdevp on 06/12/15.
 */
public class Utility {
    static String TAG = "Utility";

    public static String getNormalizedIndianPhoneNumber(String rawPhoneNumber) {
        if (isNotEmpty(rawPhoneNumber)) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            try {
                Phonenumber.PhoneNumber localNumberProto = phoneUtil.parse(rawPhoneNumber, "IN"); // india locale
                String parsedPhoneNumber = phoneUtil.format(localNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
                parsedPhoneNumber = parsedPhoneNumber != null && parsedPhoneNumber.length() > 0 ? parsedPhoneNumber : rawPhoneNumber;

                // remove all spaces
                parsedPhoneNumber = parsedPhoneNumber.replaceAll("\\s+", "");
                return parsedPhoneNumber;
            } catch (NumberParseException e) {
                Log.e(TAG, "NumberParseException was thrown: ", e);
            }
        }

        return null;
    }

    public static boolean isEmpty(String str) {
        if (str == null) return true;
        return "".equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static void hideKeyboard(Context context, EditText et) {

    }
}
