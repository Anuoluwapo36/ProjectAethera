package com.example.projectaethera.ui.adapter;







import android.view.LayoutInflater;



import android.view.View;



import android.view.ViewGroup;



import android.widget.ImageView;







import androidx.annotation.NonNull;



import androidx.recyclerview.widget.RecyclerView;







import com.bumptech.glide.Glide;



import com.example.projectaethera.R;



import com.example.projectaethera.model.remote.StatusItem;



import com.example.projectaethera.utils.ImageUrlUtils;







import java.util.ArrayList;



import java.util.List;







/**

 * RecyclerView adapter for swiping through one user's status images inside a ViewPager.

 * Each page shows one status media item loaded from the server URL.

 */

public class StatusViewPagerAdapter extends RecyclerView.Adapter<StatusViewPagerAdapter.ViewHolder> {







    private List<StatusItem> items = new ArrayList<>();







    /**

     * Replaces all status items and refreshes the pager.

     *

     * @param items list of status slides for one user, or null for empty

     */

    public void setItems(List<StatusItem> items) {



        this.items = items != null ? items : new ArrayList<>();



        notifyDataSetChanged();



    }







    /**

     * Returns the status item at the given position, or null if out of range.

     *

     * @param position index in the status list

     */

    public StatusItem getItem(int position) {



        if (position < 0 || position >= items.size()) {



            return null;



        }



        return items.get(position);



    }







    /**

     * Returns the current number of status slides (safe to call anytime).

     */

    public int getItemCountSafe() {



        return items.size();



    }







    /**

     * Removes one status at the given position and returns a copy of the remaining list.

     *

     * @param position index of the status to remove

     * @return new list containing all items after removal

     */

    public List<StatusItem> removeItem(int position) {



        if (position < 0 || position >= items.size()) {



            return new ArrayList<>(items);



        }



        items.remove(position);



        notifyItemRemoved(position);



        return new ArrayList<>(items);



    }







    /**

     * Inflates the full-screen status page layout for one slide.

     */

    @NonNull



    @Override



    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(parent.getContext())



                .inflate(R.layout.status_view_page, parent, false);



        return new ViewHolder(view);



    }







    /**

     * Loads the status image at this position with Glide using the item's media URI.

     */

    @Override



    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        StatusItem item = items.get(position);



        Glide.with(holder.image.getContext())



                .load(ImageUrlUtils.toFullUrl(item.getMediaUri()))



                .into(holder.image);



    }







    /**

     * Returns how many status slides are in the pager.

     */

    @Override



    public int getItemCount() {



        return items.size();



    }







    /**

     * Holds the ImageView that displays one status slide.

     */

    static class ViewHolder extends RecyclerView.ViewHolder {



        ImageView image;







        ViewHolder(@NonNull View itemView) {



            super(itemView);



            image = itemView.findViewById(R.id.status_page_image);



        }



    }



}



