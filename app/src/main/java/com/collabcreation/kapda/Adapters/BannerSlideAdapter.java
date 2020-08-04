package com.collabcreation.kapda.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.collabcreation.kapda.R;
import com.collabcreation.kapda.model.Banner;

import java.util.List;

public class BannerSlideAdapter extends PagerAdapter {
    private Context context;
    private List<Banner> bannerList;

    public BannerSlideAdapter(Context context, List<Banner> bannerList) {
        this.context = context;
        this.bannerList = bannerList;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Banner currentBanner = bannerList.get(position);
        View bannerView = LayoutInflater.from(container.getContext()).inflate(R.layout.banner_item, container, false);
        ImageView bannerImage = bannerView.findViewById(R.id.bannerImage);
        Glide.with(context)
                .load(currentBanner.getBannerUrl())
                .into(bannerImage);
        container.addView(bannerView);
        return bannerView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
