package com.bupt.weeat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupt.weeat.R;
import com.bupt.weeat.model.DishBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class NewDishAdapter extends RecyclerView.Adapter<NewDishAdapter.ViewHolder> {
    private ArrayList<DishBean> list;
    private Context mContext;


    public NewDishAdapter(ArrayList<DishBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.new_good_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.NewDishLocation.setText(list.get(position).getLocation());
        holder.NewDishName.setText(list.get(position).getName());
        holder.NewDishPrice.setText(list.get(position).getPrice() + "元");
        holder.NewDishUpdateTime.setText(list.get(position).getUpdateTime());

        if (list.get(position).getImageUrl() != null) {
            Picasso.with(mContext)
                    .load(list.get(position).getImageUrl())
                    .resize(160, 120)
                    .centerCrop()
                    .into(holder.NewDishImage);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.new_dish_location)
        TextView NewDishLocation;
        @InjectView(R.id.new_dish_name)
        TextView NewDishName;
        @InjectView(R.id.new_dish_price)
        TextView NewDishPrice;
        @InjectView(R.id.new_dish_update_time)
        TextView NewDishUpdateTime;
        @InjectView(R.id.new_dish_image)
        ImageView NewDishImage;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, itemView);
        }
    }
}
