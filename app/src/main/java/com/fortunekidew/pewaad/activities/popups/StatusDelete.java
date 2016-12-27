package com.fortunekidew.pewaad.activities.popups;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.presenters.StatusPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class StatusDelete extends Activity {

    @BindView(R.id.deleteStatus)
    TextView deleteStatus;

    private StatusPresenter mStatusPresenter = new StatusPresenter(this);
    private int statusID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_status_delete);
        ButterKnife.bind(this);
        if (getIntent().hasExtra("statusID") && getIntent().getExtras().getInt("statusID") != 0) {
            statusID = getIntent().getExtras().getInt("statusID");
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.deleteStatus)
    public void DeleteStatus() {
        mStatusPresenter.DeleteStatus(statusID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mStatusPresenter.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mStatusPresenter.onDestroy();
    }
}
