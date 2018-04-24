package tutorial.firebase.com.bontsi.firapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by ndimphiwe.bontsi on 2018/04/11.
 */

public class FirApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
