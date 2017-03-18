package markdillman.discogsspecapp;

import android.os.AsyncTask;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;

/**
 * Created by markdillman on 3/18/17.
 */

public class AsyncIdHandler extends AsyncTask<Object,Void,Response> {

    public AsyncResponse delegate = null;

    public AsyncIdHandler(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Response doInBackground(Object... params) {

        OAuth1AccessToken mAccessToken = (OAuth1AccessToken)params[0];
        OAuth10aService mService = (OAuth10aService)params[1];

        final OAuthRequest personaRequest = new OAuthRequest(Verb.GET,"https://api.discogs.com/oauth/identity?",mService);
        personaRequest.addOAuthParameter("oauth_token_secret",mAccessToken.getParameter("oauth_token_secret"));
        personaRequest.addHeader("User-Agent","Discogeronomy/1.0");
        mService.signRequest((OAuth1AccessToken)mAccessToken, personaRequest);
        final Response response = personaRequest.send();
        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        String body=null;
        try {
            body = response.getBody();
        } catch (IOException e) {
            e.printStackTrace();
        }
        delegate.processFinish(body);
    }
}
