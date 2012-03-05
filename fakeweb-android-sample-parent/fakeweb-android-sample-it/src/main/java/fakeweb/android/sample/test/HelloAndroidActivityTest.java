package fakeweb.android.sample.test;

import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import fakeweb.android.FakeHttpRequestFactory;
import fakeweb.android.FakeHttpResponse;
import fakeweb.android.sample.Constants;
import fakeweb.android.sample.HelloAndroidActivity;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<HelloAndroidActivity> {

	public HelloAndroidActivityTest() {
		super(HelloAndroidActivity.class);
		Intent intent = new Intent();
		intent.putExtra(Constants.USE_FAKE_HTTP_REQUEST_FACTORY, true);
		setActivityIntent(intent);
	}

	public void testActivity() {
		HelloAndroidActivity activity = getActivity();

		getRequestFactory().setTimeoutInSeconds(2); // simulates a 2s delayed http response

		String fakeResponseText = "I'm a fake response";
		FakeHttpResponse fakeResponse = new FakeHttpResponse();
		fakeResponse.setResponseBody(fakeResponseText);
		getRequestFactory().setFakeResponse(fakeResponse);

		Solo solo = new Solo(getInstrumentation(), activity);
		assertTrue(solo.searchText(fakeResponseText, true));
	}

	private FakeHttpRequestFactory getRequestFactory() {
		return (FakeHttpRequestFactory) getActivity().getRequestFactory();
	}
}

