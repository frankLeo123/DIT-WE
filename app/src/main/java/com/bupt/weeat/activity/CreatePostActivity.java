package com.bupt.weeat.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bupt.weeat.R;
import com.bupt.weeat.entity.Post;
import com.bupt.weeat.entity.User;
import com.bupt.weeat.utils.LogUtils;
import com.bupt.weeat.utils.ToastUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.InjectView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

//发帖功能，没找到相机,是发帖详情界面
public class CreatePostActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_ALBUM = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private File PhotoFile;
    private String mCurrentPhotoPath;
    User user;
    private String imageUri;
    private Context mContext;
    @InjectView(R.id.post_image_list)
    LinearLayout imageList;
    @InjectView(R.id.select_picture)
    TextView select_picture;
    @InjectView(R.id.take_picture)
    TextView take_picture;
    @InjectView(R.id.pic1)
    ImageView pic1;
    @InjectView(R.id.comment_content_et)
    EditText commentContent;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    private static final String TAG = CreatePostActivity.class.getSimpleName();


    @Override
    public int getLayoutId() {
        return R.layout.create_post_activity;
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtils.i(TAG, "onCreate");
        mContext = this;
        user = BmobUser.getCurrentUser(this, User.class);
        initToolbar();

    }


    private void initToolbar() {
        setSupportActionBar(toolbar);
        setTitle("发帖");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void setListener() {
        take_picture.setOnClickListener(this);
        select_picture.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_post_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.send_new_post) {
            String postContent = commentContent.getText().toString().trim();
            if (postContent.isEmpty()) {
                ToastUtils.showToast(this, R.string.no_empty_content, Toast.LENGTH_SHORT);
            } else {
                new sendPostTask().execute(postContent);
            }
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        LogUtils.i(TAG, "back to square fragment");
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    try {
                        PhotoFile = createImageFile();
                        LogUtils.i(TAG, "" + PhotoFile);
                    } catch (IOException e) {
                        LogUtils.i(TAG, "");
                        e.printStackTrace();
                    }
                    if (PhotoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(PhotoFile));
                        startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
                    }
                }
                break;
            case R.id.select_picture:
                Intent selectPictureIntent = new Intent(Intent.ACTION_PICK);
                selectPictureIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(selectPictureIntent,
                        "Select Picture"), REQUEST_CODE_ALBUM);
                break;
            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ALBUM:
                    LogUtils.i(TAG, "onActivityResult" + REQUEST_CODE_ALBUM);
                    if (data != null) {
                        Uri originalUri = data.getData();
                        imageUri = getRealPathFromUri(originalUri);
                        LogUtils.i(TAG, imageUri);
                        imageList.setVisibility(View.VISIBLE);
                        Picasso.with(this)
                                .load(Uri.parse("file://" + imageUri))
                                .centerCrop()
                                .resize(60, 60)
                                .into(pic1);

                    }

                    break;
                case REQUEST_CODE_CAMERA:
                    LogUtils.i(TAG, "onActivityResult" + REQUEST_CODE_CAMERA);
                    galleryAddPic();
                    Uri originalUri = data.getData();
                    String targetUri = getRealPathFromUri(originalUri);
                    imageList.setVisibility(View.VISIBLE);
                    Picasso.with(this)
                            .load(targetUri)
                            .centerCrop()
                            .resize(60, 60)
                            .into(pic1);

                    break;
                default:
                    break;
            }
        }

    }

    public String getRealPathFromUri(Uri contentUri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String realPath = cursor.getString(columnIndex);
        cursor.close();
        return realPath;
    }


    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imageStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", imageStorageDir);
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }


    public void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }

    public void publishPost(final String commentContent) {
        final BmobFile imageFile = new BmobFile(new File(imageUri));

        imageFile.upload(mContext, new UploadFileListener() {

            @Override
            public void onSuccess() {
                ToastUtils.showToast(CreatePostActivity.this, R.string.upload_image_success, Toast.LENGTH_SHORT);
                publishPostNoImage(commentContent, imageFile);
                onBackPressed();
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.showToast(CreatePostActivity.this, R.string.upload_image_fail, Toast.LENGTH_SHORT);
            }
        });


    }

    public void publishPostNoImage(String commentContent, BmobFile imageFile) {
        final Post post = new Post();
        User user = BmobUser.getCurrentUser(mContext, User.class);
        post.setAuthor(user);
        post.setPraiseNum(0);
        post.setCommentNum(0);
        post.setShareNum(0);
        post.setMyPraise(false);
        post.setContent(commentContent);
        if (imageFile != null) {
            post.setPostImageFile(imageFile);
        }
        post.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                ToastUtils.showToast(CreatePostActivity.this, R.string.upload_post_success, Toast.LENGTH_SHORT);
                onBackPressed();
            }

            @Override
            public void onFailure(int i, String s) {
                ToastUtils.showToast(CreatePostActivity.this, R.string.upload_post_fail, Toast.LENGTH_SHORT);
            }
        });

    }

    class sendPostTask extends AsyncTask<String, Integer, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            if (imageUri == null) {
                publishPostNoImage(params[0], null);
            } else {
                publishPost(params[0]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }
}
