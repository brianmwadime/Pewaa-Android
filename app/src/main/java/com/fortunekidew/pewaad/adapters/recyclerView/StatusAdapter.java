package com.fortunekidew.pewaad.adapters.recyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.rockerhieu.emojicon.EmojiconTextView;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.popups.StatusDelete;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.models.users.status.StatusModel;
import com.fortunekidew.pewaad.presenters.StatusPresenter;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fortunekidew.pewaad.helpers.UtilsString.unescapeJava;

/**
 * Created by Abderrahim El imame on 28/04/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class StatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected final Context mContext;
    private List<StatusModel> mStatusModel;
    private StatusPresenter statusPresenter;

    public void setStatus(List<StatusModel> statusModelList) {
        this.mStatusModel = statusModelList;
        notifyDataSetChanged();
    }


    public StatusAdapter(@NonNull Context mContext, List<StatusModel> mStatusModel, StatusPresenter statusPresenter) {
        this.mContext = mContext;
        this.mStatusModel = mStatusModel;
        this.statusPresenter = statusPresenter;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.row_status, parent, false);
        return new StatusViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final StatusViewHolder statusViewHolder = (StatusViewHolder) holder;
        final StatusModel statusModel = this.mStatusModel.get(position);
        try {


            if (statusModel.getStatus() != null) {

                statusViewHolder.setStatus(statusModel.getStatus());
            }


        } catch (Exception e) {
            AppHelper.LogCat("" + e.getMessage());
        }
        statusViewHolder.setOnLongClickListener(v -> {
            Intent mIntent = new Intent(mContext, StatusDelete.class);
            mIntent.putExtra("statusID", statusModel.getId());
            mContext.startActivity(mIntent);
            return true;
        });
        statusViewHolder.setOnClickListener(v -> statusPresenter.UpdateCurrentStatus(statusModel.getStatus(), statusModel.getId()));

    }


    @Override
    public int getItemCount() {
        if (mStatusModel != null) return mStatusModel.size();
        return 0;
    }

    class StatusViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.status)
        EmojiconTextView status;

        StatusViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setStatus(String Status) {
            String finalStatus = unescapeJava(Status);
            if (finalStatus.length() > 27)
                status.setText(finalStatus.substring(0, 27) + "... " + "");
            else
                status.setText(finalStatus);
        }

        void setStatusColorCurrent() {
            status.setTextColor(mContext.getResources().getColor(R.color.colorBlueLight));
        }

        void setStatusColor() {
            status.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
        }

        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);

        }

        void setOnLongClickListener(View.OnLongClickListener listener) {
            itemView.setOnLongClickListener(listener);

        }
    }


}