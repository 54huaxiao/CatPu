package com.example.shick.stepcounter;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.*;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Vincent on 2016/12/19.
 */

public class BeginActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, OnItemClickListener {
    private ConvenientBanner convenientBanner;//顶部广告栏控件
    private ArrayList<Integer> localImages = new ArrayList<Integer>();
    private FloatingActionButton change;
    private int click = 0;
    private ArrayList<String> transformerList = new ArrayList<String>();
    private static int AUTO_TURNING_FREQ = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.begin_layout);
        init();
    }

    private void init(){
        initViews();
        initImageLoader();
        loadTestData();
        initListeners();
    }

    private void initViews() {
        convenientBanner = (ConvenientBanner) findViewById(R.id.convenientBanner);
        String transformerName = ForegroundToBackgroundTransformer.class.getSimpleName();
        change = (FloatingActionButton) findViewById(R.id.change);
        try {
            Class cls = Class.forName("com.ToxicBakery.viewpager.transforms." + transformerName);
            ABaseTransformer transformer = (ABaseTransformer)cls.newInstance();
            convenientBanner.getViewPager().setPageTransformer(true,transformer);

            //部分3D特效需要调整滑动速度
            if(transformerName.equals("StackTransformer")){
                convenientBanner.setScrollDuration(1200);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void initListeners() {
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click = (++click) % transformerList.size();
                String transformerName = transformerList.get(click);
                try {
                    Class cls = Class.forName("com.ToxicBakery.viewpager.transforms." + transformerName);
                    ABaseTransformer transformer = (ABaseTransformer)cls.newInstance();
                    convenientBanner.getViewPager().setPageTransformer(true,transformer);

                    //部分3D特效需要调整滑动速度
                    if(transformerName.equals("StackTransformer")){
                        convenientBanner.setScrollDuration(1200);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //初始化网络图片缓存库
    private void initImageLoader(){
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.mipmap.ic_default_adimage)
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

    private void loadTestData() {
        // load local images
        for (int position = 0; position < 6; position++)
            localImages.add(getResId("ic_test_" + position, R.mipmap.class));

        //本地图片例子
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                .setOnItemClickListener(this);

        //各种翻页效果
        transformerList.add(DefaultTransformer.class.getSimpleName());
        transformerList.add(AccordionTransformer.class.getSimpleName());
        transformerList.add(BackgroundToForegroundTransformer.class.getSimpleName());
        transformerList.add(CubeInTransformer.class.getSimpleName());
        transformerList.add(CubeOutTransformer.class.getSimpleName());
        transformerList.add(DepthPageTransformer.class.getSimpleName());
        transformerList.add(FlipHorizontalTransformer.class.getSimpleName());
        transformerList.add(FlipVerticalTransformer.class.getSimpleName());
        transformerList.add(ForegroundToBackgroundTransformer.class.getSimpleName());
        transformerList.add(RotateDownTransformer.class.getSimpleName());
        transformerList.add(RotateUpTransformer.class.getSimpleName());
        transformerList.add(StackTransformer.class.getSimpleName());
        transformerList.add(ZoomInTransformer.class.getSimpleName());
        transformerList.add(ZoomOutTranformer.class.getSimpleName());
    }

    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName
     * @param c
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // start auto turning pages every 5 seconds
    @Override
    protected void onResume() {
        super.onResume();
        convenientBanner.startTurning(AUTO_TURNING_FREQ);
    }

    // stop auto turning pages
    @Override
    protected void onPause() {
        super.onPause();
        convenientBanner.stopTurning();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//      pass
    }

    @Override
    public void onPageSelected(int position) {
        Toast.makeText(this,"jump to page " + position + ".",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        pass
    }

    @Override
    public void onItemClick(int position) {
//        jump to MainActivity
        Intent intent = new Intent(BeginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}