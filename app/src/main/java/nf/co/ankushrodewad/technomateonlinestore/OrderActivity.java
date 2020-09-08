package nf.co.ankushrodewad.technomateonlinestore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.MeasureFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class OrderActivity extends AppCompatActivity {

    Intent i;
    FirebaseAuth mAuth;
    int otp, orderId;
    int product_count;
    FirebaseFirestore db;
    LayoutInflater inflater;
    SharedPreferences orderStatus, CartStatusVariablesPref, CartPref, CountPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        CartStatusVariablesPref = getApplicationContext().getSharedPreferences("CartStatusVariables", MODE_PRIVATE);
        CartPref = getApplicationContext().getSharedPreferences("CartPreferences", MODE_PRIVATE);
        orderStatus = getApplicationContext().getSharedPreferences("orderStatus", MODE_PRIVATE);
        CountPref = getApplicationContext().getSharedPreferences("CountPreferences",MODE_PRIVATE);
        product_count = CountPref.getInt("product_count",0);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.order_toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.activity_name);
        toolbar_title.setText("Order Receipt");
        ImageView imageView = (ImageView) toolbar.findViewById(R.id.action_home);
        imageView.setVisibility(View.GONE);
        imageView = (ImageView) toolbar.findViewById(R.id.action_wish_list);
        imageView.setVisibility(View.GONE);
        imageView = (ImageView) toolbar.findViewById(R.id.action_cart);
        imageView.setVisibility(View.GONE);

        db  = FirebaseFirestore.getInstance();

        final Button bt = (Button) findViewById(R.id.bt_continue_shopping);

        i = new Intent(this, MainActivity.class);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = CartPref.edit();
                SharedPreferences.Editor editor2 = CartStatusVariablesPref.edit();
                for (int i = 0; i < product_count; i++) {
                    if (i < 10) {
                        editor.putInt("TE000" + i, 0);
                        editor.apply();
                        editor2.putInt("total_price", 0);
                        editor2.apply();
                        editor2.putInt("total_qty", 0);
                        editor2.apply();
                    } else {
                        editor.putInt("TE00" + i, 0);
                        editor.apply();
                        editor2.putInt("total_price", 0);
                        editor2.apply();
                        editor2.putInt("total_qty", 0);
                        editor2.apply();
                    }
                }
                startActivity(i);
            }
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        orderId = orderStatus.getInt("orderId", 0);
        otp = orderStatus.getInt("otp", 0);
        TextView tv_order_id = (TextView) findViewById(R.id.tv_order_id);
        tv_order_id.setText("order id: " + orderId);
        TextView tv_order_status = (TextView) findViewById(R.id.tv_order_reciept);
        tv_order_status.setText("Your order is placed with order_id " + orderId + " and will be delivered ASAP. Kindly share this OTP " + otp + " with the delivery person\n Take a screenshot of this page as your order receipt");
        product_count = CountPref.getInt("product_count",0);
        final String[] result = {""};
        TextView textView = (TextView) findViewById(R.id.textView);
        final GridLayout reciept = (GridLayout) findViewById(R.id.reciept_layout);
        final TextView[] x = {new TextView(getApplicationContext())};
        reciept.setUseDefaultMargins(true);

        x[0].setLayoutParams(new GridView.LayoutParams(50,70));
        x[0].setTextColor(getResources().getColor(R.color.black));
        reciept.addView(x[0]);
        x[0] = new TextView(getApplicationContext());
        x[0].setLayoutParams(new GridView.LayoutParams(600,70));
        x[0].setTextColor(getResources().getColor(R.color.black));
        x[0].setText("Item");
        x[0].setPadding(8,0,0,0);
       // x.setGravity(Gravity.FILL_HORIZONTAL);
        reciept.addView(x[0]);
        x[0] = new TextView(getApplicationContext());
        x[0].setLayoutParams(new GridView.LayoutParams(100,70));
        x[0].setTextColor(getResources().getColor(R.color.black));
        x[0].setText("Qty");
        reciept.addView(x[0]);
        x[0] = new TextView(getApplicationContext());
        x[0].setLayoutParams(new GridView.LayoutParams(200,70));
        x[0].setTextColor(getResources().getColor(R.color.black));
        x[0].setText("Price");
        reciept.addView(x[0]);
        final int[] serial = {0};
        int i=1;
        for(; i<product_count; i++){
            if(i>9) {
                if(CartPref.getInt("TE00"+i,-1)>0){
                    final int finalI = i;
                    db.collection("products").document(""+i).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                ProductObj productObj = documentSnapshot.toObject(ProductObj.class);
                                x[0] = new TextView(getApplicationContext());
                                x[0].setLayoutParams(new GridView.LayoutParams(50, GridLayout.LayoutParams.WRAP_CONTENT));
                         //       x[0].setTextColor(getResources().getColor(R.color.black));
                                serial[0]++;
                                x[0].setPadding(8,0,0,0);
                                x[0].setText(""+ serial[0] +".");
                                reciept.addView(x[0]);

                                  x[0] = new TextView(getApplicationContext());
                                  x[0].setPadding(8,8,0,0);
                                  x[0].setLayoutParams(new GridView.LayoutParams(600,GridLayout.LayoutParams.WRAP_CONTENT));
                            //    x[0].setTextColor(getResources().getColor(R.color.black));
                                  x[0].setText(productObj.getTitle()+" ");
                                  reciept.addView(x[0]);

                                x[0] = new TextView(getApplicationContext());
                                x[0].setPadding(8,0,0,0);
                                x[0].setLayoutParams(new GridView.LayoutParams(100,GridLayout.LayoutParams.WRAP_CONTENT));
                             //   x[0].setTextColor(getResources().getColor(R.color.black));
                                x[0].setText(""+CartPref.getInt("TE00" + finalI, 0));
                                reciept.addView(x[0]);

                                x[0] = new TextView(getApplicationContext());
                                x[0].setPadding(8,0,0,0);
                                x[0].setLayoutParams(new GridView.LayoutParams(200,GridLayout.LayoutParams.WRAP_CONTENT));
                           //     x[0].setTextColor(getResources().getColor(R.color.black));
                                int price= CartPref.getInt("TE00" + finalI, 0);
                                price*=productObj.getPrice();
                                x[0].setText(""+price);
                                reciept.addView(x[0]);
                            }
                        }
                    });
                }
            }else{
                if(CartPref.getInt("TE000"+i,-1)>0){
                    final int finalI = i;
                    db.collection("products").document(""+i).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                ProductObj productObj = documentSnapshot.toObject(ProductObj.class);
                                x[0] = new TextView(getApplicationContext());
                                x[0].setPadding(8,0,0,0);
                                x[0].setLayoutParams(new GridView.LayoutParams(50,GridLayout.LayoutParams.WRAP_CONTENT));
                           //     x[0].setTextColor(getResources().getColor(R.color.black));
                                serial[0]++;
                                x[0].setText(""+ serial[0] +".");
                                reciept.addView(x[0]);

                                x[0] = new TextView(getApplicationContext());
                                x[0].setPadding(8,8,0,0);
                                x[0].setLayoutParams(new GridView.LayoutParams(600,GridLayout.LayoutParams.WRAP_CONTENT));
                         //       x[0].setTextColor(getResources().getColor(R.color.black));
                                x[0].setText(productObj.getTitle()+" ");
                                reciept.addView(x[0]);

                                x[0] = new TextView(getApplicationContext());
                                x[0].setPadding(8,0,0,0);
                                x[0].setLayoutParams(new GridView.LayoutParams(100,GridLayout.LayoutParams.WRAP_CONTENT));
                        //        x[0].setTextColor(getResources().getColor(R.color.black));
                                x[0].setText(""+CartPref.getInt("TE000" + finalI, 0));
                                reciept.addView(x[0]);

                                x[0] = new TextView(getApplicationContext());
                                x[0].setPadding(8,0,0,0);
                                x[0].setLayoutParams(new GridView.LayoutParams(200,GridLayout.LayoutParams.WRAP_CONTENT));
                      //          x[0].setTextColor(getResources().getColor(R.color.black));
                                int price= CartPref.getInt("TE000" + finalI, 0);
                                price*=productObj.getPrice();
                                x[0].setText(""+price);
                                reciept.addView(x[0]);
                            }
                        }
                    });
                }
            }

        }textView.setText(result[0]);


        GridLayout receipt_summary = (GridLayout) findViewById(R.id.reciept_summary_entry);
        x[0] = new TextView(getApplicationContext());
        x[0].setLayoutParams(new GridView.LayoutParams(50, 5));
        x[0].setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        receipt_summary.addView(x[0]);
        x[0] = new TextView(getApplicationContext());
        x[0].setLayoutParams(new GridView.LayoutParams(600, 5));
        x[0].setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        x[0].setPadding(8, 0, 0, 0);
        // x.setGravity(Gravity.FILL_HORIZONTAL);
        receipt_summary.addView(x[0]);
        x[0] = new TextView(getApplicationContext());
        x[0].setLayoutParams(new GridView.LayoutParams(100, 5));
        x[0].setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        receipt_summary.addView(x[0]);
        x[0] = new TextView(getApplicationContext());
        x[0].setLayoutParams(new GridView.LayoutParams(200, 5));
        x[0].setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        receipt_summary.addView(x[0]);

                x[0] = new TextView(getApplicationContext());
                x[0].setLayoutParams(new GridView.LayoutParams(50, GridLayout.LayoutParams.WRAP_CONTENT));
                receipt_summary.addView(x[0]);
                x[0] = new TextView(getApplicationContext());
                x[0].setLayoutParams(new GridView.LayoutParams(600, GridLayout.LayoutParams.WRAP_CONTENT));
                x[0].setText("Total");
                x[0].setTextColor(getResources().getColor(R.color.black));
                x[0].setPadding(8, 0, 0, 0);
                // x.setGravity(Gravity.FILL_HORIZONTAL);
                receipt_summary.addView(x[0]);
                x[0] = new TextView(getApplicationContext());
                x[0].setLayoutParams(new GridView.LayoutParams(100, GridLayout.LayoutParams.WRAP_CONTENT));
                x[0].setText("" + CartStatusVariablesPref.getInt("total_qty", 0));
                 x[0].setTextColor(getResources().getColor(R.color.black));
                receipt_summary.addView(x[0]);
                x[0] = new TextView(getApplicationContext());
                x[0].setLayoutParams(new GridView.LayoutParams(200, GridLayout.LayoutParams.WRAP_CONTENT));
                x[0].setText("" + CartStatusVariablesPref.getInt("total_price", 0));
                x[0].setTextColor(getResources().getColor(R.color.black));
                receipt_summary.addView(x[0]);

    }

    public void toolbar_back_press(View view) {
        this.onBackPressed();
    }

    public void toolbar_home_press(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void toolbar_cart_press(View view) {
        view.setVisibility(View.GONE);
        Intent i = new Intent(this, CartActivity.class);
      //  startActivity(i);
    }

    public void toolbar_wish_list_press(View view) {
        view.setVisibility(View.GONE);
        Intent i = new Intent(this, WishListActivity.class);
      //  startActivity(i);
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

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = CartPref.edit();
        for (int i = 0; i < product_count; i++) {
            SharedPreferences.Editor editor2 = CartStatusVariablesPref.edit();
            if (i < 10) {
                editor.putInt("TE000" + i, 0);
                editor.apply();
                editor2.putInt("total_price", 0);
                editor2.apply();
                editor2.putInt("total_qty", 0);
                editor2.apply();
            } else {
                editor.putInt("TE00" + i, 0);
                editor.apply();
                editor2.putInt("total_price", 0);
                editor2.apply();
                editor2.putInt("total_qty", 0);
                editor2.apply();
            }
        }
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
