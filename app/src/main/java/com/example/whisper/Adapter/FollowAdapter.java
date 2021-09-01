package com.example.whisper.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.whisper.Fragments.FollowersFragment;
import com.example.whisper.Fragments.FollowingFragment;

//tablayout
public class FollowAdapter extends FragmentStateAdapter {

//    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
//    private final ArrayList<String> fragmentTitle = new ArrayList<>();
    public FollowAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle)
    {
        super(fragmentManager,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
//        return fragmentArrayList.get(position);
       switch (position){
           case 0:
               return new FollowingFragment();
           default:
               return new FollowersFragment();
       }
    }

    @Override
    public int getItemCount()
    {
//        return fragmentArrayList.size();
        return 2;
    }

//    public void addFragment(Fragment fragment,String title){
//        fragmentArrayList.add(fragment);
//        fragmentTitle.add(title);
//    }

//    getPageTitle


}
