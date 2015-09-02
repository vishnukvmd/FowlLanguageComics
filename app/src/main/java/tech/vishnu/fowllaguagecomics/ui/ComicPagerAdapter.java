package tech.vishnu.fowllaguagecomics.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ComicPagerAdapter extends FragmentStatePagerAdapter {
    private int count;

    public ComicPagerAdapter(FragmentManager fm, int count) {
        super(fm);
        this.count = count;
    }

    @Override
    public Fragment getItem(int position) {
        ComicFragment comicFragment = new ComicFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ComicFragment.POSITION, position);
        comicFragment.setArguments(bundle);
        return comicFragment;
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
