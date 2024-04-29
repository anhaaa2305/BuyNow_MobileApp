package com.haluuvananh.ecommerce_app_v1.Activities;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.haluuvananh.ecommerce_app_v1.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    private Toolbar toolbar;
    private TextView subTotal, discount, shipping, total;
    private Button checkOut;
    private double amount = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        // Get data from Intent
        amount = getIntent().getDoubleExtra("amount", 0.0);
        // Match with XML
        subTotal = findViewById(R.id.sub_total);
        discount = findViewById(R.id.discount_value);
        shipping = findViewById(R.id.shipping_value);
        checkOut = findViewById(R.id.checkout_btn);
        total = findViewById(R.id.total_amt);

        subTotal.setText(amount + "$");
        checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod();
            }
        });
    }
    private void paymentMethod() {
        Checkout checkout = new Checkout();
        final Activity activity = PaymentActivity.this;
        try {
            JSONObject options = new JSONObject();
            //Set Company Name
            options.put("name", "Ecommerce app V1");
            //Ref no
            options.put("description", "Reference No. #123456");
            //Image to be display
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            //options.put("order_id", "order_9A33XWu170gUtm");
            // Currency type
            options.put("currency", "USD");
            //double total = Double.parseDouble(mAmountText.getText().toString());
            //multiply with 100 to get exact amount in rupee
            amount = amount * 100;
            //amount
            options.put("amount", amount);
            JSONObject preFill = new JSONObject();
            //email
            preFill.put("email", "anhaaa2305@gmail.com");
            //contact
            preFill.put("contact", "0816904167");
            options.put("prefill", preFill);
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show();
    }
}