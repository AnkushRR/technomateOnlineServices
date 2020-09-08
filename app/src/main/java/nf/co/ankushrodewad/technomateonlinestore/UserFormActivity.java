package nf.co.ankushrodewad.technomateonlinestore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserFormActivity extends AppCompatActivity {

    SharedPreferences userForm;
    String user_name, user_email, user_mobile;
    EditText et_name, et_email, et_mobile;
    Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        userForm = getApplicationContext().getSharedPreferences("userForm", MODE_PRIVATE);
        et_name = (EditText) findViewById(R.id.et_user_form_name);
        et_email = (EditText) findViewById(R.id.et_user_form_email);
        et_mobile = (EditText) findViewById(R.id.et_user_form_mobile);
        proceed = (Button) findViewById(R.id.bt_user_form_goToBuy);


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int c = 0;
                SharedPreferences.Editor editor = userForm.edit();
                if (et_name.getText().length() > 1) {
                    user_name = String.valueOf(et_name.getText());
                    editor.putString("userName", user_name);
                    editor.apply();
                    c++;
                } else
                    Toast.makeText(getApplicationContext(), "Enter a valid name", Toast.LENGTH_SHORT).show();

                if (et_email.getText() != null && et_email.getText().toString().contains("@")) {
                    user_email = String.valueOf(et_email.getText());
                    editor.putString("userEmail", user_email);
                    editor.apply();
                    c++;
                } else
                    Toast.makeText(getApplicationContext(), "Enter a valid email", Toast.LENGTH_SHORT).show();

                if (et_mobile.getText() != null && et_mobile.getText().toString().length() == 10) {
                    user_mobile = et_mobile.getText().toString();
                    editor.putString("userMobile", user_mobile);
                    editor.apply();
                    c++;
                } else
                    Toast.makeText(getApplicationContext(), "Enter a valid mobile number", Toast.LENGTH_SHORT).show();

                if (c == 3) {
                    Intent i = new Intent(getApplicationContext(), BuyActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ProductsActivity.class);
        startActivity(i);

    }
}
