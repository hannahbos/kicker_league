package de.fz_juelich.inm.kicker.kicker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    public Menu mainmenu;

    RequestQueue queue;

    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("KickerInset", "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kicker_insert);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.fun_icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        //fragment.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                            //refresh();
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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            Log.i("POSITION",String.valueOf(position));
            if (position==0)
            {
                PlayersRankingFragment fragment = new PlayersRankingFragment();
                return fragment;
            }
            else if (position==1)
            {
                StartGameFragment fragment = new StartGameFragment();
                return fragment;
            }
            else {
                return null;
            }

        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_start_game).toUpperCase(l);
                case 1:
                    return getString(R.string.title_players_ranking).toUpperCase(l);
//                case 2:
//                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StartGameFragment extends Fragment implements View.OnClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        TableLayout table;
        Player[] players;
        Button[] nameButtons;
        List<Button> all = Arrays.asList(new Button[4]);
        ImageButton plus;
        RequestQueue queue;

        HashMap<Button, Player> buttonPlayerMap;
        Context thiscontext;
        SwipeRefreshLayout swipeView;
        ScrollView scroll;

        public StartGameFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            thiscontext = container.getContext();
            final View view = inflater.inflate(R.layout.fragment_kicker_insert, container, false);

            plus = (ImageButton) view.findViewById(R.id.plus);
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startGame(view);
                }
            });
            table = (TableLayout) view.findViewById(R.id.table);
            swipeView = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
            scroll = (ScrollView) view.findViewById(R.id.scrollView);
            queue = Volley.newRequestQueue(thiscontext);
            plus = (ImageButton) view.findViewById(R.id.plus);
            buttonPlayerMap = new HashMap<>();
            refresh();

            scroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {
                    int scrollY = scroll.getScrollY(); //for verticalScrollView
                    swipeView.setEnabled(scrollY == 0);
                }
            });


            return view;
        }


        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

                /**
             * Implement {@link SwipeRefreshLayout.OnRefreshListener}. When users do the "swipe to
             * refresh" gesture, SwipeRefreshLayout invokes
             * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}. In
             * {@link SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}, call a method that
             * refreshes the content. Call the same method in response to the Refresh action from the
             * action bar.
             */
            swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeView.setRefreshing(true);
                    Log.i("Log: ", "onRefresh called from SwipeRefreshLayout");
                    refresh();
                    swipeView.setRefreshing(false);
                    //initiateRefresh();
                }
            });
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
                            players[i] = new Player(oPlayer.getInt("id"), oPlayer.getString("name"), oPlayer.getInt("score"), oPlayer.getInt("elo"));
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
                    Toast errortoast = Toast.makeText(getActivity().getApplicationContext(), errortext, Toast.LENGTH_SHORT);
                    errortoast.show();
                }
            });


//            String request_url = "http://dper.de:9898/getcurrentgame/";
//            Log.i("refresh", "url: " + request_url);
//            StringRequest stringRequest_currentgame = new StringRequest(Request.Method.GET, request_url, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.i("refresh_getcurrentgame_request", "response: " + response);
//                    if (response.contentEquals("-1")) {
//                        mainmenu.getItem(0).setEnabled(false);
//                        mainmenu.getItem(0).setVisible(false);
//                    }
//                    else {
//                        mainmenu.getItem(0).setEnabled(true);
//                        mainmenu.getItem(0).setVisible(true);
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.i("refresh_getcurrentgame_request", "volley error: " + error.getMessage());
//                    CharSequence errortext;
//                    if (error.getCause() instanceof UnknownHostException) {
//                        errortext = "Connection error.";
//                    }
//                    else{
//                        errortext = "Unknown error.";
//                    }
//                    Toast errortoast = Toast.makeText(getActivity().getApplicationContext(), errortext, Toast.LENGTH_SHORT);
//                    errortoast.show();
//                }
//            });
            queue.getCache().clear();
//            queue.add(stringRequest_currentgame);
            queue.add(stringRequest);
        }

        void createTable(){

            nameButtons = new Button[players.length];
            TableRow row = new TableRow(getActivity());

            for (int i = 0; i < nameButtons.length; i++){
                nameButtons[i] = new Button(getActivity());
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
                nameButtons[i].setText(players[i].name);
                buttonPlayerMap.put(nameButtons[i], players[i]);
                if (i % 3 == 0){
                    row = new TableRow(getActivity());
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

        public void deletePlayer(final int player_id){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            //refresh();
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
                            Toast errortoast = Toast.makeText(getActivity().getApplicationContext(), errortext, Toast.LENGTH_SHORT);
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
                        Toast errortoast = Toast.makeText(getActivity().getApplicationContext(), "Game already running.", Toast.LENGTH_LONG);
                        errortoast.show();
                    }
                    Intent intent = new Intent(getActivity().getApplicationContext(), EnterScoreActivity.class);
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
                    Toast errortoast = Toast.makeText(getActivity().getApplicationContext(), errortext, Toast.LENGTH_SHORT);
                    errortoast.show();
                }
            });
            queue.add(stringRequest);
        }
    }



    public static class PlayersRankingFragment extends Fragment {

        TableLayout table;
        Player[] players;
        Context thiscontext;
        RequestQueue queue;
        ScrollView scroll;
        SwipeRefreshLayout swipeView;

        public PlayersRankingFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_players_ranking, container, false);
            thiscontext = container.getContext();
            table = (TableLayout) view.findViewById(R.id.table);
            scroll = (ScrollView) view.findViewById(R.id.scrollView);
            queue = Volley.newRequestQueue(thiscontext);
            //refresh();

            return view;
        }

        @Override
        public void onStart() {
            Log.i("Ranking OnSTART!!!","OH YEAH!");
            refresh();
            super.onStart();
        }

        public void onClick(View v) {
            Button b = (Button) v;
            Log.i("player button", b.getText().toString());
        }

        void refresh(){
            table.removeAllViews();
            players = null;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://dper.de:9898/getplayers/", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("refresh_request", "response: " + response);
                    try {
                        JSONArray aPlayers = new JSONArray(response);
                        players = new Player[aPlayers.length()];
                        for (int i = 0; i < aPlayers.length(); i++){
                            JSONObject oPlayer = aPlayers.getJSONObject(i);
                            players[i] = new Player(oPlayer.getInt("id"), oPlayer.getString("name"), oPlayer.getInt("score"), oPlayer.getInt("elo"));
                        }
                        Comparator<Player> comp = new EloComparator();
                        Arrays.sort(players, comp);

                        createRanking();
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
                    Toast errortoast = Toast.makeText(getActivity().getApplicationContext(), errortext, Toast.LENGTH_SHORT);
                    errortoast.show();
                }
            });


            queue.getCache().clear();
            queue.add(stringRequest);
        }

        void createRanking(){
            Log.i("PlayersRanking","createRanking");

            TableRow row = new TableRow(thiscontext);
            row.setMinimumWidth(table.getWidth());
            row.setMinimumHeight(table.getHeight()/(players.length+3));
            table.addView(row);

            row = new TableRow(thiscontext);
            row.setMinimumWidth(table.getWidth());
            row.setMinimumHeight(table.getHeight()/(players.length+3));
            TextView tv = new TextView(thiscontext);
            tv.setTextAppearance(getActivity().getApplicationContext(), R.style.RankingTitle);
            tv.setText(" ");
            tv.setGravity(Gravity.RIGHT);
            tv.setGravity(Gravity.BOTTOM);
            tv.setWidth(table.getWidth()*3/8);
            row.addView(tv);

            tv = new TextView(thiscontext);
            tv.setText("Player");
            tv.setTextAppearance(getActivity().getApplicationContext(), R.style.RankingTitle);
            tv.setGravity(Gravity.BOTTOM);
            tv.setWidth(table.getWidth()*3/8);
            row.addView(tv);

            tv = new TextView(thiscontext);
            tv.setText(" ELO");
            tv.setTextAppearance(getActivity().getApplicationContext(), R.style.RankingTitle);
            tv.setGravity(Gravity.BOTTOM);
            tv.setWidth(table.getWidth()*2/8);

            row.addView(tv);
            table.addView(row);


            for (int i = 0; i < players.length; i++){
                row = new TableRow(thiscontext);
                row.setMinimumWidth(table.getWidth());
                row.setMinimumHeight(table.getHeight()/(players.length+3));

                tv = new TextView(thiscontext);
                tv.setTextAppearance(getActivity().getApplicationContext(), R.style.RankingEntry);
                tv.setText(i+1+".");
                tv.setGravity(Gravity.CENTER);
                tv.setWidth(table.getWidth() * 3 / 8);
                row.addView(tv);

                tv = new TextView(thiscontext);
                tv.setTextAppearance(getActivity().getApplicationContext(), R.style.RankingEntry);
                tv.setText(players[i].name);
                tv.setWidth(table.getWidth()*3/8);
                row.addView(tv);

                tv = new TextView(thiscontext);
                tv.setTextAppearance(getActivity().getApplicationContext(), R.style.RankingEntry);
                tv.setText(" "+players[i].elo);
                tv.setWidth(table.getWidth()*2/8);
                row.addView(tv);
                if (i % 2 ==0) {
                    row.setBackgroundColor(Color.LTGRAY);
                }


                table.addView(row);
            }
        }


    }


}

