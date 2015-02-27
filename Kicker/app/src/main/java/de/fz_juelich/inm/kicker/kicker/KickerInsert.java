package de.fz_juelich.inm.kicker.kicker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class KickerInsert extends ActionBarActivity implements View.OnClickListener {

    TableLayout table;
    Player[] players;
    Button[] nameButtons;
    Menu mainmenu;

    List<Button> all = Arrays.asList(new Button[4]);

    ImageButton plus;

    RequestQueue queue;

    HashMap<Button, Player> buttonPlayerMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("KickerInset", "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kicker_insert);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.fun_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        queue = Volley.newRequestQueue(this);
        buttonPlayerMap = new HashMap<>();
    }

    @Override
    protected void onStart(){
        super.onStart();

        table = (TableLayout) findViewById(R.id.table);
        plus = (ImageButton) findViewById(R.id.plus);
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void refresh(){
        table.removeAllViews();
        nameButtons = null;
        players = null;
        all = Arrays.asList(new Button[4]);
        plus.setEnabled(false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://dper.de:9898/getplayers/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("refresh_request", "response: " + response);
                try {
                    JSONArray aPlayers = new JSONArray(response);
                    players = new Player[aPlayers.length()];
                    for (int i = 0; i < aPlayers.length(); i++){
                        JSONObject oPlayer = aPlayers.getJSONObject(i);
                        players[i] = new Player(oPlayer.getInt("id"), oPlayer.getString("name"), oPlayer.getInt("score"));
                    }
                    Arrays.sort(players);
                    createTable();
                }
                catch(JSONException e){
                    Log.i("refresh_json", "JSONException when getting players.");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("refresh_request", "volley error: " + error.getMessage());
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


        String request_url = "http://dper.de:9898/getcurrentgame/";
        Log.i("refresh", "url: " + request_url);
        StringRequest stringRequest_currentgame = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("refresh_getcurrentgame_request", "response: " + response);
                if (response.contentEquals("-1")) {
                    mainmenu.getItem(0).setEnabled(false);
                    mainmenu.getItem(0).setVisible(false);
                }
                else {
                    mainmenu.getItem(0).setEnabled(true);
                    mainmenu.getItem(0).setVisible(true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("refresh_getcurrentgame_request", "volley error: " + error.getMessage());
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
        queue.getCache().clear();
        queue.add(stringRequest_currentgame);
        queue.add(stringRequest);
    }

    void createTable(){

        nameButtons = new Button[players.length];
        TableRow row = new TableRow(this);

        for (int i = 0; i < nameButtons.length; i++){
            nameButtons[i] = new Button(this);
            nameButtons[i].setMinimumWidth(table.getWidth()/3);
            nameButtons[i].setMinimumHeight(table.getWidth()/3);
            nameButtons[i].setOnClickListener(this);
            nameButtons[i].setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    deletePlayer(buttonPlayerMap.get(((Button) v)).id);
                    return false;
                }
            });
            nameButtons[i].setText(players[i].name + "\n" + players[i].score);
            buttonPlayerMap.put(nameButtons[i], players[i]);
            if (i % 3 == 0){
                row = new TableRow(this);
                row.setMinimumWidth(table.getWidth());
                row.addView(nameButtons[i]);
            }
            else{
                row.addView(nameButtons[i]);
                if (i % 3 == 2){
                    table.addView(row);
                }
            }
        }

        if (nameButtons.length % 3 != 0){
            table.addView(row);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kicker_insert, menu);
        mainmenu = menu;
        // disable button which shows current game
        mainmenu.getItem(0).setEnabled(false);
        mainmenu.getItem(0).setVisible(false);
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
        if (id == R.id.addplayer_menu) {
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

                    Log.i("addplayer_menu", "url: " + request_url);
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("addplayer_request", "response: " + response);
                            refresh();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("addplayer_request", "volley error: " + error.getMessage());
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
            });

            builder.show();
            return true;
        }
        else if (id == R.id.refresh_menu) {
            refresh();
            return true;
        }
        else if (id == R.id.currentgame_menu) {
            Intent intent = new Intent(getApplicationContext(), EnterScoreActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.restartgame_menu) {
            Log.i("restartgame_menu", "restarting game");
            String url = "http://dper.de:9898/restartgame/";
            Log.i("startGame", "url: " + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("restartgame_menu_request", "response: " + response);
                    if (Integer.parseInt(response) == -1){
                        Toast errortoast = Toast.makeText(getApplicationContext(), "Game already running.", Toast.LENGTH_LONG);
                        errortoast.show();
                    }
                    Intent intent = new Intent(getApplicationContext(), EnterScoreActivity.class);
                    startActivity(intent);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("restartgame_menu_request", "volley error: " + error.getMessage());
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_kicker_insert, container, false);
        }
    }

    public void onClick(View v){
        Button b = (Button) v;
        Log.i("player button", b.getText().toString());

        // create new list with winners AND losers to simplify search
        int index = all.indexOf(b);
        if (index == -1){
            // if entry does not exists, create new
            int newindex = all.indexOf(null);
            if (newindex != -1) {
                // if a slot is open
                all.set(newindex, b);
                if (newindex >= 0 && newindex < 2) {
                    b.getBackground().setColorFilter(0xA0FF0000, PorterDuff.Mode.MULTIPLY);
                }
                else if (newindex >= 2 && newindex < 4) {
                    b.getBackground().setColorFilter(0xA0000000, PorterDuff.Mode.MULTIPLY);
                }
            }
        }
        else {
            // if entry does already exists, clear entry
            all.set(index, null);
            if (index >= 0 && index < 2) {
                b.getBackground().clearColorFilter();
            }
            else if (index >= 2 && index < 4) {
                b.getBackground().clearColorFilter();
            }
        }

        if (!all.contains(null)) {
            plus.setEnabled(true);
        }
        else{
            plus.setEnabled(false);
        }

    }

    public void startGame(View v){
        Log.i("startGame", "starting game");
        String url = "http://dper.de:9898/startgame/";
        String request_url = url + buttonPlayerMap.get(all.get(0)).id + "/" + buttonPlayerMap.get(all.get(1)).id +
                                "/" + buttonPlayerMap.get(all.get(2)).id + "/" + buttonPlayerMap.get(all.get(3)).id;
        Log.i("startGame", "url: " + request_url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("startGame_request", "response: " + response);
                if (Integer.parseInt(response) == -1){
                    Toast errortoast = Toast.makeText(getApplicationContext(), "Game already running.", Toast.LENGTH_LONG);
                    errortoast.show();
                }
                Intent intent = new Intent(getApplicationContext(), EnterScoreActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("startGame_request", "volley error: " + error.getMessage());
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

    public void deletePlayer(final int player_id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this player?");
        builder.setPositiveButton("Hell yeah!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String request_url = "http://dper.de:9898/deleteplayer/" + player_id;
                Log.i("deletePlayer", "url: " + request_url);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("deletePlayer_request", "response: " + response);
                        refresh();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("deletePlayer_request", "volley error: " + error.getMessage());
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
        });
        builder.setNegativeButton("Nope.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}

