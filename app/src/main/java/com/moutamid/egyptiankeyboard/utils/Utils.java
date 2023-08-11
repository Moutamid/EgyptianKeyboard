package com.moutamid.egyptiankeyboard.utils;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.provider.Settings;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

public class Utils {
    /**
     * function to check whether the soft keyboard has been enabled
     */
    public static boolean isKeyboardEnabled(Context context) {
        String packageLocal = context.getPackageName();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        List<InputMethodInfo> list = inputMethodManager.getEnabledInputMethodList();

        // check if our keyboard is enabled as input method
        for (InputMethodInfo inputMethod : list) {
            String packageName = inputMethod.getPackageName();
            String packageID = inputMethod.getId();

            //checking whether soft keyboard has been enabled
            if (packageName.equals(packageLocal)) {
                return true;
            }
            //checking whether soft keyboard has been set as default
            /*if (packageID.contains(packageLocal) &&
                    inputMethod.getId().equals(Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.DEFAULT_INPUT_METHOD))) {
                return true;
            }*/
        }
        return false;
    }

    /**
     * function to check whether the soft keyboard has been set as default by the user
     */
    public static boolean isKeyboardDefault(Context context) {
        String packageLocal = context.getPackageName();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        List<InputMethodInfo> list = inputMethodManager.getEnabledInputMethodList();

        // check if our keyboard is enabled as input method
        for (InputMethodInfo inputMethod : list) {
            String packageName = inputMethod.getPackageName();
            String packageID = inputMethod.getId();

            //checking whether soft keyboard has been enabled
            /*if (packageName.equals(packageLocal)) {
                return true;
            }*/
            //checking whether soft keyboard has been set as default
            if (packageID.contains(packageLocal) &&
                    inputMethod.getId().equals(Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.DEFAULT_INPUT_METHOD))) {
                return true;
            }
        }
        return false;
    }
}