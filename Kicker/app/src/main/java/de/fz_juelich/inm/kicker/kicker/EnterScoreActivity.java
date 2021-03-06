package de.fz_juelich.inm.kicker.kicker;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;


public class EnterScoreActivity extends ActionBarActivity {

    RequestQueue queue;
    RatingBar score_red;
    RatingBar score_black;
    TextView player_red0;
    TextView player_red1;
    TextView player_black0;
    TextView player_black1;
    TextView predict_red;
    TextView predict_black;

    String server_url = "http://dper.de";
    int server_port = 9898;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_score);
        queue = Volley.newRequestQueue(this);
        score_red = (RatingBar) findViewById(R.id.score_red);
        LayerDrawable stars_red = (LayerDrawable) score_red.getProgressDrawable();
        stars_red.getDrawable(2).setColorFilter(0xA0FF0000, PorterDuff.Mode.MULTIPLY);
        score_black = (RatingBar) findViewById(R.id.score_black);
        LayerDrawable stars_black = (LayerDrawable) score_black.getProgressDrawable();
        stars_black.getDrawable(2).setColorFilter(0xA0000000, PorterDuff.Mode.MULTIPLY);
        player_red0 = (TextView) findViewById(R.id.player_red0);
        player_red1 = (TextView) findViewById(R.id.player_red1);
        predict_red = (TextView) findViewById(R.id.predict_red);
        player_black0 = (TextView) findViewById(R.id.player_black0);
        player_black1 = (TextView) findViewById(R.id.player_black1);
        predict_black = (TextView) findViewById(R.id.predict_black);

        String request_url = server_url+":"+server_port+"/getcurrentgame/";
        Log.i("onCreate", "url: " + request_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("onCreate_request", "response: " + response);
                try {
                    JSONObject JSONresponse = new JSONObject(response);
                    JSONObject oPlayers = new JSONObject(JSONresponse.getString(("current_game")));
                    player_red0.setText(oPlayers.getString("red0_name"));
                    player_red1.setText(oPlayers.getString("red1_name"));
                    player_red0.setBackgroundColor(0xA0FF0000);
                    player_red1.setBackgroundColor(0xA0FF0000);

                    player_black0.setText(oPlayers.getString("black0_name"));
                    player_black1.setText(oPlayers.getString("black1_name"));
                    player_black0.setBackgroundColor(0xA0000000);
                    player_black1.setBackgroundColor(0xA0000000);

                    double prob_red = Float.parseFloat(JSONresponse.getString("prob"));
                    StringBuilder _predict_red = new StringBuilder();
                    _predict_red.append("P = ");
                    _predict_red.append(String.format("%.2f",prob_red));
                    _predict_red.append(", points to win : ");
                    _predict_red.append(JSONresponse.getString(("points_red")));
                    predict_red.setText(_predict_red);
                    double prob_black = 1. - Float.parseFloat(JSONresponse.getString("prob"));
                    StringBuilder _predict_black = new StringBuilder();
                    _predict_black.append("P = ");
                    _predict_black.append(String.format("%.2f",prob_black));
                    _predict_black.append(", points to win : ");
                    _predict_black.append(JSONresponse.getString(("points_black")));
                    predict_black.setText(_predict_black);
                }
                catch(JSONException e) {
                    Log.i("getcurrentgame_request", "JSONException when getting current game.");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("onCreate_request", "volley error: " + error.getMessage());
                CharSequence errortext;
                if (error.getCause() instanceof UnknownHostException) {
                    errortext = "Connection error.";
                }
                else{
                    errortext = "Unknown error.";
                }
                Toast errortoast = Toast.makeText(getApplicationContext(), errortext, Toast.LENGTH_SHORT);
                errortoast.show();
            }
        });
        queue.add(stringRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_score, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.i("menuitem", String.valueOf(id));

        //noinspection SimplifiableIfStatement
        if (id == R.id.cancelgame_menu) {
            String request_url = server_url+":"+server_port+"/cancelcurrentgame/";
            Log.i("onOptionsItemSelected", "url: " + request_url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("onOptionsItemSelected_request", "response: " + response);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("onOptionsItemSelected_request", "volley error: " + error.getMessage());
                    CharSequence errortext;
                    if (error.getCause() instanceof UnknownHostException) {
                        errortext = "Connection error.";
                    }
                    else{
                        errortext = "Unknown error.";
                    }
                    Toast errortoast = Toast.makeText(getApplicationContext(), errortext, Toast.LENGTH_SHORT);
                    errortoast.show();
                }
            });
            queue.add(stringRequest);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void endGame(View v){
        Log.i("endGame", "ending game");
        String url = server_url+":"+server_port+"/endgame/";
        RatingBar score_red = (RatingBar) findViewById(R.id.score_red);
        RatingBar score_black = (RatingBar) findViewById(R.id.score_black);
        if ((score_red.getRating() == 6) ^ (score_black.getRating() == 6)) {
            String request_url = url + String.valueOf(score_red.getRating()) + "/" + String.valueOf(score_black.getRating());
            Log.i("endGame", "url: " + request_url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("endGame_request", "response: " + response);
                    if (Integer.parseInt(response) == 0){
                        finish();
                    }
                    else{
                        Toast errortoast = Toast.makeText(getApplicationContext(), "Unknown response.", Toast.LENGTH_SHORT);
                        errortoast.show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    CharSequence errortext;
                    Log.i("endGame_request", "volley error: " + error.getMessage());
                    if (error.getCause() instanceof UnknownHostException) {
                        errortext = "Connection error.";
                    }
                    else{
                        errortext = "Unknown error.";
                    }
                    Toast errortoast = Toast.makeText(getApplicationContext(), errortext, Toast.LENGTH_SHORT);
                    errortoast.show();
                }
            });
            queue.add(stringRequest);
        }
        else{
            Toast errortoast = Toast.makeText(this, "Invalid score, dummy!", Toast.LENGTH_SHORT);
            errortoast.show();
        }

	if ((score_red.getRating()==6 & score_black.getRating()==0) ^ (score_red.getRating()==0 & score_black.getRating()==6)){
	    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bazinga);
	    mp.start();
	}

    }

}
