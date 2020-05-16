package com.example.suber_again;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class SocketControl extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
            Toast.makeText(context, activeNetworkInfo.getTypeName() + " connected", Toast.LENGTH_SHORT).show();
            // Your code to start Activity B
            Log.d("sock", "entered");

        } else {
            Toast.makeText(context, "No Internet or Network connection available", Toast.LENGTH_LONG).show();
        }
    }
}
