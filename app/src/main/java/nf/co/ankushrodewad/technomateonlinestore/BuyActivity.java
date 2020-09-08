package nf.co.ankushrodewad.technomateonlinestore;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.location.LocationManager.GPS_PROVIDER;

public class BuyActivity extends AppCompatActivity {

    Intent i;
    FirebaseAuth mAuth;
    FusedLocationProviderClient mFusedLocationClient;
    LocationManager locationManager;
    Geocoder geocoder;
    Location mlocation;
    LocationListener locationListener;
    SharedPreferences userForm, CartStatusVariablesPref, CartPref, orderStatus,CountPref;
    Location userAddress;
    FirebaseFirestore db;
    int product_count;
    private boolean eligible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        eligible = false;

        userForm = getApplicationContext().getSharedPreferences("userForm", MODE_PRIVATE);
        CartStatusVariablesPref = getApplicationContext().getSharedPreferences("CartStatusVariables", MODE_PRIVATE);
        CartPref = getApplicationContext().getSharedPreferences("CartPreferences", MODE_PRIVATE);
        orderStatus = getApplicationContext().getSharedPreferences("orderStatus", MODE_PRIVATE);
        CountPref = getApplicationContext().getSharedPreferences("CountPreferences",MODE_PRIVATE);
        userAddress = null;

        product_count = CountPref.getInt("product_count",0);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously();
        }

        db = FirebaseFirestore.getInstance();

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.buy_toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.activity_name);
        toolbar_title.setText("Buy");

        i = new Intent(this, OrderActivity.class);

        mlocation = new Location(LocationManager.NETWORK_PROVIDER);
        // mlocation.setLatitude(26.081115);
        //  mlocation.setLongitude(91.559104);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mlocation.setLatitude(location.getLatitude());
                mlocation.setLongitude(location.getLongitude());
                mlocation.setAccuracy(location.getAccuracy());
                mlocation.setProvider(location.getProvider());
                mlocation.setAltitude(location.getAltitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getApplicationContext(), "(Provider error) Unable to detect location at the moment",Toast.LENGTH_SHORT).show();
            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Button proceed = (Button) findViewById(R.id.bt_proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (true) {
                    SharedPreferences.Editor editor = userForm.edit();
                    // editor.putString("userAddress", userAddress);
                    //  editor.apply();
                    final Map<String, Object> OrderData = new HashMap<>();
                    OrderData.put("userName", userForm.getString("userName", "SharedPrefError"));
                    OrderData.put("userMobile", userForm.getString("userMobile", "SharedPrefError"));
                    OrderData.put("userEmail", userForm.getString("userEmail", "SharedPrefError"));
                    OrderData.put("userAddress", userAddress);

                    for (int i = 0; i < product_count; i++) {
                        int qty;
                        if (i > 9)
                            qty = CartPref.getInt("TE00" + i, -1);
                        else
                            qty = CartPref.getInt("TE000" + i, -1);
                        if (qty > 0) {
                            if (qty > 9)
                                OrderData.put("TE00" + i, qty + " items");
                            else
                                OrderData.put("TE000" + i, qty + " items");
                        }
                    }
                    OrderData.put("totalBill", String.valueOf(CartStatusVariablesPref.getInt("total_price", 0)));
                    OrderData.put("totalQty", String.valueOf(CartStatusVariablesPref.getInt("total_qty", 0)));
                    final int[] order_id = new int[2];

                    db.collection("orders").document("order_count").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            OrderCountObj order_count = documentSnapshot.toObject(OrderCountObj.class);
                            order_id[0] = order_count.getCount() + 1;
                            order_count.setCount(order_id[0]);
                            Random rand = new Random();
                            int otp = (int) (Math.random() * 10000);
                            OrderData.put("orderId", order_id[0]);
                            OrderData.put("otp", otp);
                            SharedPreferences.Editor editor = orderStatus.edit();
                            editor.putInt("otp", otp);
                            editor.apply();
                            editor.putInt("orderId", order_id[0]);
                            editor.apply();
                            db.collection("orders").document("order_count").set(order_count);
                            db.collection("orders").document("" + order_id[0]).set(OrderData);
                            startActivity(i);
                        }
                    });

                } else
                    Toast.makeText(getApplicationContext(), "Address is not recorded", Toast.LENGTH_LONG);
            }
        });


        Button location_access = (Button) findViewById(R.id.bt_location_check);
        location_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.setVisibility(View.GONE);
                getLocation();

            }
        });
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

    public void getLocation() {
        String result = null;
        double distance=0;

        Location store_location = new Location(LocationManager.PASSIVE_PROVIDER);
        store_location.setLatitude(26.115792);
        store_location.setLongitude(91.613898);
        distance = mlocation.distanceTo(store_location);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Obtaining location");
        progressDialog.show();

        try {
            List<Address> addressList = geocoder.getFromLocation(
                    mlocation.getLatitude(), mlocation.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();

                String eligibility = "Sorry! can't deliver to your location";
                if (((float) distance / 1000) < (float) 20) {
                    eligibility = "Your location is eligible for delivery";
                    userAddress = mlocation;
                    eligible = true;
                } else {
                    eligible = false;
                    eligibility = "We don't deliver to your location yet.";
                }
                Toast.makeText(this, result + "\n" + (float) distance / 1000 + "KM away from store\n" + eligibility, Toast.LENGTH_SHORT).show();
                TextView tv_location = (TextView) findViewById(R.id.tv_location_appears);
                tv_location.setText(result + "\n" + (float) distance / 1000 + "KM away from store");
                progressDialog.dismiss();
                TextView tv_eligibility = (TextView) findViewById(R.id.tv_eligibility_status);
                tv_eligibility.setText(eligibility);
            }
        } catch (IOException e) {
            Log.e("TAG", "Unable connect to Geocoder", e);
        }


    }

}
