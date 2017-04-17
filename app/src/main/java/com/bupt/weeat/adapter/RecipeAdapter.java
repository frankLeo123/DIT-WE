package com.bupt.weeat.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bupt.weeat.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private int[] imageIds = {R.drawable.ic_exercise_recipe, R.drawable.ic_health_recipe, R.drawable.ic_beauty_recipe};
    private String[] textArray = {"健身食谱", "养生食谱", "养颜食谱"};
    private Context context;

    public RecipeAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.recipeTitle.setText(textArray[position]);
        holder.recipeIcon.setImageResource(imageIds[position]);

    }

    @Override
    public int getItemCount() {
        return textArray.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(value = R.id.recipe_item_iv)
        ImageView recipeIcon;
        @InjectView(value = R.id.recipe_item_tv)
        TextView recipeTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
