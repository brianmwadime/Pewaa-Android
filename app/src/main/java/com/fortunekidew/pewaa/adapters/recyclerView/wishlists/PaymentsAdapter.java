package com.fortunekidew.pewaa.adapters.recyclerView.wishlists;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.models.payments.EditPayments;

import java.util.List;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.MyViewHolder> {

    private Context mContext;
    private List<EditPayments> paymentList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public PaymentsAdapter(Context mContext, List<EditPayments> paymentList) {
        this.mContext = mContext;
        this.paymentList = paymentList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        EditPayments payment = paymentList.get(position);
        holder.title.setText(payment.getName());
        holder.count.setText(payment.getNumOfSongs() + " songs");

        // loading payment cover using Glide library
        Glide.with(mContext).load(payment.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.menu_payment, popup.getMenu());
//        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
//        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
//            switch (menuItem.getItemId()) {
//                case R.id.action_add_favourite:
//                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
//                    return true;
//                case R.id.action_play_next:
//                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
//                    return true;
//                default:
//            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }
}
