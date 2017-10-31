package com.example.coffeevector.aladdinhamilton2;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    PNConfiguration pnC = new PNConfiguration();
    PubNub pubnub;
    String channelName = "gavino";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pnC.setSubscribeKey("sub-c-0f7ca070-bc72-11e7-acda-62870583ed84");
        pnC.setPublishKey("pub-c-33bdfa69-b688-4d4c-9933-1f4c345d816e");
        pnC.setSecure(false);
        final PNCallback cb = new PNCallback() {
            @Override
            public void onResponse(Object result, PNStatus status) {

            }
        };
        pubnub = new PubNub(pnC);
        pubnub.subscribe().channels(Arrays.asList("gavino")).execute();
        pubnub.publish().message("starsOn").channel(channelName).async(cb);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pubnub.publish().message("treausureOn").channel(channelName).shouldStore(true).usePOST(true).async(cb);
                pubnub.publish().message("musicOn").channel(channelName).shouldStore(true).usePOST(true).async(cb);
                Snackbar.make(view, "♫I am not throw'n away my Shot...♫", Snackbar.LENGTH_LONG).setAction("Action",null).show();
            }
        });

        Button killSwitch = (Button) findViewById(R.id.killButton);
        killSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pubnub.publish().message("kill").channel(channelName).shouldStore(true).usePOST(true).async(cb);
                Snackbar.make(view,"!   !   !   !   !   !   !   !   ",Snackbar.LENGTH_LONG).setAction("Action",null).show();;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
