package com.eyc.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GladiusScanReceiver1D extends BroadcastReceiver {

	// Tag used for logging errors
	private static final String TAG = "BarcodeScanner";
	private GladiusBarcodeScanner1D callingListener;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "received scan start");
		callingListener.startScanning();
		
	}

	public void setCallingListener(GladiusBarcodeScanner1D gbs){
		Log.d(TAG, "callingListener set");
		this.callingListener = gbs;
	}
	
	public void destroy(){
		this.callingListener = null;
	}

}
