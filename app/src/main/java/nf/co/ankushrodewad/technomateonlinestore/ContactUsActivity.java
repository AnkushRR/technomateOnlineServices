package nf.co.ankushrodewad.technomateonlinestore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class ContactUsActivity extends AppCompatActivity {

    boolean loadingFinished;
    boolean redirect;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.contact_us_toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.activity_name);
        toolbar_title.setText("Contact Us");

        WebView webView = (WebView) findViewById(R.id.webView_contact_us);
        webView.loadUrl("http://www.technomateedubotics.com/contact.php");
        loadingFinished = true;
        redirect = false;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Webpage is loading");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                if (!loadingFinished) {
                    redirect = true;
                }

                loadingFinished = false;
                //   view.loadUrl(urlNewString);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                loadingFinished = false;
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    loadingFinished = true;
                }

                if (loadingFinished && !redirect) {
                    //HIDE LOADING IT HAS FINISHED
                    progressDialog.dismiss();
                } else {
                    redirect = false;
                }

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
}
