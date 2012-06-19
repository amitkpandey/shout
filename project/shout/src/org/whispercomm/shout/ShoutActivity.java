
package org.whispercomm.shout;

import org.joda.time.DateTime;
import org.whispercomm.shout.id.SignatureUtility;
import org.whispercomm.shout.provider.ShoutProviderContract;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShoutActivity extends ListActivity {

	private static final String TAG = "ShoutActivity";

	private Cursor cursor;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		this.cursor = ShoutProviderContract
				.getCursorOverAllShouts(getApplicationContext());
		setListAdapter(new TimelineAdapter(this, cursor));
		Log.v(TAG, "Finished onCreate");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cursor.close();
		Log.v(TAG, "Finished onDestroy");
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Log.v(TAG, "Click at position " + position + ", id " + id);
		ViewHolder holder = (ViewHolder) v.getTag();
		holder.buttonHolder.setVisibility(View.VISIBLE);
		holder.expanded = true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
			case R.id.settings:
				intent = new Intent(this, SettingsActivity.class);
				break;
			case R.id.compose:
				intent = new Intent(this, MessageActivity.class);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}

		startActivity(intent);
		return true;
	}

	public void onClickShout(View v) {
		Log.v(TAG, "Shout button clicked");
		startActivity(new Intent(this, MessageActivity.class));
	}

	public void onClickSettings(View v) {
		Log.v(TAG, "Settings button clicked");
		startActivity(new Intent(this, SettingsActivity.class));
	}

	public void onClickReshout(View v) {
		Log.v(TAG, "Reshout button clicked");
		ViewGroup rowView = (ViewGroup) v.getParent().getParent(); // bad
		ViewHolder holder = (ViewHolder) rowView.getTag();
		int id = holder.id;
		Log.v(TAG, "Shout ID received as " + id);
		AsyncTask<Integer, Void, Boolean> reshoutTask = new AsyncTask<Integer, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Integer... ids) {
				Shout parent = ShoutProviderContract.retrieveShoutById(getApplicationContext(),
						ids[0]);
				if (parent == null) {
					return false;
				}
				SignatureUtility signUtility = new SignatureUtility(getApplicationContext());
				ShoutCreator creator = new ShoutCreator(getApplicationContext(), signUtility);
				Shout reshout = creator.saveShout(DateTime.now(), null, parent);
				if (reshout == null) {
					return false;
				}
				return creator.sendShout(reshout);
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result.booleanValue()) {
					Toast.makeText(getApplicationContext(), "Reshout successfully sent!",
							Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(getApplicationContext(), "Reshout unsuccessful...",
							Toast.LENGTH_LONG);
				}
			}
		};
		reshoutTask.execute(new Integer[] {
				id
		});
	}

	static class ViewHolder {
		ImageView avatar;
		TextView origSender;
		TextView sender;
		TextView message;
		TextView age;
		ViewGroup buttonHolder;
		boolean expanded = false;
		int id = -1;
	}

	private class TimelineAdapter extends CursorAdapter {

		public TimelineAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// Get the shout
			int idIndex = cursor
					.getColumnIndex(ShoutProviderContract.Shouts._ID);
			int id = cursor.getInt(idIndex);
			Shout shout = ShoutProviderContract.retrieveShoutById(context, id);

			// Find the views
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.id = id;
			holder.buttonHolder.setVisibility(View.GONE);

			// Set the Shout data in the views
			holder.avatar.setImageResource(R.drawable.defaultavatar);
			holder.sender.setText(shout.getSender().getUsername());
			holder.age.setText(ShoutMessageUtility.getDateTimeAge(shout.getTimestamp()));
			ShoutType type = ShoutMessageUtility.getShoutType(shout);
			switch (type) {
				case SHOUT:
					holder.message.setText(shout.getMessage());
					holder.origSender.setText(shout.getSender().getUsername());
					break;
				case RESHOUT:
					holder.message.setText(shout.getParent().getMessage());
					holder.origSender.setText(shout.getParent().getSender().getUsername());
					break;
				case COMMENT:
					holder.message.setText(shout.getMessage());
					holder.origSender.setText("comment"); // FIXME
					break;
				case RECOMMENT:
					holder.message.setText(shout.getParent().getMessage());
					holder.origSender.setText(shout.getParent().getSender().getUsername());
					break;
				default:
					throw new IllegalStateException("Did not get valid ShoutType");
			}
			return;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View rowView = inflater.inflate(R.layout.row, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
			holder.origSender = (TextView) rowView.findViewById(R.id.origsender);
			holder.sender = (TextView) rowView.findViewById(R.id.sender);
			holder.age = (TextView) rowView.findViewById(R.id.age);
			holder.message = (TextView) rowView.findViewById(R.id.message);
			holder.buttonHolder = (ViewGroup) rowView.findViewById(R.id.buttonHolder);
			rowView.setTag(holder);
			Log.v(TAG, "View inflated");
			return rowView;
		}
	}
}
