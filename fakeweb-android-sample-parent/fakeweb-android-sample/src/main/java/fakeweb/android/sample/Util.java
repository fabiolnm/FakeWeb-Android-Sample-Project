package fakeweb.android.sample;

import org.springframework.web.client.RestTemplate;

import android.content.Intent;
import fakeweb.android.FakeHttpRequestFactory;

public class Util {
	public static RestTemplate restTemplate(final Intent intent) {
		Boolean useFake = intent.getExtras().getBoolean(Constants.USE_FAKE_HTTP_REQUEST_FACTORY);
		if (useFake != null && useFake)
			return new RestTemplate(new FakeHttpRequestFactory());
		return new RestTemplate();
	}
}