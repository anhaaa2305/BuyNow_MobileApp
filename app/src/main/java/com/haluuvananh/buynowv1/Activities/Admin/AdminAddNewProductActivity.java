package com.haluuvananh.buynowv1.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.esotericsoftware.kryo.util.IntArray;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.haluuvananh.buynowv1.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class AdminAddNewProductActivity extends AppCompatActivity {

    public Button addNewProductButton;
    public EditText inputProductNameEdt, inputProductDescriptionEdt, inputProductPriceEdt;
    public ImageView selectProductImv;
    private static final int galleryPick = 1;
    public Uri imgUri;
    public String categoryName, pName, pDescription, pPrice;
    public String saveCurrentDate, saveCurrentTime;
    public String productRandomKey, downLoadImageUrl;
    public StorageReference productImageRef;
    public DatabaseReference productRef;
    public ProgressDialog loadingProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        // Match Object with Layout
        // Static object
        inputProductNameEdt = findViewById(R.id.product_name_edt);
        inputProductDescriptionEdt = findViewById(R.id.product_description_edt);
        inputProductPriceEdt = findViewById(R.id.product_price_edt);
        // Action object
        addNewProductButton = findViewById(R.id.add_new_product_btn);
        selectProductImv = findViewById(R.id.select_product_imv);
        loadingProgressBar = new ProgressDialog(this);
        String categoryName = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("category")).toString();
        productImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        selectProductImv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateProductData();
            }
        });
    }

    private void OpenGallery() {
        // Configure for gallery intent
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == galleryPick && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            selectProductImv.setImageURI(imgUri);
            //selectProductImv.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private void ValidateProductData() {
        pDescription = inputProductDescriptionEdt.getText().toString();
        pName = inputProductNameEdt.getText().toString();
        pPrice = inputProductPriceEdt.getText().toString();

        if (imgUri == null) {
            Toast.makeText(this, "Oops! Image selection failed. Please retry.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pDescription)) {
            Toast.makeText(this, "Please enter product description...", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(pName)) {
            Toast.makeText(this, "Please enter product name...", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(pPrice) || (Integer.parseInt(pPrice) < 0)) {
            Toast.makeText(this, "Please enter valid product price...", Toast.LENGTH_SHORT).show();
        } else {
            loadingProgressBar.setTitle("Processing");
            loadingProgressBar.setMessage("Saving product details. Please wait...");
            loadingProgressBar.setCanceledOnTouchOutside(false);
            loadingProgressBar.show();
            StoreProductInfomation();
        }
    }

    private void StoreProductInfomation() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        // Configure key for Product
        productRandomKey = saveCurrentDate + saveCurrentTime;
        // Configure firebase
        StorageReference filePath = productImageRef.child(imgUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imgUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, message, Toast.LENGTH_SHORT).show();
                loadingProgressBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image upload Successfully...", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            loadingProgressBar.dismiss();
                            throw Objects.requireNonNull(task.getException());
                        }
                        downLoadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downLoadImageUrl = task.getResult().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "getting Product image Url Successfully...", Toast.LENGTH_SHORT).show();
                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });

    }
    private void SaveProductInfoToDatabase(){
        HashMap<String, Object> productMap = new HashMap<String, Object>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("name", pName);
        productMap.put("description", pDescription);
        productMap.put("image", downLoadImageUrl);
        productMap.put("price", pPrice);
        productMap.put("category",categoryName);

        // Save to database
        productRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingProgressBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Product is saved Successfully...", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                    startActivity(i);
                }
                else {
                    String message = Objects.requireNonNull(task.getException()).toString();
                    loadingProgressBar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}