package com.example.projectaethera.ui.adapter;



import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.LinearLayout;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;



import com.bumptech.glide.Glide;

import com.example.projectaethera.R;

import com.example.projectaethera.config.AppConfig;

import com.example.projectaethera.model.entity.Comment;

import com.example.projectaethera.ui.widgets.ProfileCircleWidgets;

import com.example.projectaethera.utils.ImageUrlUtils;

import com.example.projectaethera.utils.TimeAgoUtils;



import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;



/**

 * RecyclerView adapter that shows comments and nested replies on a post.

 * Reply rows are indented based on how deep they are in the reply chain.

 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private List<Comment> commentList = new ArrayList<>();

    private final Map<Integer, Integer> replyDepthById = new HashMap<>();

    private OnReplyClickListener listener;



    /**

     * Called when the user taps Reply on a comment.

     */

    public interface OnReplyClickListener {

        void onReply(Comment comment);

    }



    /**

     * Sets the listener for the reply button on each comment row.

     *

     * @param listener callback with the comment being replied to

     */

    public void setOnReplyClickListener(OnReplyClickListener listener) {

        this.listener = listener;

    }



    /**

     * Replaces comments, recalculates reply depth for indentation, and refreshes the list.

     *

     * @param commentList flat list of comments and replies for one post

     */

    public void setCommentList(List<Comment> commentList) {

        this.commentList = commentList != null ? commentList : new ArrayList<>();

        replyDepthById.clear();

        for (Comment comment : this.commentList) {

            replyDepthById.put(comment.getCommentId(), getReplyDepth(comment));

        }

        notifyDataSetChanged();

    }



    private int getReplyDepth(Comment comment) {

        Integer replyTo = comment.getReply_to();

        if (replyTo == null || replyTo <= 0) {

            return 0;

        }

        for (Comment parent : commentList) {

            if (parent.getCommentId() == replyTo) {

                return getReplyDepth(parent) + 1;

            }

        }

        return 1;

    }



    /**

     * Inflates one comment row layout.

     */

    @NonNull

    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())

                .inflate(R.layout.home_feed_comment_item, viewGroup, false);

        return new ViewHolder(view);

    }



    /**

     * Binds username, text, time, profile photo, reply button, and left margin for nested replies.

     */

    @Override

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        Comment comment = commentList.get(i);

        viewHolder.username.setText(comment.getUserProfile().getUsername());

        viewHolder.commentText.setText(comment.getComment() != null ? comment.getComment() : "");

        viewHolder.timeAgo.setText(TimeAgoUtils.format(comment.getCreateAt()));



        String imageUrl = ImageUrlUtils.toFullUrl(comment.getUserProfile().getImage_uri());

        if (imageUrl != null) {

            Glide.with(viewHolder.itemView.getContext())

                    .load(imageUrl)

                    .placeholder(AppConfig.DEFAULT_PROFILE_IMAGE)

                    .error(AppConfig.DEFAULT_PROFILE_IMAGE)

                    .circleCrop()

                    .into(viewHolder.image);

        } else {

            viewHolder.image.setImageResource(AppConfig.DEFAULT_PROFILE_IMAGE);

        }



        viewHolder.replyComment.setOnClickListener(v -> {

            if (listener != null) {

                listener.onReply(comment);

            }

        });



        int depth = replyDepthById.getOrDefault(comment.getCommentId(), 0);

        int indent = depth * viewHolder.itemView.getResources()

                .getDimensionPixelSize(R.dimen.comment_reply_indent);

        ViewGroup.LayoutParams params = viewHolder.commentContent.getLayoutParams();

        if (params instanceof ViewGroup.MarginLayoutParams) {

            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;

            marginParams.setMarginStart(10 + indent);

            viewHolder.commentContent.setLayoutParams(marginParams);

        }

    }



    /**

     * Returns how many comments (including replies) are shown.

     */

    @Override

    public int getItemCount() {

        return commentList.size();

    }



    /**

     * Holds profile image, comment text, time, reply button, and content container for indentation.

     */

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView username, commentText, timeAgo, replyComment;

        ProfileCircleWidgets image;

        LinearLayout commentContent;



        ViewHolder(@NonNull View itemView) {

            super(itemView);

            username = itemView.findViewById(R.id.comment_username);

            commentText = itemView.findViewById(R.id.comment_text);

            timeAgo = itemView.findViewById(R.id.comment_time);

            replyComment = itemView.findViewById(R.id.btn_reply);

            image = itemView.findViewById(R.id.comment_profile_image);

            commentContent = itemView.findViewById(R.id.comment_content);

        }

    }

}

