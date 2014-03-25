package me.zhao.wirelesscontrol;

import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.provider.Settings;
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

		// Enable Data Connection
		setDataConnectionState(context, true);
		// Enable Wifi Hotspot
		setWifiApEnabled(true);
		// Enable Gps
		setGpsEnabled(context, true);
	}

	private void setDataConnectionState(Context context, boolean state) {
		Class<? extends ConnectivityManager> connectivityManagerClz = null;
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

	private void setGpsEnabled(Context context, boolean enabled) {
		ContentResolver mResolver = context.getContentResolver();
		if (VERSION.SDK_INT < KitKat.VERSION_CODE) {
			if (enabled) {
				Settings.Secure.putInt(mResolver, KitKat.LOCATION_MODE,  KitKat.LOCATION_MODE_HIGH_ACCURACY);
			} else {
				Settings.Secure.putInt(mResolver, KitKat.LOCATION_MODE,  KitKat.LOCATION_MODE_OFF);
			}
		} else {
			if (enabled) {
				Settings.Secure.putString(mResolver, KitKat.LOCATION_PROVIDERS_ALLOWED,  LocationManager.GPS_PROVIDER);
				Settings.Secure.putString(mResolver, KitKat.LOCATION_PROVIDERS_ALLOWED,  LocationManager.NETWORK_PROVIDER);
			} else {
				Settings.Secure.putString(mResolver, KitKat.LOCATION_PROVIDERS_ALLOWED,  "");
			}
		}
	}

	// For compact the low version system
	private class KitKat {
		public static final int VERSION_CODE = 19;

		// From android.provider.Settings.Secure
		public static final String LOCATION_MODE = "location_mode";
		public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
		public static final int LOCATION_MODE_OFF = 0;
		public static final int LOCATION_MODE_SENSORS_ONLY = 1;
		public static final int LOCATION_MODE_BATTERY_SAVING = 2;
		public static final int LOCATION_MODE_HIGH_ACCURACY = 3;
	}
}
