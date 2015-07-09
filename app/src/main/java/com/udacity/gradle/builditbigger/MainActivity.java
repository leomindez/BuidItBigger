package com.udacity.gradle.builditbigger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidtalks.androidjokeslibrary.JokeActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.leonelmendez.jokes.backend.myApi.MyApi;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main
        );
    }

    public void tellJoke(View view){
        JokeAsyncTask  jokeAsyncTask = new JokeAsyncTask();
        jokeAsyncTask.execute();
    }


    private class JokeAsyncTask extends AsyncTask<Void,Void,String> {

        private MyApi mService = null;
        private ProgressDialog progressDialog;

        public JokeAsyncTask(){
            progressDialog = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getString(R.string.message_downloading_joke));
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            if(mService == null) {
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport()
                        , new AndroidJsonFactory(), null)
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                mService = builder.build();
            }


            try {
                return mService.randomJoke().execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Intent jokeLibraryIntent = JokeActivity.createIntent(MainActivity.this, s);
            startActivity(jokeLibraryIntent);
        }
    }
}
