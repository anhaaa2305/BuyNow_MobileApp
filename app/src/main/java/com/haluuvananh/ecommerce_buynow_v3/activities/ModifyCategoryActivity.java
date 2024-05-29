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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.haluuvananh.ecommerce_buynow_v3.R;
import com.haluuvananh.ecommerce_buynow_v3.model.CategoryModel;
import com.haluuvananh.ecommerce_buynow_v3.utils.FirebaseUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ModifyCategoryActivity extends AppCompatActivity {
    LinearLayout detailsLinearLayout;
    TextInputEditText nameEditText, descEditText, colorEditText;
    Button imageBtn, modifyCategoryBtn;
    ImageView backBtn, categoryImageView;
    TextView removeImageBtn;
    AutoCompleteTextView idDropDown;
    ArrayAdapter<String> idAdapter;
    CategoryModel currCategory;
    String docId, categoryImage;
    Uri imageUri;
    int categoryId;
    Context context = this;
    boolean imageUploaded = true;

    SweetAlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_category);

        detailsLinearLayout = findViewById(R.id.detailsLinearLayout);
        idDropDown = findViewById(R.id.idDropDown);
        nameEditText = findViewById(R.id.nameEditText);
        descEditText = findViewById(R.id.descriptionEditText);
        colorEditText = findViewById(R.id.colorEditText);

        categoryImageView = findViewById(R.id.categoryImageView);
        imageBtn = findViewById(R.id.imageBtn);
        modifyCategoryBtn = findViewById(R.id.modifyCategoryBtn);
        backBtn = findViewById(R.id.backBtn);
        removeImageBtn = findViewById(R.id.removeImageBtn);

        imageBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 101);
        });

        modifyCategoryBtn.setOnClickListener(v -> updateToFirebase());

        backBtn.setOnClickListener(v -> onBackPressed());

        removeImageBtn.setOnClickListener(v -> removeImage());

        dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Uploading image...");
        dialog.setCancelable(false);

        initDropDown((categoriesList, docIdList) -> {
            String[] ids = new String[categoriesList.size()];
            for (int i=0; i<categoriesList.size(); i++)
                ids[i] = Integer.toString(categoriesList.get(i).getCategoryId());

            idAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, ids);
            idDropDown.setAdapter(idAdapter);
            idDropDown.setOnItemClickListener((adapterView, view, i, l) -> {
                docId = docIdList.get(i);
                initCategory(categoriesList.get(i));
            });
        });
    }

    private void initDropDown(MyCallback myCallback) {
        FirebaseUtil.getCategories().orderBy("categoryId")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int i = 0;
                        List<CategoryModel> categories = new ArrayList<>();
                        List<String> docIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            categories.add(document.toObject(CategoryModel.class));
                            docIds.add(document.getId());
                            i++;
                        }
                        myCallback.onCallback(categories, docIds);
                    }
                });
    }

    private void initCategory(CategoryModel model) {
        currCategory = model;
        categoryId = currCategory.getCategoryId();

        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Modifying...");
        dialog.setCancelable(false);

        Picasso.get().load(currCategory.getIcon()).into(categoryImageView, new Callback() {
            @Override
            public void onSuccess() {
                dialog.dismiss();
            }
            @Override
            public void onError(Exception e) {
            }
        });

        detailsLinearLayout.setVisibility(View.VISIBLE);
        categoryImageView.setVisibility(View.VISIBLE);
        removeImageBtn.setVisibility(View.VISIBLE);

        nameEditText.setText(currCategory.getName());
        descEditText.setText(currCategory.getBrief());
        colorEditText.setText(currCategory.getColor());
    }

    private void updateToFirebase(){
        if (!validate())
            return;

        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialog.setTitleText("Loading...");
        dialog.setCancelable(false);
        dialog.show();
        if (imageUri != null ) {
            FirebaseUtil.getCategoryImageReference(categoryId + "").putFile(imageUri)
                    .addOnCompleteListener(t -> FirebaseUtil.getCategoryImageReference(categoryId + "").getDownloadUrl().addOnSuccessListener(uri -> {
                        categoryImage = uri.toString();
                        FirebaseUtil.getCategories().document(docId).update("icon", categoryImage);
                        updateDataToFirebase();
                        dialog.dismiss();
                        Toast.makeText(context, "Category has been modified successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }));
        }
        else {
            updateDataToFirebase();
            dialog.dismiss();
            Toast.makeText(context, "Category has been modified successfully!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void updateDataToFirebase() {
        if (!Objects.requireNonNull(nameEditText.getText()).toString().equals(currCategory.getName()))
            FirebaseUtil.getCategories().document(docId).update("name", nameEditText.getText().toString());
        if (!Objects.requireNonNull(descEditText.getText()).toString().equals(currCategory.getBrief()))
            FirebaseUtil.getCategories().document(docId).update("brief", descEditText.getText().toString());
        if (!Objects.requireNonNull(colorEditText.getText()).toString().equals(currCategory.getColor()))
            FirebaseUtil.getCategories().document(docId).update("color", colorEditText.getText().toString());
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
                    categoryImageView.setImageDrawable(null);
                    categoryImageView.setVisibility(View.GONE);
                    removeImageBtn.setVisibility(View.GONE);
                    alertDialog.cancel();
                }).show();
    }

    boolean validate() {
        boolean isValid = true;
        if (Objects.requireNonNull(nameEditText.getText()).toString().trim().isEmpty()) {
            nameEditText.setError("Name is required");
            isValid = false;
        }
        if (Objects.requireNonNull(descEditText.getText()).toString().trim().isEmpty()) {
            descEditText.setError("Description is required");
            isValid = false;
        }
        if (Objects.requireNonNull(colorEditText.getText()).toString().trim().isEmpty()) {
            colorEditText.setError("Color is required");
            isValid = false;
        }
        if (colorEditText.getText().toString().charAt(0) != '#') {
            colorEditText.setError("Color should be HEX value");
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
                imageUri = data.getData();
                imageUploaded = true;
                dialog.show();

                Picasso.get().load(imageUri).into(categoryImageView, new Callback() {

                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
                categoryImageView.setVisibility(View.VISIBLE);
                removeImageBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface MyCallback {
        void onCallback(List<CategoryModel> categories, List<String> docIds);
    }
}