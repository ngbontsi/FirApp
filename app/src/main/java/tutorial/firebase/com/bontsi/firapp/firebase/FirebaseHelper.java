package tutorial.firebase.com.bontsi.firapp.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import tutorial.firebase.com.bontsi.firapp.model.Spacecraft;

/**
 * Created by ndimphiwe.bontsi on 2018/04/12.
 */

public class FirebaseHelper {
    DatabaseReference db;
    Boolean saved = false;

    ArrayList<String> spacecrafts = new ArrayList<String>();

    public FirebaseHelper(DatabaseReference db) {
        this.db = db;
    }


    public  Boolean save(Spacecraft spacecraft){

        if(spacecraft==null){
            saved = false;
        }else {
            try {
                db.child("Spacecraft").push().setValue(spacecraft);
                saved = true;
            }catch (DatabaseException e){
                e.printStackTrace();
                saved = false;
            }


        }
        return  saved;
    }


    public ArrayList<String> retrieve(){
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                fetchData(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                fetchData(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return  spacecrafts;
    }

    private void fetchData(DataSnapshot dataSnapshot){

        spacecrafts.clear();
        for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
            String name = snapshot.getValue(Spacecraft.class).getName();
            spacecrafts.add(name);
        }

    }
}
