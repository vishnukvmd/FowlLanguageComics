package tech.vishnu.fowllaguagecomics.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.HashMap;
import java.util.Map;

public class ComicPagerAdapter extends FragmentStatePagerAdapter {
    private int count;
    private final Map<Integer, ComicFragment> fragmentCache = new HashMap<>();

    public ComicPagerAdapter(FragmentManager fm, int count) {
        super(fm);
        this.count = count;
    }

    @Override
    public ComicFragment getItem(int position) {
        if (fragmentCache.containsKey(position)) {
            return fragmentCache.get(position);
        } else {
            ComicFragment comicFragment = new ComicFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ComicFragment.POSITION, position);
            comicFragment.setArguments(bundle);
            fragmentCache.put(position, comicFragment);
            return comicFragment;
        }
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
