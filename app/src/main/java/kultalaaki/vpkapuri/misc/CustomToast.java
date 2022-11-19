/*
 * Created by Kultala Aki on 2/28/21 9:06 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 2/14/21 8:59 PM
 */

package kultalaaki.vpkapuri.misc;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import kultalaaki.vpkapuri.R;

public class CustomToast {

    public void showToast(String headText, String toastText, Context ctx, View parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View parent = inflater.inflate(R.layout.custom_toast, container, false);
        View layoutToast = inflater.inflate(R.layout.custom_toast, (ViewGroup) parent.findViewById(R.id.custom_toast_container));

        TextView head = (TextView) layoutToast.findViewById(R.id.head_text);
        head.setText(headText);
        TextView toastMessage = (TextView) layoutToast.findViewById(R.id.toast_text);
        toastMessage.setText(toastText);

        Toast toast = new Toast(ctx);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layoutToast);
        toast.show();
    }
}
