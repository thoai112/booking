package com.kurata.hotelmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.kurata.hotelmanagement.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    //ArrayList<Uri> ImageUrls;
    ArrayList<String> ImageUrls;
    LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context, ArrayList<String> imageUrls) {
        this.context = context;
        ImageUrls = imageUrls;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ImageUrls.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view=layoutInflater.inflate(R.layout.showimageslayout,container,false);
        ImageView imageView=view.findViewById(R.id.UploadImage);
        //imageView.setImageURI(ImageUrls.get(position));
        Glide.with(imageView).load(ImageUrls.get(position)).into(imageView);
        container.addView(view);

        return view ;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view== object;
    }

    @Override
    public void destroyItem(@NonNull View container, int position, @NonNull Object object) {
        ((RelativeLayout)object).removeView(container);
    }
}
