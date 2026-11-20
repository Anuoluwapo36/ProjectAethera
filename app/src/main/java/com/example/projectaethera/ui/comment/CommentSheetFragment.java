package com.example.projectaethera.ui.comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.entity.Comment;
import com.example.projectaethera.ui.adapter.CommentsAdapter;
import com.example.projectaethera.viewmodel.CommentViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Bottom sheet for reading and writing comments on a post.
 * Opened from the home feed; uses {@link CommentViewModel} for list and submit.
 * Supports nested replies and notifies the host via {@link CommentCountListener}.
 */
public class CommentSheetFragment extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;
    private CommentViewModel commentViewModel;
    private int postId;
    private EditText enterComment;
    private TextView sendComment;
    private int replyingToId = -1;
    private String replyingToUsername = null;

    public interface CommentCountListener {
        void onCountUpdate(int totalCount);
    }

    private CommentCountListener countListener;

    public void setCountListener(CommentCountListener countListener) {
        this.countListener = countListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_feed_commet_fragment, container, false);
    }

    /**
     * Loads comments for {@code postId}, sets up reply mode, and wires send button / IME action.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        commentViewModel = new ViewModelProvider(this).get(CommentViewModel.class);
        recyclerView = view.findViewById(R.id.recycler_comments);
        enterComment = view.findViewById(R.id.input_comment);
        sendComment = view.findViewById(R.id.btn_post);
        int userId = AppConfig.getUserId(requireContext());

        if (getArguments() != null) {
            postId = getArguments().getInt("postId");
        }

        commentsAdapter = new CommentsAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(commentsAdapter);

        commentsAdapter.setOnReplyClickListener(comment -> {
            replyingToId = comment.getCommentId();
            replyingToUsername = comment.getUserProfile().getUsername();
            enterComment.setHint(getString(R.string.replying_to, replyingToUsername));
            enterComment.requestFocus();
        });

        if (postId != -1) {
            commentViewModel.getComments(postId);
            observerAddComment();
        }

        sendComment.setOnClickListener(v -> submitComment(userId));

        enterComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                submitComment(userId);
                return true;
            }
            return false;
        });
    }

    /** Sends the typed comment (or reply) through {@link CommentViewModel#addComment}. */
    private void submitComment(int userId) {
        String text = enterComment.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }
        Integer replyTo = replyingToId > 0 ? replyingToId : null;
        commentViewModel.addComment(postId, userId, text, replyTo);
        enterComment.setText("");
        clearReplyTarget();
    }

    private void clearReplyTarget() {
        replyingToId = -1;
        replyingToUsername = null;
        enterComment.setHint(R.string.add_a_comment);
    }

    /** Observes add/load results and flattens reply threads for the RecyclerView. */
    private void observerAddComment() {
        commentViewModel.getCommentSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                commentViewModel.getComments(postId);
            }
        });

        commentViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        commentViewModel.getCommentsList().observe(getViewLifecycleOwner(), response -> {
            if (response == null || !response.isSuccess()) {
                return;
            }

            List<Comment> original = response.getComments();
            List<Comment> finalList = sortRepliesOnly(original);
            commentsAdapter.setCommentList(finalList);

            if (countListener != null) {
                countListener.onCountUpdate(finalList.size());
            }
        });
    }

    /** Builds a top-level comment list with nested replies inserted under each parent. */
    private List<Comment> sortRepliesOnly(List<Comment> original) {
        List<Comment> result = new ArrayList<>();
        if (original == null) {
            return result;
        }

        for (Comment c : original) {
            if (c.getReply_to() != null && c.getReply_to() > 0) {
                continue;
            }
            result.add(c);
            addAllNestedReplies(c, original, result);
        }
        return result;
    }

    private void addAllNestedReplies(Comment parent, List<Comment> all, List<Comment> result) {
        for (Comment reply : all) {
            if (reply.getReply_to() != null && reply.getReply_to() == parent.getCommentId()) {
                result.add(reply);
                addAllNestedReplies(reply, all, result);
            }
        }
    }

    @Override
    public int getTheme() {
        return R.style.BottomSheetDialogTheme;
    }
}
