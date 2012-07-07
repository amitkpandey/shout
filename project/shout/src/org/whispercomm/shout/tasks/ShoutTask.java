package org.whispercomm.shout.tasks;

import org.joda.time.DateTime;
import org.whispercomm.shout.LocalShout;
import org.whispercomm.shout.Me;
import org.whispercomm.shout.R;
import org.whispercomm.shout.ShoutCreator;
import org.whispercomm.shout.id.IdManager;
import org.whispercomm.shout.id.UserNotInitiatedException;

import android.content.Context;

public class ShoutTask extends AsyncTaskCallback<String, Void, LocalShout> {

	private Context context;
	private Me me;

	public ShoutTask(final Context context) {
		super(new AsyncTaskCompleteListener<LocalShout>() {
			@Override
			public void onComplete(LocalShout result) {
				SendShoutTask sendTask = new SendShoutTask(context,
						R.string.shoutSuccess, R.string.shoutFail);
				sendTask.execute(result);
			}
		});
		this.context = context;
		try {
			this.me = new IdManager(context).getMe();
		} catch (UserNotInitiatedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected LocalShout doInBackground(String... params) {
		String message = params[0];

		ShoutCreator creator = new ShoutCreator(context);
		return creator.createShout(DateTime.now(), message, me);
	}

}
