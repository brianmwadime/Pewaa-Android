package com.fortunekidew.pewaad.presenters;


import com.fortunekidew.pewaad.interfaces.Presenter;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class EditGroupPresenter implements Presenter {
//    private EditWishlistActivity view;
    private Realm realm;
//    private ContactsService mContactsService;
//    private APIService mApiService;
//
//
//    public EditGroupPresenter(EditGroupActivity editGroupActivity) {
//        this.view = editGroupActivity;
//        this.realm = Realm.getDefaultInstance();
//
//    }
//
    public EditGroupPresenter() {
        this.realm = Realm.getDefaultInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void
    onCreate() {
//        this.mApiService = APIService.with(view);
//        this.mContactsService = new ContactsService(this.realm, view, this.mApiService);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        realm.close();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }


//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        String imagePath = null;
//        if (resultCode == Activity.RESULT_OK) {
//            if (AppHelper.checkPermission(view, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                AppHelper.LogCat("Read contact data permission already granted.");
//            } else {
//                AppHelper.LogCat("Please request Read contact data permission.");
//                AppHelper.requestPermission(view, Manifest.permission.READ_EXTERNAL_STORAGE);
//            }
//
//
//            if (AppHelper.checkPermission(view, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                AppHelper.LogCat("Read contact data permission already granted.");
//            } else {
//                AppHelper.LogCat("Please request Read contact data permission.");
//                AppHelper.requestPermission(view, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            }
//            switch (requestCode) {
//                case AppConstants.SELECT_PROFILE_PICTURE:
//                    imagePath = FilesManager.getPath(PewaaApplication.getAppContext(), data.getData());
//                    break;
//                case AppConstants.SELECT_PROFILE_CAMERA:
//                    if (data.getData() != null) {
//                        imagePath = FilesManager.getPath(PewaaApplication.getAppContext(), data.getData());
//                    } else {
//                        try {
//                            String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore
//                                    .Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images
//                                    .ImageColumns.MIME_TYPE};
//                            final Cursor cursor = PewaaApplication.getAppContext().getContentResolver()
//                                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns
//                                            .DATE_TAKEN + " DESC");
//
//                            if (cursor != null && cursor.moveToFirst()) {
//                                String imageLocation = cursor.getString(1);
//                                cursor.close();
//                                File imageFile = new File(imageLocation);
//                                if (imageFile.exists()) {
//                                    imagePath = imageFile.getPath();
//                                }
//                            }
//                        } catch (Exception e) {
//                            AppHelper.LogCat("error" + e);
//                        }
//                    }
//                    break;
//            }
//        }
//
//        if (imagePath != null) {
//            EventBus.getDefault().post(new Pusher("PathGroup", imagePath));
//        } else {
//            AppHelper.LogCat("imagePath is null");
//        }
//    }
//
//
//    public void EditCurrentName(String name, int groupID) {
//        mContactsService.editGroupName(name, groupID).subscribe(statusResponse -> {
//            if (statusResponse.isSuccess()) {
//                realm.executeTransactionAsync(realm1 -> {
//                            GroupsModel groupsModel = realm1.where(GroupsModel.class).equalTo("id", groupID).findFirst();
//                            groupsModel.setGroupName(name);
//                            realm1.copyToRealmOrUpdate(groupsModel);
//                        }, () -> realm.executeTransactionAsync(realm1 -> {
//                            WishlistsModel wishlistsModel = realm1.where(WishlistsModel.class).equalTo("groupID", groupID).findFirst();
//                            wishlistsModel.setRecipientUsername(name);
//                            realm1.copyToRealmOrUpdate(wishlistsModel);
//                        }, () -> {
//                            AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
//                            EventBus.getDefault().post(new Pusher("updateGroupName", String.valueOf(groupID)));
//                            EventBus.getDefault().post(new Pusher("createGroup"));
//                            view.finish();
//                        }, error -> AppHelper.LogCat("error update group name in conversation model " + error.getMessage())),
//                        error -> AppHelper.LogCat("error update group name in group model  " + error.getMessage()));
//            } else {
//                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
//            }
//        }, AppHelper::LogCat);
//
//    }

}