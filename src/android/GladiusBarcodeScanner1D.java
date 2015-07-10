package com.eyc.plugins;

import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import gsm.com.eyc.GSMActivity;

import android.app.ScanSerialManager;
import android.hardware.GpioManager;
import android.hardware.GpioPort;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class GladiusBarcodeScanner1D extends CordovaPlugin {

	private String TAG = "BarcodeScanner";
	private static final String START_LISTENER = "startBarcodeListener1D";
	private static final String STOP_LISTENER = "stopBarcodeListener1D";
	private static final String START_SCAN = "startScanning1D";
	private static final int decodeTimeout = 2000;
	private CallbackContext scanReceivedCb;

	private ScanSerialManager mScanSerialManager;
	private static final int REGISTER_GET_SCAN_DATA = 1;
	private GpioManager mGpioManager;
	private GpioPort mGpioPort;
	private Messenger mMessenger;
	private IBinder mBinder;

	private int nGoodReadFlag = 0;
	private GladiusScanReceiver1D scanReceiver = null;
	private GSMActivity mActivity = null;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REGISTER_GET_SCAN_DATA:
				String read_text = msg.getData().getString("read_text");
				Log.d(TAG, "message = " + read_text);
				if (mActivity != null && mGpioPort != null){
					PluginResult result = new PluginResult(PluginResult.Status.OK, read_text);
					result.setKeepCallback(true);
					scanReceivedCb.sendPluginResult(result);
					Intent sendIntent = BarcodeScanner.getBroadCastIntent(PluginResult.Status.OK, read_text, true);
					LocalBroadcastManager.getInstance(mActivity.getApplicationContext()).sendBroadcast(sendIntent);
				}
				mActivity = null;
				break;
			}
		}
	};

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.d(TAG, "execute : "+action);
		if (START_LISTENER.equals(action)) {
			if (mScanSerialManager != null){ 
				return true; //listener already called, do nothing
			}
			scanReceivedCb = callbackContext;
			mScanSerialManager = (ScanSerialManager)((GSMActivity)this.cordova.getActivity()).getSystemService("scan_serial"); 
			mGpioManager = (GpioManager)((GSMActivity)this.cordova.getActivity()).getSystemService("gpio");			
			mMessenger = new Messenger(mHandler);
			mBinder = mMessenger.getBinder();
			mScanSerialManager.registerForReadSerialData(mBinder, REGISTER_GET_SCAN_DATA);

			try{
				mGpioPort = mGpioManager.openGpioPort(GpioPort.GPIO_PORT_NAME);
			}catch (IOException e) {
				Log.d(TAG, "IOException");
			}

			if (this.scanReceiver == null) {
				Log.d(TAG, "Instantiate Scan Receiver");
				this.scanReceiver = new GladiusScanReceiver1D();
				this.scanReceiver.setCallingListener(this);
				IntentFilter ifMiki1D = new IntentFilter("com.mobile.miki.action.startscan");
				Intent tmp = this.cordova.getActivity().registerReceiver(this.scanReceiver, ifMiki1D);
				Log.d(TAG, "Scan Receiver created successfully : "+tmp);
			}

			((GSMActivity)this.cordova.getActivity()).setGladiusScanner1D(this);
			return true;
		}
		else if (START_SCAN.equals(action)){ //possibility to call the scan directly
			Log.d(TAG, "execute : "+action);
			return startScanning(callbackContext);
		}
		else if (STOP_LISTENER.equals(action)) {
			Log.d(TAG, "execute : "+action);
			destroy();
			return true;
		}
		return false;  // Returning false results in a "MethodNotFound" error.
	}

	public boolean startScanning(){
		return startScanning(this.scanReceivedCb);
	}

	private boolean startScanning(CallbackContext scanReceivedCb) {
		if (mGpioPort != null) {
			Log.d(TAG, "mGpioPort != null");
			//mGpioPort.startScan();
			for(int timeout = 0; timeout < decodeTimeout; timeout++){
				nGoodReadFlag = mGpioPort.getScanFlag();
				//Log.d(TAG, "nGoodReadFlag = " + nGoodReadFlag);
				if (mGpioPort != null){
					if (nGoodReadFlag == 1)
					{
						Log.d(TAG, "nGoodReadFlag = " + nGoodReadFlag);
						mActivity = (GSMActivity)this.cordova.getActivity();
						break;
					}
				}
				try{
					Thread.sleep(1);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		mGpioPort.stopScan();
		mGpioPort.clearScanFlag();
		return true;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		destroy();
		super.onDestroy();
	}

	private void destroy(){
		if (mScanSerialManager != null) {
			mScanSerialManager.unRegisterForReadSerialData(mBinder);
		}
	}
}