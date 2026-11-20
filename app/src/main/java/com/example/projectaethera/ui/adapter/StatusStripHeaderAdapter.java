package com.example.projectaethera.ui.adapter;



import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;



import androidx.annotation.NonNull;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;



import com.example.projectaethera.R;



/**

 * RecyclerView adapter with a single header row that hosts the horizontal status strip.

 * The inner RecyclerView uses StatusStripAdapter for story circles at the top of the home feed.

 */

public class StatusStripHeaderAdapter extends RecyclerView.Adapter<StatusStripHeaderAdapter.HeaderHolder> {



    private final StatusStripAdapter stripAdapter = new StatusStripAdapter();



    /**

     * Returns the inner adapter that drives the status circles; use it to set data and listeners.

     */

    public StatusStripAdapter getStripAdapter() {

        return stripAdapter;

    }



    /**

     * Inflates the home status header layout that wraps the inner horizontal list.

     */

    @NonNull

    @Override

    public HeaderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())

                .inflate(R.layout.home_status_header, parent, false);

        return new HeaderHolder(view);

    }



    /**

     * Attaches the status strip adapter to the inner RecyclerView on first bind.

     */

    @Override

    public void onBindViewHolder(@NonNull HeaderHolder holder, int position) {

        holder.bind(stripAdapter);

    }



    /**

     * This adapter always shows exactly one header row.

     */

    @Override

    public int getItemCount() {

        return 1;

    }



    /**

     * Holds the nested horizontal RecyclerView for the status strip.

     */

    static class HeaderHolder extends RecyclerView.ViewHolder {

        private final RecyclerView innerStrip;

        private boolean initialized;



        HeaderHolder(@NonNull View itemView) {

            super(itemView);

            innerStrip = itemView.findViewById(R.id.status_strip_inner);

        }



        /**

         * Sets up the inner list manager and adapter once, then reuses them on later binds.

         *

         * @param adapter the StatusStripAdapter to show inside the header

         */

        void bind(StatusStripAdapter adapter) {

            if (!initialized) {

                innerStrip.setLayoutManager(

                        new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false)

                );

                innerStrip.setAdapter(adapter);

                initialized = true;

            }

        }

    }

}

