package markdillman.discogsspecapp;

import com.github.scribejava.core.builder.api.DefaultApi10a;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.extractors.OAuth1AccessTokenExtractor;
import com.github.scribejava.core.extractors.OAuth1RequestTokenExtractor;
import com.github.scribejava.core.model.OAuthConfig;

/**
 * Created by markdillman on 3/16/17.
 */

public class DiscogsApi extends DefaultApi10a {
        private static final String AUTHORIZE_URL = "https://www.discogs.com/oauth/authorize";
        @Override
        public String getAuthorizationUrl(OAuth1RequestToken requestToken){
            return String.format(AUTHORIZE_URL, requestToken.getToken());
        }
        protected DiscogsApi(){}

        private static class InstanceHolder{
            private static final DiscogsApi INSTANCE = new DiscogsApi();
        }
        public static DiscogsApi instance(){
            return InstanceHolder.INSTANCE;
        }
        @Override
        public String getAccessTokenEndpoint(){
            return "https://api.discogs.com/oauth/access_token";
        }
        @Override
        public String getRequestTokenEndpoint(){
            return "https://api.discogs.com/oauth/request_token";
        }
}
