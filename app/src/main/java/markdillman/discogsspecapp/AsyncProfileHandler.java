package markdillman.discogsspecapp;

import android.os.AsyncTask;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;
import java.lang.StringBuilder;

/**
 * Created by markdillman on 3/18/17.
 */

//param order: Token, Service, Url
public class AsyncProfileHandler extends AsyncTask<Object,Void,Response> {

    public AsyncResponse delegate = null;

    public AsyncProfileHandler(AsyncResponse delegate){
        this.delegate = delegate;
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
    @Override
    protected Response doInBackground(Object... params) {
        OAuth1AccessToken mAccessToken = (OAuth1AccessToken) params[0];
        OAuth10aService mService = (OAuth10aService) params[1];
        String url = urlHelper((String)params[2]);

        final OAuthRequest personaRequest = new OAuthRequest(Verb.GET,url,mService);
        personaRequest.addOAuthParameter("oauth_token_secret",mAccessToken.getParameter("oauth_token_secret"));
        personaRequest.addHeader("User-Agent","Discogeronomy/1.0");
        mService.signRequest(mAccessToken, personaRequest);
        final Response response = personaRequest.send();
        return response;
    }

    //appends query to URL
    private String urlHelper(String url){
        StringBuilder finalUrl = new StringBuilder();
        finalUrl.append(url);
        finalUrl.append("?");
        return finalUrl.toString();
    }
}
