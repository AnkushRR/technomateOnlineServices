package nf.co.ankushrodewad.technomateonlinestore;

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

public class WishListActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    SharedPreferences WishListPref;
    LinearLayout lLayout;
    FirebaseFirestore db;
    int product_count = 0;
    LayoutInflater inflater;
    SharedPreferences CartPref, CartStatusVariablesPref, WishListVariablesPref, CountPref;
    StorageReference products_images_ref;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        db = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        products_images_ref = mStorageRef.child("products/");

        CartPref = getApplicationContext().getSharedPreferences("CartPreferences", MODE_PRIVATE);
        CartStatusVariablesPref = getApplicationContext().getSharedPreferences("CartStatusVariables", MODE_PRIVATE);
        WishListPref = getApplicationContext().getSharedPreferences("WishListPreferences", MODE_PRIVATE);
        WishListVariablesPref = getApplicationContext().getSharedPreferences("WishListStatusVariables", MODE_PRIVATE);
        CountPref = getApplicationContext().getSharedPreferences("CountPreferences",MODE_PRIVATE);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.wish_list_toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.activity_name);
        toolbar_title.setText("Wish List");

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lLayout = (LinearLayout) findViewById(R.id.wish_list_activity_layout);

    }

    public void toolbar_back_press(View view) {
        Intent i = new Intent(this, ProductsActivity.class);
        startActivity(i);
    }

    public void toolbar_home_press(View view) {
        view.setVisibility(View.GONE);
        Intent i = new Intent(this, MainActivity.class);
       // startActivity(i);
    }

    public void toolbar_cart_press(View view) {
        Intent i = new Intent(this, CartActivity.class);
        startActivity(i);
    }

    public void toolbar_wish_list_press(View view) {
        view.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ProductsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        product_count = CountPref.getInt("product_count",0);
        addAllViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lLayout.removeAllViews();
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


    public void addAllViews() {
        int x = WishListVariablesPref.getInt("total_qty", 0);
        if (x > 0) {
            TextView nothingToShow = (TextView) findViewById(R.id.wishList_nothing_to_show);
            nothingToShow.setVisibility(View.GONE);
        } else if (x == 0) {
            TextView nothingToShow = (TextView) findViewById(R.id.wishList_nothing_to_show);
            nothingToShow.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i <= product_count; i++) {
            int status;
            if (i > 9) {
                status = WishListPref.getInt("TE00" + i, 0);
            } else {
                status = WishListPref.getInt("TE000" + i, 0);
            }

            if (status == 1) {
                final LinearLayout[] c = new LinearLayout[1];
                final int finalI = i;
                db.collection("products").document("" + i).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            final ProductObj p = documentSnapshot.toObject(ProductObj.class);
                            c[0] = (LinearLayout) inflater.inflate(R.layout.template_wish_list_item, null);
                            //add contents to the wish_list_item_template
                            TextView title = (TextView) c[0].findViewById(R.id.tp_wish_list_tv_product_name);
                            TextView price = (TextView) c[0].findViewById(R.id.tp_wishlist_tv_product_price);
                            final ImageView add_to_cart = (ImageView) c[0].findViewById(R.id.tp_wishlist_iv_add_to_cart_button);
                            ImageView delete_from_wishlist = (ImageView) c[0].findViewById(R.id.tp_wishlist_iv_delete_button);

                            if (p == null)
                                return;
                            title.setText(p.getTitle());
                            price.setText("Rs." + p.getPrice());
//Add to cart
                            add_to_cart.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final LinearLayout toolset = (LinearLayout) c[0].findViewById(R.id.tp_wishlist_add_to_cart_toolset);
                                    toolset.setVisibility(View.VISIBLE);

                                    final SharedPreferences.Editor editor = CartPref.edit();
                                    final SharedPreferences.Editor cart_stat = CartStatusVariablesPref.edit();

                                    if (finalI > 9) {
                                        if(CartPref.getInt("TE00"+finalI,-1)<=0)
                                            editor.putInt("TE00" + finalI, 1);
                                        else
                                            editor.putInt("TE00" + finalI, CartPref.getInt("TE00"+finalI,-1)+1);
                                    }
                                    else {
                                        if(CartPref.getInt("TE000"+finalI,-1)<=0)
                                            editor.putInt("TE000" + finalI, 1);
                                        else
                                            editor.putInt("TE000" + finalI, CartPref.getInt("TE000"+finalI,-1)+1);
                                    }
                                    editor.apply();

                                    int cart_price = CartStatusVariablesPref.getInt("total_price", 0);
                                    int cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
                                    cart_stat.putInt("total_qty", cart_qty + 1);
                                    cart_stat.apply();
                                    cart_stat.putInt("total_price", cart_price + ((int) p.getPrice()));
                                    cart_stat.apply();
                                    SharedPreferences.Editor wishListEditor = WishListPref.edit();
                                    if (finalI > 9)
                                        wishListEditor.putInt("TE00" + finalI, 0);
                                    else
                                        wishListEditor.putInt("TE000" + finalI, 0);
                                    wishListEditor.apply();
                                    Toast.makeText(getApplicationContext(), "Successfully added to the cart", Toast.LENGTH_SHORT).show();
                                    c[0].setVisibility(View.GONE);

                                    SharedPreferences.Editor wishListStatusEditor = WishListVariablesPref.edit();
                                    wishListStatusEditor.putInt("total_qty", WishListVariablesPref.getInt("total_qty", 0) - 1);
                                    wishListStatusEditor.apply();

                                    int x = WishListVariablesPref.getInt("total_qty", 0);
                                    if (x > 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.wishList_nothing_to_show);
                                        nothingToShow.setVisibility(View.GONE);
                                    } else if (x == 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.wishList_nothing_to_show);
                                        nothingToShow.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
//Delete from wishlist
                            delete_from_wishlist.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor wishListEditor = WishListPref.edit();
                                    if (finalI > 9)
                                        wishListEditor.putInt("TE00" + finalI, 0);
                                    else
                                        wishListEditor.putInt("TE000" + finalI, 0);
                                    wishListEditor.apply();
                                    Toast.makeText(getApplicationContext(), "Successfully removed from wishlist", Toast.LENGTH_SHORT).show();
                                    c[0].setVisibility(View.GONE);

                                    SharedPreferences.Editor wishListStatusEditor = WishListVariablesPref.edit();
                                    wishListStatusEditor.putInt("total_qty", WishListVariablesPref.getInt("total_qty", 0) - 1);
                                    wishListStatusEditor.apply();

                                    int x = WishListVariablesPref.getInt("total_qty", 0);
                                    if (x > 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.wishList_nothing_to_show);
                                        nothingToShow.setVisibility(View.GONE);
                                    } else if (x == 0) {
                                        TextView nothingToShow = (TextView) findViewById(R.id.wishList_nothing_to_show);
                                        nothingToShow.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                            String suffix = ".jpg";
                            if(finalI==1)
                                suffix = ".png";
                            else
                                suffix = ".jpg";
                            products_images_ref.child(p.getId() + suffix).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Got the download URL for 'products/****.png'
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("imgUrl", uri.toString());
                                    //map.put("id", "TE00"+finalI1);
                                    db.collection("products").document("" + finalI).update(map);
                                    ImageView imageView = (ImageView) c[0].findViewById(R.id.tp_wishlist_iv_product_image);
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


}
