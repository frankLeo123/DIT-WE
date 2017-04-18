package com.bupt.weeat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bupt.weeat.R;
import com.bupt.weeat.model.GoodBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WeekRankAdapter extends RecyclerView.Adapter<WeekRankAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<GoodBean> list;


    private static final String TAG = WeekRankAdapter.class.getSimpleName();

    public WeekRankAdapter(Context mContext, ArrayList<GoodBean> list) {
        this.mContext = mContext;
        this.list = list;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.week_rank_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        try {
            viewHolder.weekDishIndex.setText(position + "");
            viewHolder.weekDishName.setText(list.get(position).getName());
            viewHolder.weekDishPraise.setText(list.get(position).getPraise());
            if (list.get(position).getImageUrl() != null) {
                Picasso.with(mContext)
                        .load(list.get(position).getImageUrl())
                        .centerCrop()
                        .resize(160, 120)
                        .into(viewHolder.weekDishImage);
            }
            viewHolder.praiseNumLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strNum = (String) viewHolder.weekDishPraise.getText();
                    int intNum = Integer.parseInt(strNum);
                    viewHolder.weekDishPraise.setText(++intNum + "");

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView weekDishIndex, weekDishName, weekDishPraise;
        ImageView weekDishImage, weekDishPraiseImage, selectDetail;
        LinearLayout praiseNumLayout;

        public ViewHolder(View view) {
            super(view);
            weekDishIndex = (TextView) view.findViewById(R.id.week_dish_index);
            weekDishName = (TextView) view.findViewById(R.id.week_dish_name);
            weekDishPraise = (TextView) view.findViewById(R.id.week_dish_praise_num);
            weekDishPraiseImage = (ImageView) view.findViewById(R.id.week_dish_praise_image);
            weekDishImage = (ImageView) view.findViewById(R.id.week_dish_image);
            selectDetail = (ImageView) view.findViewById(R.id.select_dish_detail);
            praiseNumLayout = (LinearLayout) view.findViewById(R.id.praiseNumLayout);


        }
    }

}
