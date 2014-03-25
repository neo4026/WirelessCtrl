package me.zhao.wirelesscontrol;

import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	// Wifi Hotspot Confingure
	private static final String WIFI_HOTSPOT_SSID = "AndroidHotspot";
	private static final String WIFI_HOTSPOT_PASSWORD = "12345678";

	// System Service
	private WifiManager mWifiManager;
	private ConnectivityManager mConnManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		// for test
		Log.d("zhao", "onReceive");

		// Init service
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

		// Enable data connection
		setDataConnectionState(context, true);
		// Enable Wifi Hotspot
		setWifiApEnabled(true);
	}

	private void setDataConnectionState(Context context, boolean state) {
		Class connectivityManagerClz = null;
		try {
			mConnManager = (ConnectivityManager) context.getSystemService("connectivity");
			connectivityManagerClz = mConnManager.getClass();
			Method method = connectivityManagerClz.getMethod(
					"setMobileDataEnabled", new Class[] { boolean.class });
			method.invoke(mConnManager, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean setWifiApEnabled(boolean enabled) {
		if (enabled) { 
			mWifiManager.setWifiEnabled(false);
		}
		try {
			WifiConfiguration mApConfig = new WifiConfiguration();
			mApConfig.SSID = WIFI_HOTSPOT_SSID;
			mApConfig.preSharedKey = WIFI_HOTSPOT_PASSWORD;
			Method method = mWifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			return (Boolean) method.invoke(mWifiManager, mApConfig, enabled);
		} catch (Exception e) {
			return false;
		}
	}
}
