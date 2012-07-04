
package org.whispercomm.shout;

import org.whispercomm.shout.id.IdManager;
import org.whispercomm.shout.provider.ShoutProviderContract;
import org.whispercomm.shout.tasks.CommentTask;
import org.whispercomm.shout.tasks.ShoutTask;
import org.whispercomm.shout.util.ShoutMessageUtility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MessageActivity extends Activity {

	public static final String TAG = "MessageActivity";
	public static final String PARENT_ID = "parent";

	private Toast noUserToast;
	private IdManager idManager;
	private Shout parent = null;
	private int parentId = -1;

	@SuppressLint("ShowToast")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message);
		idManager = new IdManager(getApplicationContext());
		noUserToast = Toast.makeText(getApplicationContext(), "Set up a user before you Shout!",
				Toast.LENGTH_LONG);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		parentId = extras.getInt(PARENT_ID, -1);
		if (parentId > 0) {
			parent = ShoutProviderContract.retrieveShoutById(getApplicationContext(), parentId);
			ShoutType type = ShoutMessageUtility.getShoutType(parent);
			switch (type) {
				case COMMENT:
				case RESHOUT:
					parent = parent.getParent();
					parentId = ShoutProviderContract.storeShout(getApplicationContext(), parent);
					Log.v(TAG, "Parent was a comment/reshout, resetting parent to grandparent");
					break;
				case RECOMMENT:
					parent = parent.getParent().getParent();
					parentId = ShoutProviderContract.storeShout(getApplicationContext(), parent);
					Log.v(TAG, "Parent was a recomment, resetting parent to great grandparent");
					break;
				default:
					break;
			}
			Log.v(TAG, "Parent text received as: " + parent.getMessage());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (idManager.userIsNotSet()) {
			finish();
			noUserToast.show();
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}
	}

	public void onClickSend(View v) {
		Log.v(TAG, "Send button clicked");
		EditText editor = (EditText) findViewById(R.id.compose);
		String content = editor.getText().toString();
		Log.v(TAG, "Shout text received as: " + content);
		if (parent == null) {
			Log.v(TAG, "Creating a new shout...");
			new ShoutTask(getApplicationContext()).execute(content);
		} else {
			Log.v(TAG, "Commenting on another shout...");
			new CommentTask(getApplicationContext(), parentId).execute(content);
		}
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		finish();
	}
}
