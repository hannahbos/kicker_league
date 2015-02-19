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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class EnterScoreActivity extends ActionBarActivity {

    RequestQueue queue;
    RatingBar score_red;
    RatingBar score_black;

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void endGame(View v){
        Log.i("endGame", "ending game");
        String url = "http://dper.de:9898/endgame/";
        RatingBar score_red = (RatingBar) findViewById(R.id.score_red);
        RatingBar score_black = (RatingBar) findViewById(R.id.score_black);
        if ((score_red.getRating() == 6) ^ (score_black.getRating() == 6)) {
            String request_url = url + String.valueOf(score_red.getRating()) + "/" + String.valueOf(score_black.getRating());

            Log.i("url", request_url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("request", "response: " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("request", "something went wrong! " + error.getMessage());
                }
            });
            queue.add(stringRequest);
            finish();
        }
        else{
            Toast error = Toast.makeText(this, "Invalid score, dummy!", Toast.LENGTH_SHORT);
            error.show();
        }
    }

}
