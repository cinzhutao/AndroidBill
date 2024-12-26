package com.example.iot_software.util;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class DialogUtil {
    public static void show(Context ctx, String title, String desc, String positive, String negative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(desc);
        builder.setPositiveButton(positive, null);
        builder.setNegativeButton(negative, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
