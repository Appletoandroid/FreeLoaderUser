package com.Appleto.FreeloaderUser.Utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

import com.Appleto.FreeloaderUser.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Common {

    public static Dialog dialog;

    public static void progress_show(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public static void progress_dismiss(Context context) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isEmptyEditText(EditText edt, String msg) {
        if (edt.getText().toString().isEmpty()) {
            edt.setError(msg);
            edt.requestFocus();
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isPasswordMatch(EditText edt, EditText editText, String msg) {
        if (!edt.getText().toString().matches(editText.getText().toString().trim())) {
            editText.setError(msg);
            editText.requestFocus();
            return false;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isPhoneCorrect(EditText edt) {
        if (edt.getText().toString().length() < 10) {
            edt.setError("Please enter 10 digit number");
            edt.requestFocus();
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isEmailCorrect(EditText edt) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!edt.getText().toString().trim().matches(emailPattern)) {
            edt.setError("Please enter correct email");
            edt.requestFocus();
            return true;
        }
        return false;
    }


    public static RequestBody getJsonFromJsonArray(JSONArray jsonObject) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        return body;
    }

    public static RequestBody getJsonFromJsonObject(JSONObject jsonObject) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        return body;
    }

    public static String formatDate(String date, String initDateFormat,
                                    String endDateFormat) {
        String parsedDate = null;
        Date initDate;
        try {
            initDate = new SimpleDateFormat(initDateFormat, Locale.getDefault()).parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat, Locale.getDefault());
            parsedDate = formatter.format(initDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parsedDate;
    }

    public static String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String address = "";
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
            }
            return address;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    public static int getWidthOfScreen(Context mContext) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        try {
            ((Activity) mContext).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);
        } catch (Exception e) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.widthPixels;
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    @SuppressLint("NewApi")
    public static void setDatePickerDialog(Context context, final EditText editText) {


        final SimpleDateFormat dateFormatter;
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog dateDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                editText.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH),
                newCalendar.get(Calendar.DAY_OF_MONTH));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date inputDate;
        try {
            inputDate = dateFormat.parse(dateFormat.format(new Date()));
            dateDialog.getDatePicker().setMaxDate(inputDate.getTime());

            dateDialog.show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
