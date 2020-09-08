package nf.co.ankushrodewad.technomateonlinestore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NotificationsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.notifications_toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.activity_name);
        toolbar_title.setText("Notifications");

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout b = (LinearLayout) inflater.inflate(R.layout.template_notification_item, null);
        TextView tv = (TextView) b.findViewById(R.id.tv_notification_text);
        tv.setText("notification text will appear here");

        LinearLayout lLayout = (LinearLayout) findViewById(R.id.notifications_layout);
        lLayout.addView(b);
    }

    public void toolbar_back_press(View view) {
        this.onBackPressed();
    }

    public void toolbar_home_press(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void toolbar_cart_press(View view) {
        Intent i = new Intent(this, CartActivity.class);
        startActivity(i);
    }

    public void toolbar_wish_list_press(View view) {
        Intent i = new Intent(this, WishListActivity.class);
        startActivity(i);
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("TAG", "signInAnonymously:FAILURE", exception);
                    }
                });
    }
}
