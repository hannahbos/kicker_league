package de.fz_juelich.inm.kicker.kicker;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by weidel on 15.02.15.
 */
public class Kicker extends Application {

    @Override
    public void onCreate(){
        Parse.initialize(this, "8VWYCZEvlRgpevlb1xwgFsxrzGG6zOPnnfz9hGLV", "RQ9tdA7ZrQjXhYi5e0pLue7Pkr1j8EkR003YR1rE");
    }
}
