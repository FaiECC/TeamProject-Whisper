package com.example.whisper;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.whisper.Adapter.FollowAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class Follow extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 pager2;
    FollowAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Follow");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_pager);

        FragmentManager fm = getSupportFragmentManager();

        adapter = new FollowAdapter(fm,getLifecycle());
        pager2.setAdapter(adapter);


        tabLayout.addTab(tabLayout.newTab().setText("Following"));
        tabLayout.addTab(tabLayout.newTab().setText("Followers"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

//        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
//            @Override
//            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                tab.setText("Tab" + (position + 1));
//            }
//        }).attach();

        /*
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("フォロー");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        //Tab Layout and viewpager
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new Followers(),"followers");
        viewPagerAdapter.addFragment(new Following(),"following");


        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

         */
    }



    //Class ViewPagerAdapter
//    public static class ViewPagerAdapter extends FragmentPagerAdapter {
//        private final ArrayList<Fragment> fragments;
//        private final ArrayList<String> titles;
//
//        ViewPagerAdapter(FragmentManager fm){
//            super(fm);
//            this.fragments = new ArrayList<>();
//            this.titles = new ArrayList<>();
//        }
//
//        @NonNull
//        @Override
//        public Fragment getItem(int position) {
//            return fragments.get(position);
//        }
//
//
//        @Override
//        public int getCount() {
//            return fragments.size();
//        }
//
//        public  void addFragment(Fragment fragment,String title){
//            fragments.add(fragment);
//            titles.add(title);
//        }
//
//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return titles.get(position);
//        }
//
//   }

}
