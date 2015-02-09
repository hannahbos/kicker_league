package de.fz_juelich.inm.kicker.kicker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;

import java.util.HashMap;


public class KickerInsert extends ActionBarActivity implements View.OnClickListener {

    TableLayout table;
    Player[] players;
    Button[] nameButtons;

    Button[] winners = new Button[2];
    Button[] losers = new Button[2];

    ImageButton plus;

    RequestQueue queue;

    HashMap<Button, Player> buttonPlayerMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kicker_insert);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        queue = Volley.newRequestQueue(this);
        buttonPlayerMap = new HashMap<Button, Player>();

    }

    @Override
    protected void onStart(){
        super.onStart();

        table = (TableLayout) findViewById(R.id.table);
        plus = (ImageButton) findViewById(R.id.plus);
        refresh();

    }

    void refresh(){
        table.removeAllViews();
        nameButtons = null;
        players = null;
        winners = new Button[2];
        losers = new Button[2];
        plus.setEnabled(false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://dper.de:9898/getplayers/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("request", "response: " + response);
                try {
                    JSONArray aPlayers = new JSONArray(response);
                    players = new Player[aPlayers.length()];
                    for (int i = 0; i < aPlayers.length(); i++){
                        JSONObject oPlayer = aPlayers.getJSONObject(i);
                        players[i] = new Player(oPlayer.getInt("id"), oPlayer.getString("name"), oPlayer.getInt("score"));
                    }
                    createTable();
                }
                catch(JSONException e){

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("request", "something went wrong! " + error.getMessage());
            }
        });
        queue.add(stringRequest);

    }

    void createTable(){

        nameButtons = new Button[players.length];
        TableRow row = new TableRow(this);

        for (int i = 0; i < nameButtons.length; i++){
            nameButtons[i] = new Button(this);
            nameButtons[i].setMinimumWidth(table.getWidth()/2);
            nameButtons[i].setMinimumHeight(table.getWidth()/2);
            nameButtons[i].setOnClickListener(this);
            nameButtons[i].setText(players[i].name + " " + players[i].score);
            buttonPlayerMap.put(nameButtons[i], players[i]);
            if (i % 2 == 0){
                row = new TableRow(this);
                row.setMinimumWidth(table.getWidth());
                row.addView(nameButtons[i]);
            }
            else{
                row.addView(nameButtons[i]);
                table.addView(row);
            }
        }

        if (nameButtons.length % 2 == 1){
            table.addView(row);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kicker_insert, menu);
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
            Log.i("menu", "add player");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add new player");
            builder.setMessage("Type name");
            final EditText name = new EditText(this);
            builder.setView(name);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = "http://dper.de:9898/addplayer/";
                    String request_url = url + name.getText();

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
                    refresh();
                }
            });

            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_kicker_insert, container, false);
            return rootView;
        }
    }

    public void onClick(View v){
        Button b = (Button) v;
        Log.i("blub", b.getText().toString());


        if (b.equals(winners[0])){
            winners[0] = null;
            b.getBackground().clearColorFilter();
        }
        else if (winners[1] == b){
            winners[1] = null;
            b.getBackground().clearColorFilter();

        }
        else if (losers[0] == b){
            losers[0] = null;
            b.getBackground().clearColorFilter();

        }
        else if (losers[1] == b){
            losers[1] = null;
            b.getBackground().clearColorFilter();
        }


        else if (winners[0] == null){
            winners[0] = b;
            b.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }
        else if (winners[1] == null){
            winners[1] = b;
            b.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        }
        else if (losers[0] == null){
            losers[0] = b;
            b.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }
        else if (losers[1] == null){
            losers[1] = b;
            b.getBackground().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
        }

        if (winners[0] != null && winners[1] != null && losers[0] != null && losers[1] != null){
            plus.setEnabled(true);
        }
        else{
            plus.setEnabled(false);
        }

    }

    public void addGame(View v){
        Log.i("bla", "ADD GAME");
        String url = "http://dper.de:9898/addgame/";
        String request_url = url + buttonPlayerMap.get(winners[0]).id + "/" + buttonPlayerMap.get(winners[1]).id +
                                "/" + buttonPlayerMap.get(losers[0]).id + "/" + buttonPlayerMap.get(losers[1]).id + "/" + "0"; //TODO transmit real value for the score of the game instead of 0


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

        refresh();

    }

}

