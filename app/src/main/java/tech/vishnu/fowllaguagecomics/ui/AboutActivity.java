package tech.vishnu.fowllaguagecomics.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.OnClick;
import tech.vishnu.fowllaguagecomics.R;

public class AboutActivity extends AppCompatActivity {
    private static final String LOG_TAG = AboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }

    @OnClick(R.id.fwl_site)
    public void onFWLSiteClick() {
        openBrowser("http://www.fowllanguagecomics.com");
    }

    @OnClick(R.id.fwl_fb)
    public void onFWLFacebookClick() {
        openBrowser("https://www.facebook.com/FowlLanguageComics");
    }

    @OnClick(R.id.fwl_twitter)
    public void onFWLTwitterClick() {
        openBrowser("https://www.twitter.com/fowlcomics");
    }

    @OnClick(R.id.vishnu_site)
    public void onVishnuSiteClick() {
        openBrowser("http://www.vishnu.tech");
    }

    @OnClick(R.id.vishnu_github)
    public void onVishnuGithubClick() {
        openBrowser("https://www.github.com/v1shnu");
    }

    @OnClick(R.id.vishnu_xda)
    public void onXDAClick() {
        openBrowser("http://forum.xda-developers.com/member.php?u=4128904");
    }

    private void openBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
