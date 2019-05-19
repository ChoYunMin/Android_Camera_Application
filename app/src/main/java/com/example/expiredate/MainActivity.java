package com.example.expiredate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTextMessage;

    private Button Button_Camera;
    private ImageView imageView;
    private String imagePath;
    private BottomNavigationView bottomNavigationView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_basket:
                    mTextMessage.setText(R.string.title_basket);
                    return true;
                case R.id.navigation_calendar:
                    mTextMessage.setText(R.string.title_calendar);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Button_Camera = (Button)findViewById(R.id.camera);
        imageView = (ImageView)findViewById(R.id.view);
        Button_Camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                if(permissionCheck == PackageManager.PERMISSION_DENIED){
                    //권한 없음
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                }else{
                    //권한 있음
                    Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraApp, 10000);
                }

                /*
                // Camera Application이 있으면
                if (isExistCameraApplication()) {
                    // Camera Application을 실행한다.
                    Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(cameraApp, 10000);
                    // 찍은 사진을 보관할 파일 객체를 만들어서 보낸다.
                    File picture = savePictureFile();
                    if (picture != null) {
                        cameraApp.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture));
                        startActivityForResult(cameraApp, 10000);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "카메라 앱을 설치하세요.", Toast.LENGTH_SHORT).show();
                }
                */
            }
        });

        // 기본 nav_bar 상태
        findViewById(R.id.navigation_basket).setBackgroundColor(getResources().getColor(android.R.color.white));
        findViewById(R.id.navigation_calendar).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // 버튼 클릭시 사용되는 리스너
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                // 어떤 메뉴 아이템이 터치되었는지 확인
                switch (item.getItemId()){
                    case R.id.navigation_basket:
                        //message.setText("Basket");
                        findViewById(R.id.navigation_basket).setBackgroundColor(getResources().getColor(android.R.color.white));
                        findViewById(R.id.navigation_calendar).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        return true;
                    case R.id.navigation_calendar:
                        //message.setText("Calendar");
                        findViewById(R.id.navigation_calendar).setBackgroundColor(getResources().getColor(android.R.color.white));
                        findViewById(R.id.navigation_basket).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view){
        Intent intent = new Intent(this, AdditemActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //imageView.setImageURI(data.getData());
        //Bundle extras = data.getExtras();
        //imageView.setImageBitmap((Bitmap)extras.get("data"));

        // 사진찍기 버튼을 누른 후 잘찍고 돌아왔다면
        if (requestCode == 10000 && resultCode == RESULT_OK) {
            // 사진을 ImageView에 보여준다.
            /*
            BitmapFactory.Options factory = new BitmapFactory.Options();

            factory.inJustDecodeBounds = false;
            factory.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, factory);
            */
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    /** * Android에 Camera Application이 설치되어있는지 확인한다.
     *  * @return 카메라 앱이 있으면 true, 없으면 false */
    private boolean isExistCameraApplication(){
        // Android의 모든 Appication을 얻어온다.
        PackageManager packageManager = getPackageManager();

        //Camera Application
        Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // MediaStore.ACTION_IMAGE_CAPTURE을 처리할 수 있는 App 정보를 가져온다.
        List cameraApps = packageManager.queryIntentActivities( cameraApp, PackageManager.MATCH_DEFAULT_ONLY);

        return cameraApps.size() > 0;
    }

    /** * 카메라에서 찍은 사진을 외부 저장소에 저장한다. * *
     *  @return */
    private File savePictureFile(){
        PermissionRequester.Builder requester = new PermissionRequester.Builder(this);

        int result = requester.create().request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                20000, new PermissionRequester.OnClickDenyButtonListener() {
                    @Override
                    public void onClick(Activity activity) {

                    }
                });

        // 사용자가 권한을 수락한 경우
        if(result == PermissionRequester.ALREADY_GRANTED || result == PermissionRequester.REQUEST_PERMISSION){
            // 사진 파일의 이름을 만든다.
            // data는 java.util을 import한다.
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = "IMG_" + timestamp;

            /** * 사진파일이 저장될 장소를 구한다.
             * * 외장메모리에서 사진을 저장하는 폴더를 찾아서
             * * 그곳에 MYAPP 이라는 폴더를 만든다. */
            File pictureStorage = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MYAPP/"
            );

            //만약 장소가 존재하지 않는다면 폴더를 새롭게 만든다.
            if(!pictureStorage.exists()){
                pictureStorage.mkdirs();
            }

            try{
                File file = File.createTempFile(fileName, ".jpg", pictureStorage);

                //ImageView에 보여주기 위해 사진파일의 절대 경로를 얻어온다.
                imagePath = file.getAbsolutePath();

                //찍힌 사진을 "갤러리" 앱에 추가한다.
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                File f = new File(imagePath);
                Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                return file;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //사용자가 권한을 거부한 경우
        else{

        }

        return null;
    }

    /*
    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode==0){
            if(grantResults[0] == 0){
                Toast.makeText(this, "카메라 권한이 승인됨", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "카메라 권한이 거절됨", Toast.LENGTH_SHORT).show();
            }
        }
    }
    */
}
