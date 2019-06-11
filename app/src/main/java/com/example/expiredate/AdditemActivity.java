package com.example.expiredate;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AdditemActivity extends AppCompatActivity {

    private ImageView itemImageView;
    private EditText itemName;
    private TextView itemBuyDate;
    private Uri ItemImgUri;
    private Uri ItemExpiryDateUri;
    private String imagePath = "";
    private RelativeLayout expiryDateLayout;

    private String expiryDatePath;

    private EditText editYear;
    private EditText editMonth;
    private EditText editDate;

    String itemJSON;
    JSONArray items = null;

    private boolean isItemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        isItemImage = false;

        itemImageView = findViewById(R.id.item_img);
        itemName = findViewById(R.id.item_name);
        itemBuyDate = findViewById(R.id.item_buy_date);

        expiryDateLayout = findViewById(R.id.layout_expiry_date);
        expiryDateLayout.setVisibility(View.GONE);
        Button cameraBtn = findViewById(R.id.btn_expiry_date_camera);
        Button customBtn = findViewById(R.id.btn_expiry_date_custom);
        RelativeLayout addItemLayout = findViewById(R.id.btn_add_item);

        String timeStamp = new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date());
        itemBuyDate.setText(timeStamp);

        editYear = findViewById(R.id.edit_expire_date_year);
        editYear.setHint(new SimpleDateFormat("yyyy").format(new Date()));
        editMonth = findViewById(R.id.edit_expire_date_month);
        editMonth.setHint(new SimpleDateFormat("MM").format(new Date()));
        editDate = findViewById(R.id.edit_expire_date_date);
        editDate.setHint(new SimpleDateFormat("dd").format(new Date()));

        itemImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int permissionCheck = ContextCompat.checkSelfPermission(AdditemActivity.this, Manifest.permission.CAMERA);
                if(permissionCheck == PackageManager.PERMISSION_DENIED){
                    //권한 없음
                    ActivityCompat.requestPermissions(AdditemActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                }else{
                    //권한 있음
                    Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File photoFile = null;
                    try {
                        photoFile = createItemImageFile();
                    }catch (IOException ex){
                        // Error occurred while creating the File
                    }

                    if(photoFile != null){
                        isItemImage = true;

                        ItemImgUri = FileProvider.getUriForFile(AdditemActivity.this, getPackageName(), photoFile);
                        cameraApp.putExtra(MediaStore.EXTRA_OUTPUT, ItemImgUri);
                        startActivityForResult(cameraApp, 10000);
                    }
                }
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int permissionCheck = ContextCompat.checkSelfPermission(AdditemActivity.this, Manifest.permission.CAMERA);
                if(permissionCheck == PackageManager.PERMISSION_DENIED){
                    //권한 없음
                    ActivityCompat.requestPermissions(AdditemActivity.this, new String[]{Manifest.permission.CAMERA}, 0);
                }else {
                    //권한 있음
                    Intent cameraApp = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File photoFile = null;
                    try {
                        photoFile = createExpiryDateImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }

                    if (photoFile != null) {
                        ItemExpiryDateUri = FileProvider.getUriForFile(AdditemActivity.this, getPackageName(), photoFile);
                        cameraApp.putExtra(MediaStore.EXTRA_OUTPUT, ItemExpiryDateUri);
                        startActivityForResult(cameraApp, 20000);
                    }
                }

                expiryDateLayout.setVisibility(View.VISIBLE);
            }
        });

        customBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                expiryDateLayout.setVisibility(View.VISIBLE);
            }
        });

        // 등록하기 눌렀을 때
        addItemLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                uploadNewItem();

                finish();
            }
        });
    }

    private File createItemImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imagePath = image.getAbsolutePath();
        return image;
    }

    private File createExpiryDateImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        expiryDatePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        // 사진찍기 버튼을 누른 후 잘찍고 돌아왔다면
        if (requestCode == 10000 && resultCode == RESULT_OK) {
            // 사진을 ImageView에 보여준다.

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            ExifInterface exif = null;

            try{
                exif = new ExifInterface(imagePath);
            }catch(IOException e){
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if(exif != null){
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegress(exifOrientation);
            }else{
                exifDegree = 0;
            }

            //imageView.setImageURI(photoUri);
            itemImageView.setImageBitmap(rotate(bitmap, exifDegree));
        }
        else if(requestCode == 20000 && resultCode == RESULT_OK){
            // 유통기한 인식 사진 찍으면
            //getImageNameToUri(ItemExpiryDateUri);
            //new HttpRequestAsyncTask().execute(expiryDatePath, expiryDateTitle, expriyDateOrient);
            //uploadFile(expiryDatePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(expiryDatePath);
            ExifInterface exif = null;

            try{
                exif = new ExifInterface(expiryDatePath);
            }catch(IOException e){
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;

            if(exif != null){
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegress(exifOrientation);
            }else{
                exifDegree = 0;
            }

            uploadFileBitmap(rotate(bitmap, exifDegree));


        }
    }

    public void uploadFileBitmap(final Bitmap bitmap){
        Thread thread = new Thread(new Runnable(){
            HttpClient client = new DefaultHttpClient();

            public void run(){
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                byte [] ba = bao.toByteArray();
                String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("image", ba1));

                try{

                    HttpPost post = new HttpPost("http://192.168.35.86/ExpiryDate/Get_Images.php");
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = client.execute(post);

                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }catch (ClientProtocolException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }

                getData("http://192.168.35.86/ExpiryDate/Send_Expirydate.php");
            }
        });
        thread.start();
    }

    /*
    public void uploadFile(String filePath){

        String url = "http://192.168.35.86/serverProcess.php";
        try{
            UploadFile uploadFile = new UploadFile(AdditemActivity.this);
            uploadFile.setPath(filePath);
            uploadFile.execute(url);
        }catch (Exception e){

        }
    }

    public class UploadFile extends AsyncTask<String, String, String>{

        Context context; // 생성자 호출 시
        ProgressDialog mProgressDialog; // 진행 상태 다이얼로그
        String fileName; // 파일 위치

        HttpURLConnection conn = null; // 네트워크 연결 객체
        DataOutputStream dos = null; // 서버 전송 시 데이터 작성한 뒤 전송

        String lineEnd = "\r\n"; // 구분자
        String twoHyphens = "--";
        String boundary = "*****";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024;
        File sourceFile;
        int serverResponseCode;
        String TAG = "FileUpload";

        public UploadFile(Context context){
            this.context = context;
        }

        public void setPath(String uploadFilePath){
            this.fileName = uploadFilePath;
            this.sourceFile = new File(uploadFilePath);
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setTitle("Loading...");
            mProgressDialog.setMessage("Searching...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings){
            if(!sourceFile.isFile()){
                Log.e(TAG, "sourceFile(" + fileName + ") is Not a File");
                return null;
            }else{
                String success = "Success";
                try{

                    URL url = new URL(strings[0]);

                    //Open a HTTP connection to the URL
                    conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST"); // 전송 방식
                    //conn.setRequestProperty("Connection", "Keep-Alive");
                    //conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    // 사용자 이름으로 폴더를 생성하기 위해 사용자 이름을 서버로 전송한다.

                    dos.writeBytes( lineEnd + twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"data1\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes("ym");
                    dos.writeBytes(lineEnd);

                    // 이미지 전송, 데이터 전달 uploadded_file이라는 php key값에 저장되는 내용은 fileName
                    dos.writeBytes(lineEnd + twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + "image.jpg" + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: application/octet-stream" + lineEnd + lineEnd);
                    //dos.writeBytes(lineEnd);

                    FileInputStream fileInputStream = new FileInputStream(fileName);

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while(bytesRead > 0){
                        DataOutputStream dataWrite = new DataOutputStream(conn.getOutputStream());
                        dataWrite.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    fileInputStream.close();
                    // send multipart form data necessary after file data..., 마지막에 two~~ lineEdn로 마무리
                    //dos.writeBytes(lineEnd);
                    //dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    if(serverResponseCode == 200){
                        //EditText name = findViewById(R.id.item_name);
                        //name.setText(serverResponseMessage);
                    }

                    // 결과 확인
                    BufferedReader rd = null;
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line = null;
                    while((line = rd.readLine()) != null){
                        Log.i("Upload State", line);
                    }

                    // close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (IOException e){
                    Log.e(TAG + " Error", e.toString());
                }
                return success;
            }
        }

        @Override
        protected void onPostExecute(String aResult){
            super.onPostExecute(aResult);

            if(mProgressDialog != null){
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            if(aResult != null){

                editYear.setText("2020");
                editMonth.setText("05");
                editDate.setText("21");
            }
        }
    }
    */

    private int exifOrientationToDegress(int exifOrientation){
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }



    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params){
                String uri = params[0];

                BufferedReader bufferedReader = null;
                try{
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        sb.append(json + '\n');
                    }

                    return sb.toString().trim();
                }catch (Exception e){
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result){
                itemJSON = result;

                try{
                    JSONObject jsonObject = new JSONObject(result);
                    items = jsonObject.getJSONArray("result");

                    JSONObject c = items.getJSONObject(0);
                    String ex_year = c.getString("year");
                    String ex_month = c.getString("month");
                    String ex_date = c.getString("date");

                    editYear.setText(ex_year);
                    editMonth.setText(ex_month);
                    editDate.setText(ex_date);
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

    public void uploadNewItem(){

        Thread thread = new Thread(new Runnable(){
            HttpClient client = new DefaultHttpClient();

            public void run(){
                // name, buydate, expirydate 서버로 보내기
                try {
                    String name;
                    if(itemName.getText().toString() == ""){ // 입력된 이름이 없으면
                        // 이름의 유일성을 위해 이미지 이름에 timestamp 넣기
                        String pattern = "yyMMddHHmmss";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.KOREA);
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        String time = dateFormat.format(timestamp);
                        name = "식료품" + time;
                    }
                    else{
                        name = itemName.getText().toString();
                    }

                    // buydate는 서버에서 오늘 날짜로 처리

                    String getData = "Name=" + name + "&" + "ExpiryYear=" + editYear.getText().toString() + "&" + "ExpiryMonth=" + editMonth.getText().toString() + "&" + "ExpiryDate=" + editDate.getText().toString();
                    URL url = new URL("http://192.168.35.86/Item_Insert.php");
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestMethod("POST");
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    conn.setDefaultUseCaches(false);

                    //OutputStream outputStream = conn.getOutputStream();
                    OutputStreamWriter outputStream = new OutputStreamWriter(conn.getOutputStream());
                    outputStream.write(getData);
                    outputStream.flush();
                    outputStream.close();

                        /*
                        PrintWriter out = new PrintWriter(conn.getOutputStream());
                        out.println(getData);
                        out.close();
                        */
                    //String result = readStream(conn.getInputStream());

                    int resCode = conn.getResponseCode();
                    if(resCode == 200){
                        // 잘 동작해야하는데... 그게 맞는데..
                    }
                    else{
                        // 연결 안된 경우
                    }

                    conn.disconnect();
                    //return result;

                }
                catch (Exception e) {
                    Log.i("PHPRequest", "request was failed.");
                }

                if(imagePath == ""){ // 상품 이미지 사진 안찍었을 경우

                }
                else{ // 찍었을 경우 서버로 보냄
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ExifInterface exif = null;

                    try{
                        exif = new ExifInterface(imagePath);
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                    int exifOrientation;
                    int exifDegree;

                    if(exif != null){
                        exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        exifDegree = exifOrientationToDegress(exifOrientation);
                    }else{
                        exifDegree = 0;
                    }
                    itemImageView.setImageBitmap(rotate(bitmap, exifDegree));
                    Bitmap rotate_bitmap = rotate(bitmap, exifDegree);

                    ByteArrayOutputStream bao = new ByteArrayOutputStream();
                    rotate_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                    byte [] ba = bao.toByteArray();
                    String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("sumnail", ba1));

                    try{

                        HttpPost post = new HttpPost("http://192.168.35.86/sumnail/Get_sumnails.php");
                        post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = client.execute(post);

                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }catch (ClientProtocolException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }


            }
        });
        thread.start();
    }
}
