package com.bupt.weeat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupt.weeat.R;
import com.bupt.weeat.model.GoodBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

//暂时改为了NewGoodAdapter
public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.ViewHolder> {
//    private int[] imageIds = {R.drawable.ic_heat_statistics, R.drawable.ic_recipe_recommended};
//    private String[] textArray = {"热量统计", "食谱推荐"};
//    private Context context;
//
//    public CollectAdapter(Context context) {
//        this.context = context;
//    }
private ArrayList<GoodBean> list;
    private Context mContext;

    public CollectAdapter(ArrayList<GoodBean> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.heath_fragment_list_item, parent, false);
//        return new ViewHolder(view);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.new_good_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.heathItemTv.setText(textArray[position]);
//        holder.heathItemImg.setImageResource(imageIds[position]);
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
//        return imageIds.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
//        @InjectView(R.id.heath_item_tv)
//        TextView heathItemTv;
//        @InjectView(R.id.heath_item_iv)
//        ImageView heathItemImg;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            ButterKnife.inject(this, itemView);
//        }
//    }
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
