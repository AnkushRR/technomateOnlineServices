package nf.co.ankushrodewad.technomateonlinestore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class ScrollingActivity extends AppCompatActivity {

    LinearLayout lLayout;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lLayout = (LinearLayout) findViewById(R.id.layout_about_us);
        Element dev_name = new Element();
        dev_name.setTitle("Ankush Ramchandra Rodewad");

        Element dev_desc = new Element();
        dev_desc.setTitle("B.tech, CSE, IIIT Guwahati");

        Element company_name = new Element();
        company_name.setTitle("Technomate Edubotics Pvt.Ltd");


        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL,"rodewad.ankush29@gmail.com");
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("Technomate Online Store is an online electronics store. Powered by Technomate Edubotics Pvt.Ltd. And designed and developed by Ankush Rodewad.")
                .addGroup("Connect with us")
                .addItem(company_name)
                .addEmail("admin@technomateedubotics.com")
                .addWebsite("www.technomateedubotics.com")
                .addGroup("Connect with Developer")
                .addItem(dev_name)
                .addItem(dev_desc)
                .addEmail("rodewad.ankush29@gmail.com", "Contact Developer")
                .create();
        lLayout.addView(aboutPage);
    }

}
