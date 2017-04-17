package com.bupt.weeat.activity;


import com.bupt.weeat.R;
import com.squareup.picasso.Picasso;

import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;

public class ImageDetailActivity extends BaseActivity {


    @InjectView(value = R.id.iv_img)
    PhotoView iv_img;
    String url;
    @Override
    public int getLayoutId() {
        return R.layout.activity_image_detail;
    }

    @Override
    protected void initData() {
        super.initData();
        url=getIntent().getStringExtra("image_url");
        Picasso.with(context).load(url).into(iv_img);
    }
}
