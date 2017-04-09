package com.fortunekidew.pewaad.adapters.recyclerView.contacts;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.animations.AnimationsUtil;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.UtilsPhone;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;

import static com.fortunekidew.pewaad.helpers.UtilsString.unescapeJava;

/**
 * Created by Brian Mwakima on 09/04/2017.
 * Email : mwadime@fortunekidew.co.ke
 */
public class SelectContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final Activity mActivity;
    private List<ContactsModel> mContactsModel;
    public List<String> mselectedContacts = new ArrayList<>();
    private String SearchQuery;
    private static final int BASIC_ITEM = 2;
    private int mPreviousPosition = 0;

    public SelectContactsAdapter(@NonNull Activity mActivity, List<ContactsModel> contacts) {
        this.mActivity = mActivity;
        this.mContactsModel = contacts;
    }


    public void setContacts(List<ContactsModel> contacts) {
        this.mselectedContacts.clear();
        this.mContactsModel = contacts;
        notifyDataSetChanged();
    }

    //Methods for search start
    public void setString(String SearchQuery) {
        this.SearchQuery = SearchQuery;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
            return BASIC_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_select_contacts, parent, false);
        return new ContactsViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof ContactsViewHolder) {
            final ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
            final ContactsModel contactsModel = this.mContactsModel.get(position);
            try {

                if(mselectedContacts.contains(this.mContactsModel.get(position).getId())) {
                    contactsViewHolder.contact_selected.setChecked(true);
                }

                contactsViewHolder.setUsername(contactsModel.getUsername(), contactsModel.getPhone());

                String Username;
                String name = UtilsPhone.getContactName(mActivity, contactsModel.getPhone());
                if (name != null) {
                    Username = name;
                } else {
                    Username = contactsModel.getPhone();
                }

                SpannableString recipientUsername = SpannableString.valueOf(Username);
                if (SearchQuery == null) {
                    contactsViewHolder.username.setText(recipientUsername, TextView.BufferType.NORMAL);
                } else {
                    int index = TextUtils.indexOf(Username.toLowerCase(), SearchQuery.toLowerCase());
                    if (index >= 0) {
                        recipientUsername.setSpan(new ForegroundColorSpan(AppHelper.getColor(mActivity, R.color.colorAccent)), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        recipientUsername.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    contactsViewHolder.username.setText(recipientUsername, TextView.BufferType.SPANNABLE);
                }
                if (contactsModel.getStatus() != null) {
                    String status = unescapeJava(contactsModel.getStatus());
                    if (status.length() > 18)
                        contactsViewHolder.setStatus(status.substring(0, 18) + "... " + "");

                    else
                        contactsViewHolder.setStatus(status);

                } else {
                    contactsViewHolder.setStatus(contactsModel.getPhone());
                }

            } catch (Exception e) {
                AppHelper.LogCat("Contacts adapters Exception " + e.getMessage());
            }


            if (position > mPreviousPosition) {
                AnimationsUtil.animateY(holder, true);
            } else {
                AnimationsUtil.animateY(holder, false);
            }
            mPreviousPosition = position;

        }

    }


    @Override
    public int getItemCount() {
        if (mContactsModel != null) return mContactsModel.size();
        return 0;
    }


    public class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.wishlist_name)
        TextView username;
        @BindView(R.id.status)
        EmojiconTextView status;
        @BindView(R.id.contact_selected)
        AppCompatCheckBox contact_selected;

        private SparseBooleanArray selectedItems = new SparseBooleanArray();

        ContactsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        void setUsername(String Username, String phone) {
            if (Username != null) {
                username.setText(Username);
            } else {
                String name = UtilsPhone.getContactName(mActivity, phone);
                if (name != null) {
                    username.setText(name);
                } else {
                    username.setText(phone);
                }
            }
        }

        void setStatus(String Status) {
            status.setText(Status);
        }

        @Override
        public void onClick(View view) {
            if (mselectedContacts.contains(mContactsModel.get(getAdapterPosition()).getId())) {
//                selectedItems.delete(getAdapterPosition());
                mselectedContacts.remove(mContactsModel.get(getAdapterPosition()).getId());
                contact_selected.setChecked(false);
            } else {

//                selectedItems.put(getAdapterPosition(), true);
                mselectedContacts.add(mContactsModel.get(getAdapterPosition()).getId());
                contact_selected.setChecked(true);
            }
        }

    }

}
