package com.example.ethan.myproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ethan.myproject.model.People;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class RecyclerCustomAdapter extends RecyclerView.Adapter<RecyclerCustomAdapter.ViewHolder> {
    private final ViewHolder.ItemClickListener clickListener;
    private List<People> peopleList;
    private LayoutInflater inflater;

    public RecyclerCustomAdapter(List<People> peopleList, ViewHolder.ItemClickListener clickListener) {
        this.peopleList = peopleList;
        this.clickListener = clickListener;
    }

    public void updateList(List<People> newList) {
        peopleList = new ArrayList<>();
        peopleList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerCustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        return ViewHolder.create(inflater, parent, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerCustomAdapter.ViewHolder holder, int position) {
        People p = peopleList.get(position);
        holder.bind(p);
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemClickListener clickListener;
        RelativeLayout backgroundLayout;
        private TextView firstName;
        private TextView lastName;
        private TextView currentId;
        private ImageView photo;
        private Context context;
        private People people;

        public ViewHolder(View itemView, ItemClickListener clickListener) {
            super(itemView);
            this.firstName = itemView.findViewById(R.id.first_name);
            this.lastName = itemView.findViewById(R.id.last_name);
            this.currentId = itemView.findViewById(R.id.current_id);
            this.photo = itemView.findViewById(R.id.photo);
            this.backgroundLayout = itemView.findViewById(R.id.background_layout);

            this.clickListener = clickListener;
            itemView.setOnClickListener(this);
            this.context = itemView.getContext();
        }

        public static ViewHolder create(LayoutInflater inflater, ViewGroup parent, ItemClickListener clickListener) {
            return new ViewHolder(inflater.inflate(R.layout.items, parent, false), clickListener);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onItemClick(people);
            }
        }

        public void bind(People p) {
            this.people = p;

            firstName.setText(p.getFirstName());
            lastName.setText(p.getLastName());
            currentId.setText(p.getId().toString());

            Picasso.with(context).load(p.getPhoto()).
                    fit().
                    error(R.drawable.ic_launcher_background).
                    into(photo);

            if (p.getGender().equalsIgnoreCase("male")) {
                backgroundLayout.setBackgroundColor(context.getResources().getColor(R.color.colorMale));
            } else if (p.getGender().equalsIgnoreCase("female")) {
                backgroundLayout.setBackgroundColor(context.getResources().getColor(R.color.colorFemale));
            }
        }

        interface ItemClickListener {
            void onItemClick(People people);
        }
    }
}
