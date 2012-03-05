package fakeweb.android.sample;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class HelloAndroidActivity extends Activity {
	private static String TAG = "fakeweb-android-sample";

	private RestTemplate rest;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.main);
		rest = Util.restTemplate(getIntent());

		final TextView tv = (TextView) findViewById(R.id.sample_text);
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(final Void... params) {
				final String response = rest.getForObject("http://dum.my", String.class);
				runOnUiThread(new Runnable() {
					public void run() {
						tv.setText(response);
					}
				});
				return response;
			}
		}.execute();
	}

	public ClientHttpRequestFactory getRequestFactory() {
		return rest.getRequestFactory();
	}
}

