package org.whispercomm.shout.network;

import org.whispercomm.manes.client.maclib.ManesInterface;
import org.whispercomm.manes.client.maclib.ManesNotInstalledException;
import org.whispercomm.shout.Shout;
import org.whispercomm.shout.provider.ShoutProviderContract;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class NetworkService extends Service {
	public static final String TAG = NetworkService.class.getSimpleName();

	public static final int NEW_SHOUT = 1;
	public static final int APP_ID = 74688;// "shout" on a phone keyboard

	private ManesInterface manes;
	private Messenger appMessenger;
	private NetworkProtocol networkProtocol;
	private NetworkReceiver networkReceiver;

	@Override
	public final void onCreate() {
		Log.i(TAG, "Starting service.");
		try {
			this.manes = new ManesInterface(APP_ID, getApplicationContext());
		} catch (ManesNotInstalledException e) {
			// TODO Handle this
			throw new RuntimeException(e);
		}

		this.appMessenger = new Messenger(new AppShoutHandler());
		this.networkProtocol = new NaiveNetworkProtocol(manes,
				getApplicationContext());
		this.networkReceiver = new NetworkReceiver(this.manes,
				this.networkProtocol);

		this.networkProtocol.initialize();
		this.networkReceiver.initialize();

		Log.i(TAG, "Service started.");
	}

	/**
	 * Create the channel for accepting incoming shouts from applications, e.g.,
	 * UI
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return appMessenger.getBinder();
	}

	@Override
	public final void onDestroy() {
		Log.i(TAG, "Stopping service.");
		manes.disconnect();
		networkProtocol.cleanup();
		networkReceiver.cleanup();
		Log.i(TAG, "Service stopped.");
	}

	/**
	 * Handler wrapper for processing incoming shouts from application (e.g.,
	 * UI)
	 */
	class AppShoutHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == NEW_SHOUT) {
				long shoutId = (Long) msg.obj; // FIXME
				int shoutIdInt = (int) shoutId;
				Shout shout = ShoutProviderContract
						.retrieveShoutById(
								NetworkService.this.getApplicationContext(),
								shoutIdInt);
				// TODO Find out why networkProtocol gets nulled
				networkProtocol.sendShout(shout);
			}
		}
	}

}