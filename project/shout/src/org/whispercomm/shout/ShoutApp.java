
package org.whispercomm.shout;

import org.whispercomm.shout.content.ContentManager;

import android.app.Application;

public class ShoutApp extends Application {

	private ContentManager mContentManager;

	@Override
	public void onCreate() {
		super.onCreate();
		mContentManager = new ContentManager(this);
	}

	@Override
	public Object getSystemService(String name) {
		if (ContentManager.SHOUT_CONTENT_SERVICE.equals(name)) {
			return mContentManager;
		} else {
			return super.getSystemService(name);
		}
	}
}
