package com.fortunekidew.pewaad.activities.status;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.presenters.StatusPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fortunekidew.pewaad.helpers.UtilsString.escapeJava;

/**
 * Created by Abderrahim El imame on 28/04/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class EditStatusActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    @BindView(R.id.cancelStatus)
    TextView cancelStatusBtn;
    @BindView(R.id.OkStatus)
    TextView OkStatusBtn;
    @BindView(R.id.StatusWrapper)
    EmojiconEditText StatusWrapper;
    @BindView(R.id.ParentLayoutStatusEdit)
    LinearLayout ParentLayoutStatusEdit;


    @BindView(R.id.emoticonBtn)
    ImageView emoticonBtn;
    @BindView(R.id.emojicons)
    FrameLayout emojiIconLayout;
    public boolean emoticonShown = false;

    private int statusID;
    private StatusPresenter statusPresenter = new StatusPresenter(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_status);
        ButterKnife.bind(this);
        if (getIntent().getExtras() != null) {
            String oldStatus = getIntent().getStringExtra("currentStatus");
            statusID = getIntent().getExtras().getInt("statusID");
            StatusWrapper.setText(oldStatus);
        }
        initializerView();

        StatusWrapper.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                String insertedStatus = escapeJava(StatusWrapper.getText().toString().trim());
                AppHelper.showDialog(this, getString(R.string.adding_new_status));
                statusPresenter.EditCurrentStatus(insertedStatus, statusID);
            }
            return false;
        });
        StatusWrapper.setOnClickListener(v1 -> {
            if (emoticonShown) {
                emoticonShown = false;
                emojiIconLayout.setVisibility(View.GONE);
            }
        });
        emoticonBtn.setOnClickListener(v -> {
            if (!emoticonShown) {
                emoticonShown = true;
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    final Animation animation = AnimationUtils.loadAnimation(this, R.anim.push_up_in);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            emojiIconLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    emojiIconLayout.startAnimation(animation);
                }
            }

        });
        setEmojIconFragment();
    }

    /**
     * method to setup  EmojIcon Fragment
     */
    private void setEmojIconFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(false))
                .commit();
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_status_activity_title);
        cancelStatusBtn.setOnClickListener(v -> finish());
        OkStatusBtn.setOnClickListener(v -> {
            String insertedStatus = escapeJava(StatusWrapper.getText().toString());
            AppHelper.showDialog(this,  getString(R.string.adding_new_status));
            statusPresenter.EditCurrentStatus(insertedStatus, statusID);
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(StatusWrapper, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(StatusWrapper);
    }
}
