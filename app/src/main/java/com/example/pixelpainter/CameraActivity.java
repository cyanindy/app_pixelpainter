package com.example.pixelpainter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    ImageButton imgbtn_user_unchk;
    ImageButton imgbtn_library_unchk;
    ImageButton imgbtn_nft_unchk;
    ImageButton imgbtn_camera_chk;
    ImageButton imgbtn_setting;

    Button btn_selectimage;
    ImageView iv_userimage;
    Button btn_sendtoserver;

    ImageView iv_testimage;
    TextView tv_testuserfilename;

    String imgName="userimage.png";

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMddhhmm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imgbtn_user_unchk=findViewById(R.id.imgbtn_user_unchk);
        imgbtn_library_unchk=findViewById(R.id.imgbtn_library_unchk);
        imgbtn_nft_unchk=findViewById(R.id.imgbtn_nft_unchk);
        imgbtn_camera_chk=findViewById(R.id.imgbtn_camera_chk);
        imgbtn_setting=findViewById(R.id.imgbtn_setting);

        btn_selectimage=findViewById(R.id.btnSelectimage);
        iv_userimage=findViewById(R.id.ivUserimage);
        btn_sendtoserver=findViewById(R.id.btnSendtoserver);

        iv_testimage=findViewById(R.id.ivTestimage);
        tv_testuserfilename=findViewById(R.id.tvTestuserfilename);

        // --------------- 화면전환 --------------------
        //내 도안 클릭
        imgbtn_user_unchk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //라이브러리 클릭
        imgbtn_library_unchk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraActivity.this, LibraryActivity.class);
                startActivity(intent);
            }
        });

        //NFT 클릭
        imgbtn_nft_unchk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraActivity.this, NftActivity.class);
                startActivity(intent);
            }
        });

        //도안 만들기 클릭
        imgbtn_camera_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        //환경설정 클릭
        imgbtn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CameraActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });


        //---------------- 사진 불러오기 클릭 ---------------
        btn_selectimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCropActivity();
            }
        });


        //---------------- 서버로 사진 전송 클릭 ---------------
        btn_sendtoserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String imgpath = getCacheDir() + "/" + imgName;   // 내부 저장소에 저장되어 있는 이미지 경로
                    Bitmap bm = BitmapFactory.decodeFile(imgpath);
                    iv_testimage.setImageBitmap(bm);   // 내부 저장소에 저장된 이미지를 이미지뷰에 셋

                    File cachefile = new File(imgpath);
                    String time = getTime();
                    String devicemodel = getDeviceModel();
                    String cachefilename= devicemodel + "_" + time + imgName;
                    tv_testuserfilename.setText(cachefilename);

                    uploadWithTransferUtilty(cachefilename, cachefile);

                    Toast.makeText(getApplicationContext(), "캐시파일 접근 성공", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "캐시파일 접근 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }//onCreate end

    //---------------- 사진 불러오기 ---------------
    private void startCropActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                ContentResolver resolver = getContentResolver();
                try {
                    InputStream instream = resolver.openInputStream(resultUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                    saveBitmapToJpeg(imgBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                iv_userimage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    //이미지뷰 사진 캐시에 저장
    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
            Toast.makeText(getApplicationContext(), "파일 불러오기 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
        }
    }

    //현재시간 가져오기
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    //모델명 가져오기
    public static String getDeviceModel() {
        return Build.MODEL;
    }


    //---------------- S3 업로드 ---------------
    public void uploadWithTransferUtilty(String fileName, File file) {

        AWSCredentials awsCredentials = new BasicAWSCredentials(
                "액세스 키 id", "비밀 액세스 키");    // IAM 생성하며 받은 것 입력
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build();
        TransferNetworkLossHandler.getInstance(this);

        TransferObserver uploadObserver = transferUtility.upload("버킷", fileName, file);    // (bucket api, file이름, file객체)

        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    // Handle a completed upload
                }
            }

            @Override
            public void onProgressChanged(int id, long current, long total) {
                int done = (int) (((double) current / total) * 100.0);
                Toast.makeText(getApplicationContext(), "S3 업로드 성공", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int id, Exception ex) {
                Toast.makeText(getApplicationContext(), "S3 업로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
