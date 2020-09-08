package nf.co.ankushrodewad.technomateonlinestore;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
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

public class ProductsActivity extends AppCompatActivity {
// Add a bottom bar for cart

    Intent i;
    LinearLayout lLayout;
    LayoutInflater inflater;
    FirebaseFirestore db;
    TextView cart_status_qty, cart_status_price;
    int cart_qty, cart_price,product_count;
    TextView title;
    TextView price;
    TextView id;
    int viewCount;
    SearchView searchView;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;
    ProgressDialog progressDialog;
    ImageView imageView, add_to_wishList_Button;
    LinearLayout[] layouts;
    StorageReference products_images_ref;
    FirebaseAuth mAuth;
    SharedPreferences CartPref, WishListPref, CartStatusVariablesPref, WishListVariablesPref, CountPref;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        CartPref = getApplicationContext().getSharedPreferences("CartPreferences", MODE_PRIVATE);
        WishListPref = getApplicationContext().getSharedPreferences("WishListPreferences", MODE_PRIVATE);
        CartStatusVariablesPref = getApplicationContext().getSharedPreferences("CartStatusVariables", MODE_PRIVATE);
        WishListVariablesPref = getApplicationContext().getSharedPreferences("WishListStatusVariables", MODE_PRIVATE);
        CountPref = getApplicationContext().getSharedPreferences("CountPreferences",MODE_PRIVATE);

        final String DATABASE_NAME = "products.db";
        ProductDatabase productDatabase;
        productDatabase = Room.databaseBuilder(getApplicationContext(),
                ProductDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();

        product_count = 0;


        //Adding custom toolbar
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.products_toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.activity_name);
        toolbar_title.setText("All products");

        //card status textviews
        cart_status_price = (TextView) findViewById(R.id.cart_status_price);
        cart_status_qty = (TextView) findViewById(R.id.cart_status_qty);
        cart_price = CartStatusVariablesPref.getInt("total_price", 0);
        cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
        cart_status_price.setText("Rs." + cart_price);
        cart_status_qty.setText(cart_qty + " items");


        //inflating current layout to add templates in it
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lLayout = (LinearLayout) findViewById(R.id.products_layout);



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

        viewCount = 0;

      //  recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //  addAllViews();

    }

    public void toolbar_back_press(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        db.collection("products").document("count").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    OrderCountObj count = documentSnapshot.toObject(OrderCountObj.class);
                    product_count  = count.getCount();
                    //Initializing array to store all products
                    layouts = new LinearLayout[product_count+5];
                    CountPref.edit().putInt("product_count",product_count).apply();
                    addAllViews();
                }
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Loading products..");
        progressDialog.show();
        cart_price = CartStatusVariablesPref.getInt("total_price", 0);
        cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
        cart_status_price.setText("Rs." + cart_price);
        cart_status_qty.setText(cart_qty + " items");
    }

   /* @Override
    protected void onStart() {
        super.onStart();
        lLayout.removeAllViews();
        addAllViews();
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        lLayout.removeAllViews();
    }

    public void viewAdded() {
        viewCount++;
        if (viewCount == product_count) {
            progressDialog.dismiss();
            //     Toast.makeText(this, "All views added",Toast.LENGTH_SHORT).show();
           // recyclerView.setLayoutManager(new LinearLayoutManager(this));
           // recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),layouts);
           // recyclerView.setAdapter(recyclerViewAdapter);

            for(int i=1; i<=product_count; i++){
                lLayout.addView(layouts[i]);
            }
            //Searchview initialization
            searchView = (SearchView) findViewById(R.id.search_view);
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.setIconified(false);
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                          //  Toast.makeText(getApplicationContext(),"The search query is: "+query,Toast.LENGTH_SHORT).show();
                            lLayout.removeAllViews();
                            for(int i=1; i<product_count; i++){
                                TextView t =(TextView) layouts[i].findViewById(R.id.tp_product_tv_title);
                                if(t.getText().toString().toLowerCase().contains(query.toLowerCase())){
                                    lLayout.addView(layouts[i]);
                                }
                            }
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            return false;
                        }
                    });
                    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                        @Override
                        public boolean onClose() {
                            lLayout.removeAllViews();
                            for(int i=1; i<=product_count; i++){
                                lLayout.addView(layouts[i]);
                            }
                            return false;
                        }
                    });
                }
            });

        }
    }

    public void addAllViews() {

        final Count[] count = {new Count(2)};

        for (int i = 1; i <= product_count; i++) {
            final LinearLayout[] b = new LinearLayout[1];
            final String[] ids = new String[1];
            final int finalI = i;
            final int finalI1 = i;

            final int finalI2 = i;
            db.collection("products").document("" + i).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        final ProductObj p = documentSnapshot.toObject(ProductObj.class);
                        b[0] = (LinearLayout) inflater.inflate(R.layout.template_product, null);
                        title = (TextView) b[0].findViewById(R.id.tp_product_tv_title);
                        price = (TextView) b[0].findViewById(R.id.tp_product_tv_price);
                        id = (TextView) b[0].findViewById(R.id.tp_product_tv_id);
                        add_to_wishList_Button = (ImageView) b[0].findViewById(R.id.tp_product_iv_add_to_wish_list_button);
                        int stat;
                        if (finalI > 9)
                            stat = WishListPref.getInt("TE00" + finalI, 0);
                        else
                            stat = WishListPref.getInt("TE000" + finalI, 0);
                        if (stat == 0) {
                            add_to_wishList_Button.setBackgroundResource(R.drawable.ic_favorite_border_red_24dp);
                        } else if (stat == 1) {
                            add_to_wishList_Button.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                        }


                        if (p == null)
                            return;
                        title.setText(p.getTitle());
                        price.setText("Rs. " + p.getPrice());
                        id.setText("SKU: " + p.getId());
                        ids[0] = p.getId();
                        b[0].setTag(p);
                        final Button add_to_cart = (Button) b[0].findViewById(R.id.tp_product_bt_add_to_cart);
                        int x;
                        if(finalI>9)
                        x= CartPref.getInt("TE00" + finalI, 0);
                        else
                            x=CartPref.getInt("TE000" + finalI, 0);

//Add the product to the cart
                        if(x>0){
                            add_to_cart.setText("Added");
                        }else
                        add_to_cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final SharedPreferences.Editor editor = CartPref.edit();
                                final SharedPreferences.Editor cart_stat = CartStatusVariablesPref.edit();
                               /* if (finalI > 9)
                                    editor.putInt("TE00" + finalI, CartPref.getInt("TE00" + finalI, 0) + 1);
                                else
                                    editor.putInt("TE000" + finalI, CartPref.getInt("TE000" + finalI, 0) + 1);
                                editor.apply();*/

                                final TextView qty_t = (TextView) b[0].findViewById(R.id.tp_product_quantity);
                                qty_t.setVisibility(View.VISIBLE);
                                if (finalI > 9)
                                    qty_t.setText("" + CartPref.getInt("TE00" + finalI, 0));
                                else
                                    qty_t.setText("" + CartPref.getInt("TE000" + finalI, 0));

                                LinearLayout qty_layout = (LinearLayout) b[0].findViewById(R.id.tp_product_qty_layout);
                                qty_layout.setVisibility(View.VISIBLE);

                                final Button inc_qty = (Button) b[0].findViewById(R.id.tp_product_increase_quantity);
                                inc_qty.setVisibility(View.VISIBLE);
                                inc_qty.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int qty;
                                        if (finalI > 9) {
                                            qty = CartPref.getInt("TE00" + finalI, 0);
                                            editor.putInt("TE00" + finalI, qty + 1);
                                        } else {
                                            qty = CartPref.getInt("TE000" + finalI, 0);
                                            editor.putInt("TE000" + finalI, qty + 1);
                                        }
                                        editor.apply();
                                        qty_t.setText("" + (qty + 1));
                                        cart_price = CartStatusVariablesPref.getInt("total_price", 0);
                                        cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
                                        cart_stat.putInt("total_qty", cart_qty + 1);
                                        cart_stat.apply();
                                        cart_stat.putInt("total_price", cart_price + ((int) p.getPrice()));
                                        cart_stat.apply();
                                        cart_price = CartStatusVariablesPref.getInt("total_price", 0);
                                        cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
                                        cart_status_price.setText("Rs. " + cart_price);
                                        cart_status_qty.setText(cart_qty + " items");
                                    }
                                });
                                add_to_cart.setVisibility(View.GONE);

                                final Button red_qty = (Button) b[0].findViewById(R.id.tp_product_reduce_quantity);
                                red_qty.setVisibility(View.VISIBLE);
                                red_qty.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        int qty;
                                        if (finalI > 9)
                                            qty = CartPref.getInt("TE00" + finalI, 0);
                                        else
                                            qty = CartPref.getInt("TE000" + finalI, 0);
                                        if (qty > 0) {
                                            if (finalI > 9)
                                                editor.putInt("TE00" + finalI, qty - 1);
                                            else
                                                editor.putInt("TE000" + finalI, qty - 1);
                                            editor.apply();
                                            qty_t.setText("" + (qty - 1));
                                            cart_price = CartStatusVariablesPref.getInt("total_price", 0);
                                            cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
                                            cart_stat.putInt("total_qty", cart_qty - 1);
                                            cart_stat.apply();
                                            cart_stat.putInt("total_price", cart_price - ((int) p.getPrice()));
                                            cart_stat.apply();
                                            cart_price = CartStatusVariablesPref.getInt("total_price", 0);
                                            cart_qty = CartStatusVariablesPref.getInt("total_qty", 0);
                                            cart_status_price.setText("Rs. " + cart_price);
                                            cart_status_qty.setText(cart_qty + " items");
                                        }
                                        if (qty == 0) {
                                            if (finalI > 9)
                                                editor.putInt("TE00" + finalI, 0);
                                            else
                                                editor.putInt("TE000" + finalI, 0);
                                            editor.apply();
                                            qty_t.setVisibility(View.GONE);
                                            inc_qty.setVisibility(View.GONE);
                                            red_qty.setVisibility(View.GONE);
                                            add_to_cart.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                        });


//add to wishList button
                        add_to_wishList_Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SharedPreferences.Editor wishListEditor = WishListPref.edit();
                                int status;
                                if (finalI > 9)
                                    status = WishListPref.getInt("TE00" + finalI, 0);
                                else
                                    status = WishListPref.getInt("TE000" + finalI, 0);
                                if (status == 0) {
                                    v.setBackgroundResource(R.drawable.ic_favorite_red_24dp);
                                    wishListEditor.apply();

                                    SharedPreferences.Editor wishListStatusEditor = WishListVariablesPref.edit();
                                    wishListStatusEditor.putInt("total_qty", WishListVariablesPref.getInt("total_qty", 0) + 1);
                                    wishListStatusEditor.apply();

                                    if (finalI > 9) {
                                        wishListEditor.putInt("TE00" + finalI, 1);
                                    } else {
                                        wishListEditor.putInt("TE000" + finalI, 1);
                                    }
                                    wishListEditor.apply();
                                } else if (status == 1) {
                                    v.setBackgroundResource(R.drawable.ic_favorite_border_red_24dp);
                                    wishListEditor.apply();

                                    SharedPreferences.Editor wishListStatusEditor = WishListVariablesPref.edit();
                                    wishListStatusEditor.putInt("total_qty", WishListVariablesPref.getInt("total_qty", 0) - 1);
                                    wishListStatusEditor.apply();

                                    if (finalI > 9)
                                        wishListEditor.putInt("TE00" + finalI, 0);
                                    else
                                        wishListEditor.putInt("TE000" + finalI, 0);
                                    wishListEditor.apply();
                                }
                            }
                        });


                        String suffix = ".jpg";
                        if(finalI==1)
                            suffix = ".png";
                        else
                            suffix = ".jpg";

                        //Retrieve and add image to the product
                        products_images_ref.child(p.getId() + suffix).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'products/****.png'
                                Map<String, Object> map = new HashMap<>();
                                map.put("imgUrl", uri.toString());
                                //map.put("id", "TE00"+finalI1);
                                db.collection("products").document("" + finalI1).update(map);
                                imageView = (ImageView) b[0].findViewById(R.id.tp_product_iv_img);
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

                        b[0].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                goToProduct(v);
                            }
                        });
                      //  if (b[0] != null)
                      //      lLayout.addView(b[0]);

                        layouts[finalI] = b[0];

                        viewAdded();
                    } else {
                        return;
                    }
                }
            });
        }
    }

    public void goToProduct(View view) {

        ProductObj p = new ProductObj();
        p = (ProductObj) view.getTag();

        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(this);
        ImageDialog.setTitle(p.getTitle());
        ImageView showImage = new ImageView(this);
        Picasso
                .get()
                .load(p.getImgUrl())
                .into(showImage);
        ImageDialog.setView(showImage);

        ImageDialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });
        ImageDialog.show();

        Intent j = new Intent(this, ZoomActivity.class);
        // p = new ProductObj(String.valueOf(title.getText()), Double.parseDouble(String.valueOf(price.getText())), String.valueOf(id.getText()), "", "");
        j.putExtra("title", p.getTitle());
        j.putExtra("id", p.getId());
        j.putExtra("price", String.valueOf(p.getPrice()));
        j.putExtra("imgUrl", p.getImgUrl());

        Toast.makeText(this, "" + p.getTitle(), Toast.LENGTH_SHORT).show();
        //   startActivity(j);

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

    public void goToCart(View view) {
        Intent j = new Intent(this, CartActivity.class);
        startActivity(j);
    }


}



