package com.fortunekidew.pewaad.adapters.recyclerView;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.models.wishlists.MessagesModel;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abderrahim El imame on 11/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class MediaProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;
    private List<MessagesModel> mMessagesModel;
    private LayoutInflater mInflater;

    public MediaProfileAdapter(Activity mActivity) {
        this.mActivity = mActivity;
        mInflater = LayoutInflater.from(mActivity);
    }

    public void setMessages(List<MessagesModel> mMessagesList) {
        this.mMessagesModel = mMessagesList;
        notifyDataSetChanged();
    }


    public List<MessagesModel> getMessages() {
        return mMessagesModel;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_media_profile, parent, false);
        return new MediaProfileViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MediaProfileViewHolder mediaProfileViewHolder = (MediaProfileViewHolder) holder;
        final MessagesModel messagesModel = this.mMessagesModel.get(position);
        try {
            if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
                mediaProfileViewHolder.imageFile.setVisibility(View.VISIBLE);
                mediaProfileViewHolder.setImage(messagesModel.getImageFile(), String.valueOf(messagesModel.getId()), messagesModel.getUsername());
            } else {
                mediaProfileViewHolder.imageFile.setVisibility(View.GONE);
            }

            if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
                mediaProfileViewHolder.mediaAudio.setVisibility(View.VISIBLE);
            } else {
                mediaProfileViewHolder.mediaAudio.setVisibility(View.GONE);
            }
            if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
                mediaProfileViewHolder.mediaDocument.setVisibility(View.VISIBLE);
            } else {
                mediaProfileViewHolder.mediaDocument.setVisibility(View.GONE);
            }

            if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
                mediaProfileViewHolder.mediaVideo.setVisibility(View.VISIBLE);
                mediaProfileViewHolder.setMediaVideoThumbnail(messagesModel.getImageFile(), String.valueOf(messagesModel.getId()), messagesModel.getUsername());
            } else {
                mediaProfileViewHolder.mediaVideo.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            AppHelper.LogCat("" + e.getMessage());
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mMessagesModel != null) {
            return mMessagesModel.size();
        } else {
            return 0;
        }
    }

    public class MediaProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.media_image)
        ImageView imageFile;
        @BindView(R.id.media_audio)
        ImageView mediaAudio;
        @BindView(R.id.media_document)
        ImageView mediaDocument;
        @BindView(R.id.media_video_thumbnail)
        ImageView mediaVideoThumbnail;
        @BindView(R.id.media_video)
        FrameLayout mediaVideo;
        @BindView(R.id.play_btn_video)
        ImageButton playVideo;


        MediaProfileViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imageFile.setOnClickListener(this);
            mediaVideo.setOnClickListener(this);
            mediaAudio.setOnClickListener(this);
            mediaDocument.setOnClickListener(this);
            playVideo.setOnClickListener(this);

        }


        void setImage(String ImageUrl, String userId, String name) {
            if (FilesManager.isFileImagesOtherExists(FilesManager.getOthersSentImage(userId, name))) {
                Glide.with(mActivity)
                        .load(FilesManager.getFileImageOther(userId, name))
                        .override(100, 100)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(imageFile);
            } else {

                BitmapImageViewTarget target = new BitmapImageViewTarget(imageFile) {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        imageFile.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        imageFile.setImageBitmap(resource);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        imageFile.setImageDrawable(errorDrawable);
                    }


                };

                Glide.with(mActivity)
                        .load(EndPoints.BASE_URL + ImageUrl)
                        .asBitmap()
                        .override(100, 100)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(target);
            }

        }


        void setMediaVideoThumbnail(String ImageUrl, String userId, String name) {
            if (FilesManager.isFileImagesOtherExists(FilesManager.getOthersSentImage(userId, name))) {
                Glide.with(mActivity)
                        .load(FilesManager.getFileImageOther(userId, name))
                        .override(100, 100)
                        .centerCrop()
                        .into(mediaVideoThumbnail);
            } else {

                Glide.with(mActivity)
                        .load(EndPoints.BASE_URL + ImageUrl)
                        .override(100, 100)
                        .centerCrop()
                        .placeholder(mediaVideoThumbnail.getDrawable())
                        .into(mediaVideoThumbnail);
            }

        }


        @Override
        public void onClick(View view) {
            MessagesModel messagesModel = mMessagesModel.get(getAdapterPosition());
            switch (view.getId()) {
                case R.id.media_audio:
                    playingAudio(messagesModel);
                    break;

                case R.id.media_video:
                    playingVideo(messagesModel);
                    break;
                case R.id.play_btn_video:
                    playingVideo(messagesModel);
                    break;

                case R.id.media_document:
                    if (FilesManager.isFileDocumentsExists(FilesManager.getDocument(String.valueOf(messagesModel.getId()), messagesModel.getUsername()))) {
                        openDocument(FilesManager.getFileDocument(String.valueOf(messagesModel.getId()), messagesModel.getUsername()));
                    } else {
                        File file = new File(EndPoints.BASE_URL + messagesModel.getDocumentFile());
                        openDocument(file);
                    }
                    break;

                case R.id.media_image:
                    showImage(messagesModel);
                    break;
            }

        }

    }

    private void playingVideo(MessagesModel messagesModel) {
        String videoUrl = messagesModel.getVideoFile();
        String userId = String.valueOf(messagesModel.getId());
        String name = messagesModel.getUsername();
        if (FilesManager.isFileVideosExists(FilesManager.getVideo(String.valueOf(messagesModel.getId()), messagesModel.getUsername()))) {
            File fileVideo = FilesManager.getFileVideo(String.valueOf(messagesModel.getId()), messagesModel.getUsername());
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            Uri data = Uri.fromFile(fileVideo);
            intent.setDataAndType(data, "video/*");
            try {
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                AppHelper.CustomToast(mActivity, mActivity.getString(R.string.no_app_to_play_video));
            }

        } else {
            FilesManager.downloadFilesToDevice(mActivity, videoUrl, userId, name, "video");
        }
    }

    private void showImage(MessagesModel messagesModel) {
        String imageUrl = messagesModel.getImageFile();
        String userId = String.valueOf(messagesModel.getId());
        String name = messagesModel.getUsername();
        if (FilesManager.isFileImagesOtherExists(FilesManager.getOthersSentImage(String.valueOf(messagesModel.getId()), messagesModel.getUsername()))) {
            File fileImageOther = FilesManager.getFileImageOther(String.valueOf(messagesModel.getId()), messagesModel.getUsername());
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            Uri data = Uri.fromFile(fileImageOther);
            intent.setDataAndType(data, "image/*");
            try {
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                AppHelper.CustomToast(mActivity, mActivity.getString(R.string.no_application_to_show_image));
            }

        } else {
            FilesManager.downloadFilesToDevice(mActivity, imageUrl, userId, name, "other");
        }
    }

    private void playingAudio(MessagesModel messagesModel) {
        String audioFile = messagesModel.getAudioFile();
        String userId = String.valueOf(messagesModel.getId());
        String name = messagesModel.getUsername();
        if (FilesManager.isFileAudioExists(FilesManager.getSentAudio(String.valueOf(messagesModel.getId()), messagesModel.getUsername()))) {
            File fileAudio = FilesManager.getFileAudio(String.valueOf(messagesModel.getId()), messagesModel.getUsername());
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
            Uri data = Uri.fromFile(fileAudio);
            intent.setDataAndType(data, "audio/*");
            try {
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                AppHelper.CustomToast(mActivity, mActivity.getString(R.string.no_app_to_play_audio));
            }

        } else {
            FilesManager.downloadFilesToDevice(mActivity, audioFile, userId, name, "audio");
        }
    }

    private void openDocument(File file) {
        if (file.exists()) {
            Uri path = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(path, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try {
                mActivity.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                AppHelper.CustomToast(mActivity, mActivity.getString(R.string.no_application_to_view_pdf));
            }
        }
    }

}
