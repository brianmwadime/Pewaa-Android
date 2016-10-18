package com.fortunekidew.pewaa.activities.wishlists;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.Files.FilesManager;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.presenters.EditWishlistPresenter;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Brian Mwakima on 17/10/2016.
 * Email : mwadime@fortunekidew.co.ke
 */

@SuppressLint("SetTextI18n")
public class AddWishlistsActivity extends AppCompatActivity implements LoadingData {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.edit_wishlist_name)
    EditText EditName;

    @Bind(R.id.edit_wishlist_description)
    EditText EditDescription;

    private ActionMode actionMode;
    private String FileImagePath, FileSize;

    private EditWishlistPresenter mEditWishlistPresenter = new EditWishlistPresenter(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wishlist);
        ButterKnife.bind(this);
        mEditWishlistPresenter.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    /**
     * method to launch the camera preview
     */
    private void launchAttachCamera() {
        if (AppHelper.checkPermission(this, Manifest.permission.CAMERA)) {
            AppHelper.LogCat("camera permission already granted.");
        } else {
            AppHelper.LogCat("Please request camera  permission.");
            AppHelper.requestPermission(this, Manifest.permission.CAMERA);
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        startActivityForResult(cameraIntent, AppConstants.SELECT_MESSAGES_CAMERA);
    }

    /**
     * method to launch the image chooser
     */
    private void launchImageChooser() {

        if (AppHelper.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AppHelper.LogCat("Read data permission already granted.");
        } else {
            AppHelper.LogCat("Please request Read data permission.");
            AppHelper.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(intent, "Choose An image"),
                AppConstants.UPLOAD_PICTURE_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        File fileVideo = null;
        // Get file from file name
        File file = null;
        if (resultCode == RESULT_OK) {
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
            switch (requestCode) {
                case AppConstants.UPLOAD_PICTURE_REQUEST_CODE:
                    FileImagePath = FilesManager.getPath(PewaaApplication.getAppContext(), data.getData());

                    if (FileImagePath != null) {
                        file = new File(FileImagePath);
                    }
                    if (file != null) {
                        FileSize = String.valueOf(file.length());

                    }
                    sendMessage();
                    break;
                case AppConstants.SELECT_MESSAGES_CAMERA:
                    if (data.getData() != null) {
                        FileImagePath = FilesManager.getPath(PewaaApplication.getAppContext(), data.getData());
                        if (FileImagePath != null) {
                            file = new File(FileImagePath);
                        }
                        if (file != null) {
                            FileSize = String.valueOf(file.length());
                        }
                        sendMessage();
                    } else {
                        try {
                            String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore
                                    .Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images
                                    .ImageColumns.MIME_TYPE};
                            final Cursor cursor = PewaaApplication.getAppContext().getContentResolver()
                                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns
                                            .DATE_TAKEN + " DESC");

                            if (cursor != null && cursor.moveToFirst()) {
                                String imageLocation = cursor.getString(1);
                                cursor.close();
                                File imageFile = new File(imageLocation);
                                if (imageFile.exists()) {
                                    FileImagePath = imageFile.getPath();
                                    file = new File(FileImagePath);
                                    FileSize = String.valueOf(file.length());
                                    sendMessage();
                                }
                            }
                        } catch (Exception e) {
                            AppHelper.LogCat("error" + e);
                        }
                    }
                    break;
            }

        }
    }

    /**
     * method to send the new message
     */
    private void sendMessage() {

//        EventBus.getDefault().post(new Pusher("startConversation"));//for change viewpager current item to 0
//        String messageBody = escapeJavaString(messageWrapper.getText().toString().trim());
//        if (messageTransfer != null)
//            messageBody = messageTransfer;
//
//        if (FileImagePath == null && FileAudioPath == null && FileDocumentPath == null && FileVideoPath == null) {
//            if (messageBody.isEmpty()) return;
//        }
//        DateTime current = new DateTime();
//        String sendTime = String.valueOf(current);
//
//        if (isGroup) {
//            final JSONObject messageGroup = new JSONObject();
//            try {
//                messageGroup.put("messageBody", messageBody);
//                messageGroup.put("senderId", senderId);
//                try {
//
//                    messageGroup.put("senderName", "null");
//
//                    messageGroup.put("phone", mUsersModel.getPhone());
//                    if (mGroupsModel.getGroupImage() != null)
//                        messageGroup.put("GroupImage", mGroupsModel.getGroupImage());
//                    else
//                        messageGroup.put("GroupImage", "null");
//                    if (mGroupsModel.getGroupName() != null)
//                        messageGroup.put("GroupName", mGroupsModel.getGroupName());
//                    else
//                        messageGroup.put("GroupName", "null");
//                } catch (Exception e) {
//                    AppHelper.LogCat(e);
//                }
//
//                messageGroup.put("groupID", groupID);
//                messageGroup.put("date", sendTime);
//                messageGroup.put("isGroup", true);
//
//                if (FileImagePath != null)
//                    messageGroup.put("image", FileImagePath);
//                else
//                    messageGroup.put("image", "null");
//
//                if (FileVideoPath != null)
//                    messageGroup.put("video", FileVideoPath);
//                else
//                    messageGroup.put("video", "null");
//
//                if (FileVideoThumbnailPath != null)
//                    messageGroup.put("thumbnail", FileVideoThumbnailPath);
//                else
//                    messageGroup.put("thumbnail", "null");
//
//                if (FileAudioPath != null)
//                    messageGroup.put("audio", FileAudioPath);
//                else
//                    messageGroup.put("audio", "null");
//
//                if (FileDocumentPath != null)
//                    messageGroup.put("document", FileDocumentPath);
//                else
//                    messageGroup.put("document", "null");
//
//                if (!FileSize.equals("0"))
//                    messageGroup.put("fileSize", FileSize);
//                else
//                    messageGroup.put("fileSize", "0");
//
//                if (!Duration.equals("0"))
//                    messageGroup.put("duration", Duration);
//                else
//                    messageGroup.put("duration", "0");
//
//
//            } catch (JSONException e) {
//                AppHelper.LogCat("send group message " + e.getMessage());
//            }
//            unSentMessagesGroup(groupID);
//            new Handler().postDelayed(() -> runOnUiThread(() -> setStatusAsWaiting(messageGroup, true)), 100);
//            AppHelper.LogCat("send group message to");
//
//        } else {
//            final JSONObject message = new JSONObject();
//            try {
//                message.put("messageBody", messageBody);
//                message.put("recipientId", recipientId);
//                message.put("senderId", senderId);
//                try {
//
//                    message.put("senderName", "null");
//
//                    if (mUsersModel.getImage() != null)
//                        message.put("senderImage", mUsersModel.getImage());
//                    else
//                        message.put("senderImage", "null");
//                    message.put("phone", mUsersModel.getPhone());
//                } catch (Exception e) {
//                    AppHelper.LogCat("Sender name " + e.getMessage());
//                }
//
//
//                message.put("date", sendTime);
//                message.put("isGroup", false);
//                message.put("conversationId", ConversationID);
//                if (FileImagePath != null)
//                    message.put("image", FileImagePath);
//                else
//                    message.put("image", "null");
//
//                if (FileVideoPath != null)
//                    message.put("video", FileVideoPath);
//                else
//                    message.put("video", "null");
//
//                if (FileVideoThumbnailPath != null)
//                    message.put("thumbnail", FileVideoThumbnailPath);
//                else
//                    message.put("thumbnail", "null");
//
//                if (FileAudioPath != null)
//                    message.put("audio", FileAudioPath);
//                else
//                    message.put("audio", "null");
//
//
//                if (FileDocumentPath != null)
//                    message.put("document", FileDocumentPath);
//                else
//                    message.put("document", "null");
//
//
//                if (!FileSize.equals("0"))
//                    message.put("fileSize", FileSize);
//                else
//                    message.put("fileSize", "0");
//
//                if (!Duration.equals("0"))
//                    message.put("duration", Duration);
//                else
//                    message.put("duration", "0");
//
//            } catch (JSONException e) {
//                AppHelper.LogCat("send message " + e.getMessage());
//            }
//            unSentMessagesForARecipient(recipientId, false);
//            new Handler().postDelayed(() -> runOnUiThread(() -> setStatusAsWaiting(message, false)), 100);
//        }
//        messageWrapper.setText("");
//        messageTransfer = null;


    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        realm.close();
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat("Wishlists " + throwable.getMessage());
    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Pusher pusher) {
        int messageId = pusher.getMessageId();
        switch (pusher.getAction()) {
//            case "new_message":
//                MessagesModel messagesModel = pusher.getMessagesModel();
//                if (messagesModel.getSenderID() == recipientId && messagesModel.getRecipientID() == senderId) {
//                    addMessage(messagesModel);
//                    mMessagesPresenter.updateConversationStatus();
//                }
//
//
//                break;
//            case "new_message_group":
//                if (isGroup) {
//                    MessagesModel messagesModel1 = pusher.getMessagesModel();
//                    if (messagesModel1.getSenderID() != PreferenceManager.getID(this)) {
//                        addMessage(messagesModel1);
//                        mMessagesPresenter.updateConversationStatus();
//                    }
//                }
//                break;

        }
    }

    private int convertToDp(float value) {
        return (int) Math.ceil(1 * value);
    }

    @OnClick(R.id.action_save)
    public void saveWishlist(View view) {
        String newName = EditName.getText().toString().trim();
        String newDescription = EditDescription.getText().toString().trim();
        try {
            mEditWishlistPresenter.EditWishlist(newName, newDescription);
        } catch (Exception e) {
            AppHelper.LogCat("Edit  name  Exception " + e.getMessage());
        }
    }

    @OnClick(R.id.action_discard)
    public void onBackPress(View view) {
        onBackPressed();
    }
}

