package com.example.expiredate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView mTextMessage;

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

        // Fragment 등록
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.fragmentBasket_Calendar, new BasketFragment());
        fragmentTransaction.commit();

        // 기본 nav_bar 상태
        findViewById(R.id.navigation_basket).setBackgroundColor(getResources().getColor(android.R.color.white));
        findViewById(R.id.navigation_calendar).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // 버튼 클릭시 사용되는 리스너
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item){
                // 어떤 메뉴 아이템이 터치되었는지 확인
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                switch (item.getItemId()){
                    case R.id.navigation_basket:
                        //message.setText("Basket");
                        findViewById(R.id.navigation_basket).setBackgroundColor(getResources().getColor(android.R.color.white));
                        findViewById(R.id.navigation_calendar).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        fragmentTransaction.replace(R.id.fragmentBasket_Calendar, new BasketFragment());
                        fragmentTransaction.commit();
                        return true;
                    case R.id.navigation_calendar:
                        //message.setText("Calendar");
                        findViewById(R.id.navigation_calendar).setBackgroundColor(getResources().getColor(android.R.color.white));
                        findViewById(R.id.navigation_basket).setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        fragmentTransaction.replace(R.id.fragmentBasket_Calendar, new CalendarFragment());
                        fragmentTransaction.commit();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view){
        Intent intent = new Intent(this, AdditemActivity.class);
        //startActivityForResult(intent, 0);
        startActivity(intent);
    }




}
