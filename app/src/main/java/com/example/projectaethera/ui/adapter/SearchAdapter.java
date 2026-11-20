package com.example.projectaethera.ui.adapter;



import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;



import com.bumptech.glide.Glide;

import com.example.projectaethera.R;

import com.example.projectaethera.config.AppConfig;

import com.example.projectaethera.model.entity.UserProfile;

import com.example.projectaethera.ui.widgets.ProfileCircleWidgets;



import com.example.projectaethera.utils.ImageUrlUtils;



import java.util.ArrayList;

import java.util.List;



/**

 * RecyclerView adapter that shows search results as a list of users.

 * Each row shows profile photo, username, full name, and opens the profile on tap.

 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {



    private List<UserProfile> userList = new ArrayList<>();

    private OnUserClickListener listener;



    /**

     * Called when the user taps a row in the search results list.

     */

    public interface OnUserClickListener {

        void onClick(UserProfile user);

    }



    /**

     * Sets the listener for when a search result user is tapped.

     *

     * @param listener callback with the tapped user profile

     */

    public void setOnUserClickListener(OnUserClickListener listener) {

        this.listener = listener;

    }



    /**

     * Replaces the search results and refreshes the list.

     *

     * @param userList users matching the search query, or null for empty

     */

    public void setUserList(List<UserProfile> userList) {

//        this.userList = userList;

        this.userList = (userList != null) ? userList : new ArrayList<>();



        notifyDataSetChanged();



    }





    /**

     * Inflates one search result row layout.

     */

    @NonNull

    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_items, viewGroup, false);

        return new ViewHolder(view);

    }



    /**

     * Binds username, full name, profile image, and click to open that user's profile.

     */

    @Override

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {



        UserProfile user = userList.get(i);



        viewHolder.username.setText(user.getUsername());



        viewHolder.fullname.setText(user.getFullname() != null ? user.getFullname() : "");





        String imageUrl = ImageUrlUtils.toFullUrl(user.getImage_uri());

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





        viewHolder.itemView.setOnClickListener(v -> {

            if (listener != null) {

                listener.onClick(user);

            }

        });





    }



    /**

     * Returns how many users are in the current search results.

     */

    @Override

    public int getItemCount() {

        return userList.size();

    }



    /**

     * Holds profile image and name labels for one search result row.

     */

    class ViewHolder extends RecyclerView.ViewHolder {



        ProfileCircleWidgets image;

        TextView username, fullname;



        /**

         * Finds views inside one search result item layout.

         */

        public ViewHolder(@NonNull View itemView) {

            super(itemView);



            image = itemView.findViewById(R.id.profile_image);

            username = itemView.findViewById(R.id.username_text);

            fullname = itemView.findViewById(R.id.fullname_text);



        }

    }

}

