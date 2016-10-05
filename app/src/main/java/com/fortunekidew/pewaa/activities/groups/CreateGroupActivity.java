package com.fortunekidew.pewaa.activities.groups;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import io.github.rockerhieu.emojicon.EmojiconEditText;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.adapters.recyclerView.groups.CreateGroupMembersToGroupAdapter;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.Files.FilesManager;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.models.groups.GroupsModel;
import com.fortunekidew.pewaa.models.groups.MembersGroupModel;
import com.fortunekidew.pewaa.models.messages.WishlistsModel;
import com.fortunekidew.pewaa.models.messages.MessagesModel;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.models.users.contacts.ContactsModel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmList;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.fortunekidew.pewaa.helpers.UtilsString.escapeJavaString;

/**
 * Created by Abderrahim El imame on 20/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class CreateGroupActivity extends AppCompatActivity implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {


    @Bind(R.id.subject_wrapper)
    EmojiconEditText subjectWrapper;
    @Bind(R.id.group_image)
    ImageView groupImage;
    @Bind(R.id.add_image_group)
    ImageView addImageGroup;
    @Bind(R.id.fab)
    FloatingActionButton doneBtn;
    @Bind(R.id.emoticonBtn)
    ImageView emoticonBtn;
    @Bind(R.id.emojicons)
    FrameLayout emojiIconLayout;
    @Bind(R.id.ContactsList)
    RecyclerView ContactsList;
    @Bind(R.id.participantCounter)
    TextView participantCounter;
    @Bind(R.id.app_bar)
    Toolbar toolbar;

    private CreateGroupMembersToGroupAdapter mAddMembersToGroupListAdapter;
    private boolean emoticonShown = false;
    private String selectedImagePath = null;
    private Realm realm;

    private int lastGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();
        initializeView();
        setupToolbar();
        loadData();

        subjectWrapper.setOnClickListener(v1 -> {
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
     * method to load members form shared preference
     */
    private void loadData() {
        List<ContactsModel> contactsModels = new ArrayList<>();
        String id;
        for (int x = 0; x < PreferenceManager.getMembers(this).size(); x++) {
            id = PreferenceManager.getMembers(this).get(x).getUserId();
            ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", id).findFirst();
            contactsModels.add(contactsModel);
        }
        mAddMembersToGroupListAdapter.setContacts(contactsModels);

        String text = String.format(getString(R.string.participants) + " %s/%s ", mAddMembersToGroupListAdapter.getItemCount(), PreferenceManager.getContactSize(this));
        participantCounter.setText(text);
    }

    /**
     * method to setup the toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_add_members_to_group);
    }

    /**
     * method to setup the  EmojIcon Fragment
     */
    private void setEmojIconFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(false))
                .commit();
    }

    /**
     * method to initialize  the view
     */
    private void initializeView() {
        GridLayoutManager mLinearLayoutManager = new GridLayoutManager(PewaaApplication.getAppContext(), 4);
        ContactsList.setLayoutManager(mLinearLayoutManager);
        mAddMembersToGroupListAdapter = new CreateGroupMembersToGroupAdapter(this);
        ContactsList.setAdapter(mAddMembersToGroupListAdapter);
        doneBtn.setOnClickListener(v -> createGroupOffline());
        addImageGroup.setOnClickListener(v -> launchImageChooser());
        if (AppHelper.isAndroid5()) {
            Transition enterTrans = new Fade();
            getWindow().setEnterTransition(enterTrans);
            enterTrans.setDuration(300);
        }else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 16, 0);
            params.gravity =  Gravity.RIGHT | Gravity.BOTTOM;
            doneBtn.setLayoutParams(params);
        }


    }

    /**
     * method to select an image
     */
    private void launchImageChooser() {
        Intent mIntent = new Intent();
        mIntent.setType("image/*");
        mIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(mIntent, getString(R.string.select_picture)),
                AppConstants.UPLOAD_PICTURE_REQUEST_CODE);
    }

    /**
     * method to create group in offline mode
     */
    private void createGroupOffline() {
        String groupName = escapeJavaString(subjectWrapper.getText().toString().trim());
        if (groupName.length() <= 3) {
            subjectWrapper.setError(getString(R.string.name_is_too_short));
        } else {
            DateTime current = new DateTime();
             String createTime = String.valueOf(current);
            if (selectedImagePath != null) {
                realm.executeTransactionAsync(realm1 -> {
                    int lastGroupID = 1;
                    int lastID = 1;
                    try {
                        WishlistsModel groupsModel = realm1.where(WishlistsModel.class).findAll().last();
                        lastGroupID = groupsModel.getId();
                        lastGroupID++;

                        List<MessagesModel> messagesModel1 = realm1.where(MessagesModel.class).findAll();
                        lastID = messagesModel1.size();
                        lastID++;

                        AppHelper.LogCat("last message ID  CreateGroupActivity " + lastID);

                    } catch (Exception e) {
                        AppHelper.LogCat("last conversation  ID  conversation 0 Exception  CreateGroupActivity" + e.getMessage());
                        lastGroupID = 1;
                    }
                    RealmList<MembersGroupModel> membersGroupModelRealmList = new RealmList<>();
                    ContactsModel membersGroupModel1 = realm1.where(ContactsModel.class).equalTo("id", PreferenceManager.getID(this)).findFirst();
                    MembersGroupModel membersGroupModel = new MembersGroupModel();
                    String role = "admin";
                    membersGroupModel.setUserId(membersGroupModel1.getId());
                    membersGroupModel.setGroupID(lastGroupID);
                    membersGroupModel.setUsername(membersGroupModel1.getUsername());
                    membersGroupModel.setPhone(membersGroupModel1.getPhone());
                    membersGroupModel.setStatus(membersGroupModel1.getStatus());
                    membersGroupModel.setStatus_date(membersGroupModel1.getStatus_date());
                    membersGroupModel.setImage(membersGroupModel1.getImage());
                    membersGroupModel.setRole(role);
                    membersGroupModelRealmList.add(membersGroupModel);
                    PreferenceManager.addMember(this, membersGroupModel);
                    RealmList<MessagesModel> messagesModelRealmList = new RealmList<MessagesModel>();
                    MessagesModel messagesModel = new MessagesModel();
                    messagesModel.setId(lastID);
                    messagesModel.setDate(createTime);
                    messagesModel.setSenderID(PreferenceManager.getID(this));
                    messagesModel.setRecipientID("0");
                    messagesModel.setStatus(AppConstants.IS_SEEN);
                    messagesModel.setUsername(null);
                    messagesModel.setGroup(true);
                    messagesModel.setMessage("FK");
                    messagesModel.setGroupID(lastGroupID);
                    messagesModel.setConversationID(lastGroupID);
                    messagesModelRealmList.add(messagesModel);
                    GroupsModel groupsModel = new GroupsModel();
                    groupsModel.setId(lastGroupID);
                    groupsModel.setMembers(membersGroupModelRealmList);
                    groupsModel.setGroupImage(selectedImagePath);
                    groupsModel.setGroupName(groupName);
//                    groupsModel.setCreatorID(PreferenceManager.getID(this));
                    realm1.copyToRealmOrUpdate(groupsModel);
                    WishlistsModel wishlistsModel = new WishlistsModel();
                    wishlistsModel.setLastMessage("FK");
                    wishlistsModel.setLastMessageId(lastID);
//                    wishlistsModel.setCreatorID(PreferenceManager.getID(this));
                    wishlistsModel.setRecipientID("0");
                    wishlistsModel.setRecipientUsername(groupName);
                    wishlistsModel.setRecipientImage(selectedImagePath);
                    wishlistsModel.setGroupID(lastGroupID);
                    wishlistsModel.setMessageDate(createTime);
                    wishlistsModel.setId(lastGroupID);
                    wishlistsModel.setGroup(true);
                    wishlistsModel.setMessages(messagesModelRealmList);
                    wishlistsModel.setStatus(AppConstants.IS_SEEN);
                    wishlistsModel.setUnreadMessageCounter("0");
                    wishlistsModel.setCreatedOnline(false);
                    realm1.copyToRealmOrUpdate(wishlistsModel);
                    lastGroupId = lastGroupID;

                }, () -> {
                    EventBus.getDefault().post(new Pusher("createGroup"));
                    AppHelper.Snackbar(this, findViewById(R.id.create_group), getString(R.string.group_created_successfully), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                    new Handler().postDelayed(() -> {
                        EventBus.getDefault().post(new Pusher("createGroup"));
                        EventBus.getDefault().post(new Pusher("addMember", String.valueOf(lastGroupId)));
                        finish();
                        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                    }, 200);
                }, error -> {
                    AppHelper.LogCat("Realm Error create group offline CreateGroupActivity " + error.getMessage());
                    AppHelper.Snackbar(this, findViewById(R.id.create_group), getString(R.string.create_group_failed), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

                });

            } else {
                AppHelper.Snackbar(this, findViewById(R.id.create_group), getString(R.string.please_choose_an_avatar), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

            }
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppConstants.UPLOAD_PICTURE_REQUEST_CODE) {

                if (AppHelper.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AppHelper.LogCat("Read contact data permission already granted.");
                } else {
                    AppHelper.LogCat("Please request Read contact data permission.");
                    AppHelper.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                }


                if (AppHelper.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AppHelper.LogCat("Read contact data permission already granted.");
                } else {
                    AppHelper.LogCat("Please request Read contact data permission.");
                    AppHelper.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                selectedImagePath = FilesManager.getPath(this, data.getData());
                Glide.with(this)
                        .load(data.getData())
                        .asBitmap()
                        .transform(new CropCircleTransformation(this))
                        .into(groupImage);
                if (groupImage.getVisibility() != View.VISIBLE) {
                    groupImage.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(subjectWrapper, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(subjectWrapper);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mAddMembersToGroupListAdapter.getContacts().size() != 0) {
                PreferenceManager.clearMembers(this);
                mAddMembersToGroupListAdapter.getContacts().clear();

            }
            finish();
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mAddMembersToGroupListAdapter.getContacts().size() != 0) {
            PreferenceManager.clearMembers(this);
            mAddMembersToGroupListAdapter.getContacts().clear();

        }
        finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }
}