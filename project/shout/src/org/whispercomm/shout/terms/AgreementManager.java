
package org.whispercomm.shout.terms;

import org.whispercomm.shout.customwidgets.DialogFactory;
import org.whispercomm.shout.customwidgets.ShoutAgreementView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class AgreementManager {

	private static final String AGREEMENT = "agreement_prefs";
	private static final String KEY_AGREED = "has_agreed";
	protected static final String TAG = AgreementManager.class.getSimpleName();

	public static void showAgreementIfNotAccepted(Context displayContext, AppKiller killer) {
		if (!hasUserAgreedToTerms(displayContext.getApplicationContext())) {
			showAgreement(displayContext, killer);
		}
	}

	private static boolean hasUserAgreedToTerms(Context applicationContext) {
		SharedPreferences appSharedPrefs = applicationContext.getSharedPreferences(AGREEMENT, 0);
		boolean agreed = appSharedPrefs.getBoolean(KEY_AGREED, false);
		return agreed;
	}

	private static void recordUserAgreement(Context context) {
		SharedPreferences appSharedPrefs = context.getApplicationContext().getSharedPreferences(
				AGREEMENT, 0);
		Editor editor = appSharedPrefs.edit();
		editor.putBoolean(KEY_AGREED, true);
		editor.commit();
		Log.v(TAG, "User agreed to terms");
	}

	private static void showAgreement(final Context context, final AppKiller killer) {
		DialogInterface.OnClickListener positive = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				recordUserAgreement(context);
			}

		};
		DialogInterface.OnClickListener negative = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.v(TAG, "User did not agree to the terms");
				killer.killSelf();
			}

		};
		AlertDialog agreement = DialogFactory.buildUserAgreementDialog(context,
				new ShoutAgreementView(context), positive, negative);
		agreement.show();
	}
}
