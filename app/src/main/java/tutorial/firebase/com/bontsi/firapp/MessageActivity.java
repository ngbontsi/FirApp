package tutorial.firebase.com.bontsi.firapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import tutorial.firebase.com.bontsi.firapp.model.Message;
import tutorial.firebase.com.bontsi.firapp.model.User;

public class MessageActivity extends AppCompatActivity {

    private static final String TAG ="MessageActivity";
    private static final String REQUIRED= "Required";
    private Button btnBack;
    private Button btnSend;
    private EditText edtSentText;
    private TextView tvAuthor;
    private TextView tvTime;
    private TextView tvBody;

    private FirebaseUser user;
private int totalMSG = 0;
    private DatabaseReference mDatabase;
    private DatabaseReference mMessageReference;
    private ValueEventListener mMessageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        btnSend = (Button) findViewById(R.id.btn_send);
        btnBack = (Button) findViewById(R.id.btn_back);
        edtSentText = (EditText) findViewById(R.id.edt_sent_text);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvBody = (TextView) findViewById(R.id.tv_body);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessageReference = FirebaseDatabase.getInstance().getReference("message");

        user = FirebaseAuth.getInstance().getCurrentUser();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitMessage();
                edtSentText.setText("");
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        ValueEventListener massageListner =  new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    for(DataSnapshot message: dataSnapshot.getChildren()){
                        Message c = message.getValue(Message.class);
                        Log.e(TAG, "onDataChange: Message data is updated: " + c.getAuthor() + ", " + c.getTime()+ ", " + c.getBody());
                        totalMSG++;
                        tvAuthor.setText(c.getAuthor());
                        tvTime.setText(c.getTime());
                        tvBody.setText(c.getBody());
                    }




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Failed to read value
                Log.e(TAG, "onCancelled: Failed to read message");

                tvAuthor.setText("");
                tvTime.setText("");
                tvBody.setText("onCancelled: Failed to read message!");

            }
        };
        mMessageReference.addValueEventListener(massageListner);

        // copy for removing at onStop()
        mMessageListener = massageListner;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mMessageListener!=null){
            mMessageReference.removeEventListener(mMessageListener);
        }
    }

    private  void submitMessage(){

        final String body = edtSentText.getText().toString();

        if (TextUtils.isEmpty(body)) {
            edtSentText.setError(REQUIRED);
            return;
        }


        // User data change listener
        mDatabase.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user == null) {
                    Log.e(TAG, "onDataChange: User data is null!");
                    Toast.makeText(MessageActivity.this, "onDataChange: User data is null!", Toast.LENGTH_SHORT).show();
                    return;
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "onCancelled: Failed to read user!");
            }
        });
        writeNewMessage(body);
    }

    private void writeNewMessage(String body) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        Message message = new Message(getUsernameFromEmail(user.getEmail()), body, time);

        mMessageReference.child(String.valueOf(totalMSG)).setValue(message);
    }

    private String getUsernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
    }
