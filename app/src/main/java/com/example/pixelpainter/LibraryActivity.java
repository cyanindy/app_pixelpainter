package com.example.pixelpainter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class LibraryActivity extends AppCompatActivity {

    ImageButton imgbtn_user_unchk;
    ImageButton imgbtn_library_chk;
    ImageButton imgbtn_nft_unchk;
    ImageButton imgbtn_camera_unchk;
    ImageButton imgbtn_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        imgbtn_user_unchk=findViewById(R.id.imgbtn_user_unchk);
        imgbtn_library_chk=findViewById(R.id.imgbtn_library_chk);
        imgbtn_nft_unchk=findViewById(R.id.imgbtn_nft_unchk);
        imgbtn_camera_unchk=findViewById(R.id.imgbtn_camera_unchk);
        imgbtn_setting=findViewById(R.id.imgbtn_setting);

        // --------------- 화면전환 --------------------
        //내 도안 클릭
        imgbtn_user_unchk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LibraryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //라이브러리 클릭
        imgbtn_library_chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LibraryActivity.this, LibraryActivity.class);
                startActivity(intent);
            }
        });

        //NFT 클릭
        imgbtn_nft_unchk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LibraryActivity.this, NftActivity.class);
                startActivity(intent);
            }
        });

        //도안 만들기 클릭
        imgbtn_camera_unchk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LibraryActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        //환경설정 클릭
        imgbtn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LibraryActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        // --------------- 화면전환 --------------------
    }
}