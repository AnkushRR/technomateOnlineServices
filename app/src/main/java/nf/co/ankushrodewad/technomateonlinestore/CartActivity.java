package nf.co.ankushrodewad.technomateonlinestore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    SharedPreferences CartPref, CartStatusVariablesPref, CountPref;
    FirebaseFirestore db;
    int viewCount,product_count;
    boolean processable;
    StorageReference products_images_ref;
    int cart_qty, cart_price;
    LinearLayout lLayout;
    LayoutInflater inflater;
    ProgressDialog progressDialog;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        CartPref = getApplicationContext().getSharedPreferences("CartPreferences", MODE_PRIVATE);

        CartStatusVariablesPref = getApplicationContext().getSharedPreferences("CartStatusVariables", MODE_PRIVATE);
        CountPref = getApplicationContext().getSharedPreferences("CountPreferences",MODE_PRIVATE);
        cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
        cart_price = CartStatusVariablesPref.getInt("total_price", 0);

        //One time initialization
        /*SharedPreferences.Editor cart_stat = CartStatusVariablesPref.edit();
        cart_stat.putInt("total_qty",0);
        cart_stat.apply();
        cart_stat.putInt("total_price",0);
        cart_stat.apply();*/

        product_count = 0;

        //Initializing database
        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        products_images_ref = mStorageRef.child("products/");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.cart_toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.activity_name);
        toolbar_title.setText("Cart");

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lLayout = (LinearLayout) findViewById(R.id.cart_layout);


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        processable = false;
        product_count = CountPref.getInt("product_count",0);
        addAllItems();
        updateSummaryUI();

    }

    @Override
    protected void onPause() {
        super.onPause();
        lLayout.removeAllViews();
    }

    public void toolbar_back_press(View view) {
        Intent i = new Intent(this, ProductsActivity.class);
        startActivity(i);
    }

    public void toolbar_home_press(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void toolbar_cart_press(View view) {
        view.setVisibility(View.GONE);
    }

    public void toolbar_wish_list_press(View view) {
        Intent i = new Intent(this, WishListActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ProductsActivity.class);
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


    private void addAllItems() {
        int x = CartStatusVariablesPref.getInt("total_qty", 0);
        if (x > 0) {
            TextView nothingToShow = (TextView) findViewById(R.id.cart_nothing_to_show);
            nothingToShow.setVisibility(View.GONE);
            processable = true;
        } else if (x == 0) {
            TextView nothingToShow = (TextView) findViewById(R.id.cart_nothing_to_show);
            nothingToShow.setVisibility(View.VISIBLE);
            processable = false;
        }


        for (int i = 1; i <= product_count; i++) {
            final int[] qty = new int[2];
            if (i > 9)
                qty[0] = CartPref.getInt("TE00" + i, 0);
            else
                qty[0] = CartPref.getInt("TE000" + i, 0);
            final LinearLayout[] c = new LinearLayout[1];
            if (qty[0] > 0) {
                final int finalI = i;
                db.collection("products").document("" + i).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            final ProductObj p = documentSnapshot.toObject(ProductObj.class);
                            c[0] = (LinearLayout) inflater.inflate(R.layout.template_cart_item, null);
                            TextView title_tv = (TextView) c[0].findViewById(R.id.tp_cart_item_tv_title);
                            if (p == null)
                                return;
                            title_tv.setText(p.getTitle());
//remove from cart
                            ImageView remove_from_cart = (ImageView) c[0].findViewById(R.id.tp_cart_item_remove_from_cart);
                            remove_from_cart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor editor = CartPref.edit();
                                    SharedPreferences.Editor cart_stat = CartStatusVariablesPref.edit();
                                    if (finalI > 9)
                                        editor.putInt("TE00" + finalI, 0);
                                    else
                                        editor.putInt("TE000" + finalI, 0);
                                    editor.apply();
                                    c[0].setVisibility(View.GONE);
                                    int current_qty = Integer.parseInt(((TextView) c[0].findViewById(R.id.tp_cart_item_tv_product_quantity)).getText().toString());
                                    cart_price = CartStatusVariablesPref.getInt("total_price", 0);
                                    cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
                                    cart_qty -= current_qty;
                                    cart_price -= (current_qty * p.getPrice());
                                    cart_stat.putInt("total_qty", cart_qty);
                                    cart_stat.apply();
                                    cart_stat.putInt("total_price", cart_price);
                                    cart_stat.apply();

                                    int x = CartStatusVariablesPref.getInt("total_qty", 0);
                                    if (x > 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.cart_nothing_to_show);
                                        nothingToShow.setVisibility(View.GONE);
                                        processable = true;
                                    } else if (x == 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.cart_nothing_to_show);
                                        nothingToShow.setVisibility(View.VISIBLE);
                                        processable = false;
                                    }

                                    updateSummaryUI();
                                }
                            });

                            final TextView qty_tv = (TextView) c[0].findViewById(R.id.tp_cart_item_tv_product_quantity);
                            qty_tv.setText("" + qty[0]);

                            TextView price = (TextView) c[0].findViewById(R.id.tp_cart_item_tv_product_price);
                            price.setText("" + p.getPrice());

                            final Button red_qty = (Button) c[0].findViewById(R.id.tp_cart_item_iv_decrement_quantity);
                            red_qty.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor editor = CartPref.edit();
                                    SharedPreferences.Editor cart_stat = CartStatusVariablesPref.edit();
                                    if (finalI > 9)
                                        qty[0] = CartPref.getInt("TE00" + finalI, 0);
                                    else
                                        qty[0] = CartPref.getInt("TE000" + finalI, 0);
                                    if (qty[0] > 1) {
                                        if (finalI > 9)
                                            editor.putInt("TE00" + finalI, qty[0] - 1);
                                        else
                                            editor.putInt("TE000" + finalI, qty[0] - 1);
                                        editor.apply();
                                        qty_tv.setText("" + (qty[0] - 1));
                                    }
                                    if (qty[0] <= 1) {
                                        if (finalI > 9)
                                            editor.putInt("TE00" + finalI, 0);
                                        else
                                            editor.putInt("TE000" + finalI, 0);
                                        editor.apply();
                                        c[0].setVisibility(View.GONE);
                                    }
                                    cart_price = CartStatusVariablesPref.getInt("total_price", 0);
                                    cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
                                    cart_stat.putInt("total_qty", cart_qty - 1);
                                    cart_stat.apply();
                                    cart_stat.putInt("total_price", cart_price - ((int) p.getPrice()));
                                    cart_stat.apply();
                                    cart_qty = cart_qty - 1;
                                    cart_price = cart_price - ((int) p.getPrice());

                                    int x = CartStatusVariablesPref.getInt("total_qty", 0);
                                    if (x > 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.cart_nothing_to_show);
                                        nothingToShow.setVisibility(View.GONE);
                                        processable = true;
                                    } else if (x == 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.cart_nothing_to_show);
                                        nothingToShow.setVisibility(View.VISIBLE);
                                        processable = false;
                                    }

                                    updateSummaryUI();
                                }
                            });

                            final Button inc_qty = (Button) c[0].findViewById(R.id.tp_cart_item_iv_increment_quantity);
                            inc_qty.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor editor = CartPref.edit();
                                    SharedPreferences.Editor cart_stat = CartStatusVariablesPref.edit();
                                    if (finalI > 9) {
                                        qty[0] = CartPref.getInt("TE00" + finalI, 0);
                                        editor.putInt("TE00" + finalI, qty[0] + 1);
                                        editor.apply();
                                        qty_tv.setText("" + (qty[0] + 1));
                                    } else {
                                        qty[0] = CartPref.getInt("TE000" + finalI, 0);
                                        editor.putInt("TE000" + finalI, qty[0] + 1);
                                        editor.apply();
                                        qty_tv.setText("" + (qty[0] + 1));
                                    }

                                    cart_price = CartStatusVariablesPref.getInt("total_price", 0);
                                    cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
                                    cart_stat.putInt("total_qty", cart_qty + 1);
                                    cart_stat.apply();
                                    cart_stat.putInt("total_price", cart_price + ((int) p.getPrice()));
                                    cart_stat.apply();
                                    cart_qty = cart_qty + 1;
                                    cart_price = cart_price + ((int) p.getPrice());

                                    int x = CartStatusVariablesPref.getInt("total_qty", 0);
                                    if (x > 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.cart_nothing_to_show);
                                        nothingToShow.setVisibility(View.GONE);
                                        processable = true;
                                    } else if (x == 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.cart_nothing_to_show);
                                        nothingToShow.setVisibility(View.VISIBLE);
                                        processable = false;
                                    }

                                    updateSummaryUI();
                                }
                            });

                            products_images_ref.child(p.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Got the download URL for 'products/****.png'
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("imgUrl", uri.toString());
                                    //map.put("id", "TE00"+finalI1);
                                    db.collection("products").document("" + finalI).update(map);
                                    ImageView imageView = (ImageView) c[0].findViewById(R.id.tp_cart_item_iv_product_image);
                                    Picasso
                                            .get()
                                            .load(uri.toString())
                                            .into(imageView);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                            lLayout.addView(c[0]);
                        }
                    }
                });
            }
        }
    }

    public void updateSummaryUI() {
        TextView no_of_items, total_price, grand_total, discount, delivery_charge;
        no_of_items = (TextView) findViewById(R.id.tv_cart_total_qty);
        total_price = (TextView) findViewById(R.id.tv_cart_total_price);
        grand_total = (TextView) findViewById(R.id.tv_cart_grand_total);
        discount = (TextView) findViewById(R.id.tv_cart_discount);
        delivery_charge = (TextView) findViewById(R.id.tv_cart_delivery_charges);

        no_of_items.setText(cart_qty + " items in total");
        total_price.setText("Rs." + cart_price);
        grand_total.setText("Rs." + cart_price);
        discount.setText("Rs." + 0);
        delivery_charge.setText("Rs.0");
    }

    public void goToUserForm(View view) {
        Intent j = new Intent(this, UserFormActivity.class);
        if (processable) {
            startActivity(j);
        } else {
            Toast.makeText(this, "Can not proceed with an empty cart :)", Toast.LENGTH_LONG).show();
        }
    }
}
