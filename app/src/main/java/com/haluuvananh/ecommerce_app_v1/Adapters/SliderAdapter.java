package com.haluuvananh.ecommerce_app_v1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.haluuvananh.ecommerce_app_v1.R;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public SliderAdapter(Context context) {
        this.context = context;
    }
    int[] imagesArray = {
            R.drawable.onboard_screen_modified1,
            R.drawable.onboard_screen_modified4,
            R.drawable.onboard_screen_modified5,
    };
    int[] headingArray = {
            R.string.first_slide,
            R.string.second_slide,
            R.string.third_slide,
    };

    int[] descriptionArray = {
            R.string.description,
            R.string.description,
            R.string.description,
    };

    @Override
    public int getCount() {
        return headingArray.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.siding_layout,container, false);

        ImageView imageView = view.findViewById(R.id.slider_img);
        TextView heading = view.findViewById(R.id.heading);
        TextView description = view.findViewById(R.id.description);

        imageView.setImageResource(imagesArray[position]);
        heading.setText(headingArray[position]);
        description.setText(descriptionArray[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}


