package gabyquiles.com.quickbooksoauthretrofit;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.model.OAuth1RequestToken;

/**
 * Created by gabyq on 2/28/2017.
 */

public class QuickBooksApi extends DefaultApi10a {

    private static final String AUTHORIZE_URL = "https://appcenter.intuit.com/Connect/Begin?oauth_token=%s";
    private static final String REQUEST_TOKEN_RESOURCE = "oauth.intuit.com/oauth/v1/get_request_token";
    private static final String ACCESS_TOKEN_RESOURCE = "oauth.intuit.com/oauth/v1/get_access_token";

    protected QuickBooksApi() {
    }

    private static class InstanceHolder {
        private static final QuickBooksApi INSTANCE = new QuickBooksApi();
    }

    public static QuickBooksApi instance() {
        return InstanceHolder.INSTANCE;
    }


    @Override
    public String getAccessTokenEndpoint()
    {
        return "http://" + ACCESS_TOKEN_RESOURCE;
    }

    @Override
    public String getRequestTokenEndpoint()
    {
        return "http://" + REQUEST_TOKEN_RESOURCE;
    }

    @Override
    public String getAuthorizationUrl(OAuth1RequestToken requestToken)
    {
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }

    public static class SSL extends QuickBooksApi
    {

        private static class InstanceHolder {
            private static final SSL INSTANCE = new SSL();
        }

        public static SSL instance() {
            return InstanceHolder.INSTANCE;
        }

        @Override
        public String getAccessTokenEndpoint()
        {
            return "https://" + ACCESS_TOKEN_RESOURCE;
        }

        @Override
        public String getRequestTokenEndpoint()
        {
            return "https://" + REQUEST_TOKEN_RESOURCE;
        }
    }

    /**
     * QuickBooks 'friendlier' authorization endpoint for OAuth.
     *
     * Uses SSL.
     */
    public static class Authenticate extends SSL
    {
        private static final String AUTHENTICATE_URL = "https://appcenter.intuit.com/Connect/Begin?oauth_token=%s";

        @Override
        public String getAuthorizationUrl(OAuth1RequestToken requestToken)
        {
            return String.format(AUTHENTICATE_URL, requestToken.getToken());
        }
    }

    /**
     * Just an alias to the default (SSL) authorization endpoint.
     *
     * Need to include this for symmetry with 'Authenticate' only.
     */
    public static class Authorize extends SSL{}
}