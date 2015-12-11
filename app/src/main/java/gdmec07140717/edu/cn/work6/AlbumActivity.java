package gdmec07140717.edu.cn.work6;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.Vector;

public class AlbumActivity extends Activity {
    private ViewFlipper flipper;
    private Bitmap[] mBgList;
    private long startTime = 0;
    private SensorManager sm;
    private SensorEventListener sel;

    public String[] loadAlbum(){
        String pathName = android.os.Environment.getExternalStorageDirectory().getPath()+"/com.demo.pr4";
        File file = new File(pathName);
        Vector<Bitmap> fileName = new Vector<>();
        if (file.exists() && file.isDirectory()){
            String[] str = file.list();
            for (String s : str){
                if (new File(pathName+"/"+s).isFile()){
                    fileName.addElement(loadImage(pathName + "/" + s));
                }
            }
            mBgList =fileName.toArray(new Bitmap[]{});
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        flipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
        loadAlbum();
        if (mBgList == null){
            Toast.makeText(this,"相册无图片",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }else{
            for (int i = 0;i <= mBgList.length-1;i++){
                flipper.addView(addImage(mBgList[i]),i,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT));
            }
        }

        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sel = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent se) {
                float x = se.values[SensorManager.DATA_X];

                if (x > 10 && System.currentTimeMillis() > startTime+1000){
                    startTime = System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_right_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this, R.anim.push_right_out));
                    flipper.showPrevious();
                }else if (x < -10 && System.currentTimeMillis() > startTime +1000){
                    startTime = System.currentTimeMillis();
                    flipper.setInAnimation(AnimationUtils.loadAnimation(AlbumActivity.this,R.anim.push_left_in));
                    flipper.setOutAnimation(AnimationUtils.loadAnimation(AlbumActivity.this, R.anim.push_left_out));
                    flipper.showNext();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sm.registerListener(sel, sensor,SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onDestroy(){
        super.onDestroy();
        sm.unregisterListener(sel);
    }
    public Bitmap loadImage(String pathName){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeFile(pathName,options);
        WindowManager manager = getWindowManager();
        Display display = manager.getDefaultDisplay();
        int screenWidth = display.getWidth();
        options.inSampleSize = options.outWidth/screenWidth;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName,options);
        return bitmap;
    }

    private View addImage(Bitmap bitmap){
        ImageView img = new ImageView(this);
        img.setImageBitmap(bitmap);
        return img;
    }
}
