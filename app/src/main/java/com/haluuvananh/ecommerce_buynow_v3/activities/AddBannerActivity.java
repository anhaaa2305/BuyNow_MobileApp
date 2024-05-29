package com.haluuvananh.ecommerce_buynow_v3.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.haluuvananh.ecommerce_buynow_v3.R;
import com.haluuvananh.ecommerce_buynow_v3.model.BannerModel;
import com.haluuvananh.ecommerce_buynow_v3.utils.FirebaseUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddBannerActivity extends AppCompatActivity {
    TextInputEditText idEditText, descEditText;
    Button imageBtn, addBannerBtn;
    ImageView backBtn, bannerImageView;
    TextView removeImageBtn;

    AutoCompleteTextView statusDropDown;
    ArrayAdapter<String> arrayAdapter;
    String status, bannerImage;
    int bannerId;
    Context context = this;
    boolean imageUploaded = false;

    //    ProgressDialog dialog;
    SweetAlertDialog dialog;
    SweetAlertDialog sweetAlertDialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_banner);

        idEditText = findViewById(R.id.idEditText);
        statusDropDown = findViewById(R.id.statusDropDown);
        descEditText = findViewById(R.id.descEditText);
        bannerImageView = findViewById(R.id.bannerImageView);

        imageBtn = findViewById(R.id.imageBtn);
        addBannerBtn = findViewById(R.id.addBannerBtn);
        backBtn = findViewById(R.id.backBtn);
        removeImageBtn = findViewById(R.id.removeImageBtn);

        FirebaseUtil.getDetails().get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bannerId = Integer.parseInt(task.getResult().get("lastBannerId").toString()) + 1;
                        idEditText.setText(bannerId + "");
                    }
                });

        arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, new String[]{"Live", "Not Live"});
        statusDropDown.setAdapter(arrayAdapter);
        statusDropDown.setOnItemClickListener((adapterView, view, i, l) -> {
            status = adapterView.getItemAtPosition(i).toString();
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        });

        imageBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 101);
        });


        addBannerBtn.setOnClickListener(v -> {
            sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            sweetAlertDialog.setTitleText("Adding banner...");
            sweetAlertDialog.setCancelable(false);
            addToFirebase();
        });

        backBtn.setOnClickListener(v -> onBackPressed());

        removeImageBtn.setOnClickListener(v -> removeImage());

        dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Uploading image...");
        dialog.setCancelable(false);
    }

    private void addToFirebase() {
        if (!validate())
            return;
        String bannerDesc = descEditText.getText().toString();
        BannerModel banner = new BannerModel(bannerId, bannerImage, bannerDesc, status);
        FirebaseUtil.getBanner().add(banner)
                .addOnSuccessListener(documentReference -> FirebaseUtil.getDetails().update("lastBannerId", bannerId)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                sweetAlertDialog.dismiss();
                                Toast.makeText(AddBannerActivity.this, "Banner has been added successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }));

    }
    private void removeImage() {
        SweetAlertDialog alertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        alertDialog
                .setTitleText("Are you sure?")
                .setContentText("Do you want to remove this image?")
                .setConfirmText("Yes")
                .setCancelText("No")
                .setConfirmClickListener(sweetAlertDialog -> {
                    imageUploaded = false;
                    bannerImageView.setImageDrawable(null);
                    bannerImageView.setVisibility(View.GONE);
                    removeImageBtn.setVisibility(View.GONE);
                    FirebaseUtil.getBannerImageReference(bannerId + "").delete();
                    alertDialog.dismiss();
                }).show();
    }

    private boolean validate() {
        boolean isValid = true;
        if (idEditText.getText().toString().trim().isEmpty()) {
            idEditText.setError("Id is required");
            isValid = false;
        }
        if (status.trim().isEmpty()) {
            statusDropDown.setError("Category is required");
            isValid = false;
        }
        if (descEditText.getText().toString().trim().isEmpty()) {
            descEditText.setError("Description is required");
            isValid = false;
        }

        if (!imageUploaded) {
            Toast.makeText(context, "Image is not selected", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (data != null && data.getData() != null) {
                Uri imageUri = data.getData();
                if (idEditText.getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Please fill the id first", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.show();

                bannerId = Integer.parseInt(idEditText.getText().toString());
                FirebaseUtil.getBannerImageReference(bannerId + "").putFile(imageUri)
                        .addOnCompleteListener(t -> {
                            imageUploaded = true;
                            FirebaseUtil.getBannerImageReference(bannerId + "").getDownloadUrl().addOnSuccessListener(uri -> {
                                bannerImage = uri.toString();

                                Picasso.get().load(uri).into(bannerImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        dialog.dismiss();
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                    }
                                });
                                bannerImageView.setVisibility(View.VISIBLE);
                                removeImageBtn.setVisibility(View.VISIBLE);
                            });
                        });
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        FirebaseUtil.getBannerImageReference(bannerId + "").delete();
    }
}