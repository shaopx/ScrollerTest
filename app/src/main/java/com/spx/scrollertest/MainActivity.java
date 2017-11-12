package com.spx.scrollertest;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MyImageview myImageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String imageUrl = "https://cdn.ruguoapp.com/d73fc74b083302c47b230335cd033fc6?imageView2/0/h/1379/interlace/1";
//        String imageUrl = "http://f.hiphotos.baidu.com/image/pic/item/2fdda3cc7cd98d1004fc53762a3fb80e7bec9048.jpg";
//        String imageUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510288427151&di=1e3c071b546dd0c0b3dcd067c6521886&imgtype=0&src=http%3A%2F%2Fc.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Ff9198618367adab4e8fd4a1e83d4b31c8701e4ba.jpg";
//        String imageUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510298447871&di=bde7aa0c2feb82ec3bff4a37e4ed13ac&imgtype=0&src=http%3A%2F%2Fimg5q.duitang.com%2Fuploads%2Fitem%2F201407%2F27%2F20140727020316_Q35Bj.jpeg";
        myImageview = findViewById(R.id.mylinearlayout);
//        myImageview.setScrollOutListener(new MyImageview.ScrollOutListener() {
//            @Override
//            public void onScrollOut() {
//                finish();
//            }
//        });
        Glide.with(this).asBitmap().load(imageUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                handleBitmap(resource);
            }
        });

//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timg);
//        handleBitmap(bitmap);
    }

    private void handleBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Log.d(TAG, "handleBitmap: bitmapWidth:"+bitmapWidth+",bitmapHeight:"+bitmapHeight);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        Log.d(TAG, "handleBitmap: screenWidth:"+screenWidth+",screenHeight:"+screenHeight);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //应用区域
        Rect outRect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect1);
        Log.d(TAG, "handleBitmap outRect1: "+outRect1);
        int statusBar = dm.heightPixels - outRect1.height();  //状态栏高度=屏幕高度-应用区域高度
        Log.e(TAG, "状态栏-方法4:" + statusBar);

        //View绘制区域
//        Rect outRect2 = new Rect();
//        getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect2);
//        Log.e(TAG, "View绘制区域顶部-错误方法：outRect2:" + outRect2);

        float scale = 1f;
        Log.d(TAG, "handleBitmap: screenWidth:"+screenWidth+",bitmapWidth:"+bitmapWidth);
        if (screenWidth > bitmapWidth) {
            scale = 1f * screenWidth / bitmapWidth;
        }
        Log.d(TAG, "handleBitmap: scale:"+scale);

        matrix.setScale(scale, scale);
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        myImageview.setBitmap(bitmap);
        myImageview.setScrollOutListener(new MyImageview.ScrollOutListener() {
            @Override
            public void onScrollOut() {
                finish();
            }
        });
    }
}
