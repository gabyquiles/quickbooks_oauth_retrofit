package gabyquiles.com.quickbooksoauthretrofit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private final OAuth10aService s = new ServiceBuilder()
            .apiKey(Constants.API_KEY)
            .apiSecret(Constants.API_SECRET)
            .callback(Constants.CALLBACKURL)
            .build(QuickBooksApi.SSL.instance());


    private static OAuth1RequestToken requestToken;

    Button loginButton;
    TextView tokenView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.loginButton);
        tokenView = (TextView) findViewById(R.id.token);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Login", Toast.LENGTH_LONG).show();
                String authUrl = null;
                try {
                    authUrl = new MainActivity.authUrl().execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Uri uri = this.getIntent().getData();
        SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);

        if (settings.getString("accessToken", null) != null && settings.getString("accessSecret", null) != null){
            String token =  settings.getString("accessToken", null);
            tokenView.setText(token);

            Toast.makeText(getApplicationContext(), "Token in prefs", Toast.LENGTH_LONG).show();

        } else {
            // if shared settings are not set / check whether the uri is valid to do an OAuth Dance
            if (uri != null && uri.toString().startsWith(Constants.CALLBACKURL)) {
                Log.v("onResume", "callback is valid");
                Log.v("uri", uri.toString());
                String verifier = uri.getQueryParameter("oauth_verifier");
                new OauthEnd().execute(verifier);

                Toast.makeText(getApplicationContext(), "Token Authorized", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class authUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                requestToken = s.getRequestToken();
                final String authorizationUrl = s.getAuthorizationUrl(requestToken);
                Log.v("Wepa", authorizationUrl);
                return authorizationUrl;
            }catch (Exception e) {

            }
            return  null;
        }

    }

    private class OauthEnd extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, 0);
            final SharedPreferences.Editor editor = settings.edit();

            final String verifier = params[0];
            Log.v("R", requestToken.getRawResponse());
            // Setup storage for access token
            try {
                final Token accessToken =  s.getAccessToken(requestToken, verifier);


                editor.putString("accessToken", accessToken.getParameter("oauth_token"));
                editor.putString("accessSecret", accessToken.getParameter("oauth_token_secret"));
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
