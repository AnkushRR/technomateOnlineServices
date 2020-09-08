package nf.co.ankushrodewad.technomateonlinestore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProductActivity extends AppCompatActivity {

    Intent i;
    FirebaseAuth mAuth;
    String title;
    String id;
    double price;
    String imgUrl;
    SharedPreferences CartStatusVariablesPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

//initialize with dummy values
        title = "null";
        id = "null";
        price = 0;
        imgUrl = null;


//Authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }
//Add toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.product_toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.activity_name);
        toolbar_title.setText("ProductObj");

        i = new Intent(this, CartActivity.class);

        TextView textView = (TextView) findViewById(R.id.tv_buy_button);

//Add listener to buy button and forward the data
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("title", title);
                i.putExtra("id", id);
                i.putExtra("price", price);
                i.putExtra("imgUrl", imgUrl);
                startActivity(i);
            }
        });

        //Adding the item
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// get intent data via the key
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        title = (String) extras.get("title");
        id = (String) extras.get("id");
        String sprice = (String) extras.get("price");
        price = Double.parseDouble(sprice);
        imgUrl = (String) extras.get("imgUrl");


//Set the textViews  with data
        TextView tv_title = (TextView) findViewById(R.id.tv_product_name);
        tv_title.setText(title);
        TextView tv_price = (TextView) findViewById(R.id.tv_product_price);
        tv_price.setText("Rs. " + price);
        TextView tv_id = (TextView) findViewById(R.id.tv_product_id);
        tv_id.setText("SKU: " + id);
        ImageView imageView = (ImageView) findViewById(R.id.iv_product);
        Picasso
                .get()
                .load(imgUrl)
                .into(imageView);

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
