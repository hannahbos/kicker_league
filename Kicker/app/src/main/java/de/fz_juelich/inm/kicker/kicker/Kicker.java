package de.fz_juelich.inm.kicker.kicker;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.SaveCallback;

/**
 * Created by weidel on 15.02.15.
 */
public class Kicker extends Application {

    @Override
    public void onCreate(){
        Parse.initialize(this, "8VWYCZEvlRgpevlb1xwgFsxrzGG6zOPnnfz9hGLV", "RQ9tdA7ZrQjXhYi5e0pLue7Pkr1j8EkR003YR1rE");
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

    }
}
