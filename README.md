Use of Rest APIs is a common pattern to Android applications. Many applications use Android's builtin HttpClient library by default, and Google Gson library to convert raw JSON response to Java Objects. The Spring Android framework has a module RestTemplate that provides a ready-to-use RestClient.

To test Rest based Android applications in isolation, we can provide a custom RequestFactory, that overrides default RestTemplate behavior. This way, we can test-driven develop an entire Android Application, even when server-side counterpart does not yet exist.

This is a sample project using FakeWeb-Android (https://github.com/fabiolnm/FakeWeb-Android-Project) and below there are instructions on how to build a project from scratch using it.


# Instructions


1) Install FakeWeb-Android artifact in your local maven repository.


2) Create new project using android-release archetype

	https://github.com/akquinet/android-archetypes


2.1) In yourproject-it/pom.xml comment-out classifier on apk dependency - it breaks maven test project execution

    <dependency>
      <groupId>yourproject-group</groupId>
      <artifactId>yourproject</artifactId>
      <type>apk</type>
      <version>0.0.1-SNAPSHOT</version>
    <!-- 
      <classifier>${zipaligned-classifier}</classifier>
     -->
    </dependency>


3) Add FakeWeb-Android dependency to dependencyManagement section of yourproject-parent/pom.xml:

	<dependency>
       		<groupId>fakeweb-android</groupId>
          	<artifactId>fakeweb-android</artifactId>
		<version>0.1.0</version>
	</dependency>


4) Add dependency (without version) to dependencies section of yourproject/pom.xml

	<dependency>
		<groupId>fakeweb-android</groupId>
		<artifactId>fakeweb-android</artifactId>
	</dependency>


5) In main project, create a Utility class that inspects activity launch intent and chooses to inject a FakeHttpRequestFactory into RestTemplate:

	public class Util {
		public static RestTemplate restTemplate(final Intent intent) {
			Boolean useFake = intent.getExtras().getBoolean(Constants.USE_FAKE_HTTP_REQUEST_FACTORY);
			if (useFake != null && useFake)
				return new RestTemplate(new FakeHttpRequestFactory());
			return new RestTemplate();
		}
	}


6) In your tests, setup activity launch to send a customized launch intent, to be handled by Utility class.

	public HelloAndroidActivityTest() {
		super(HelloAndroidActivity.class);
		Intent intent = new Intent();
		intent.putExtra(Constants.USE_FAKE_HTTP_REQUEST_FACTORY, true);
		setActivityIntent(intent);
	}


7) Start activity and obtain its underlying RequestFactory (that we faked in step 5):

	private FakeHttpRequestFactory getRequestFactory() {
		return (FakeHttpRequestFactory) getActivity().getRequestFactory();
	}


8) Setup fake responses and use your favorite test framework to verify changes in view (Robotium is used in this example).

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


9) Repeat steps 3 and 4, to add Spring RestTemplate and Gson dependencies to parent and main project, respectively:

	<repositories>
		<repository>
			<id>spring-milestone</id>
			<name>Spring Maven MILESTONE Repository</name>
			<url>http://maven.springframework.org/milestone</url>
		</repository>	
	</repositories>

	...

	<dependency>
		<groupId>org.springframework.android</groupId>
		<artifactId>spring-android-rest-template</artifactId>
		<version>1.0.0.M4</version>
	</dependency> 
	<dependency>
		<groupId>com.google.code.gson</groupId>
		<artifactId>gson</artifactId>
		<version>2.1</version>
	</dependency>


10) Add your activity implementation to make the test pass:
	
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
					// rest is using a fake HttpRequestFactory, URI is irrelevant by now.
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
...
	}

