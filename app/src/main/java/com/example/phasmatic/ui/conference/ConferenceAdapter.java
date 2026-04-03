package com.example.phasmatic.ui.conference;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.phasmatic.R;
import com.example.phasmatic.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class ConferenceAdapter extends RecyclerView.Adapter<ConferenceAdapter.VH> {

    public interface OnUserClick {
        void onClick(User user);
    }

    private final List<User> list;
    private final OnUserClick listener;
    private final List<String> selectedUserIds = new ArrayList<>();


    public ConferenceAdapter(List<User> list, OnUserClick listener) {
        this.list = list;
        this.listener = listener;
    }

    public List<String> getSelectedUserIds() {
        return selectedUserIds;
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        User user = list.get(position);

        holder.txtName.setText(
                user != null && user.getFullName() != null
                        ? user.getFullName()
                        : "User"
        );

        assert user != null;
        if (selectedUserIds.contains(user.getId())) {
            holder.itemView.setAlpha(0.5f);
        } else {
            holder.itemView.setAlpha(1f);
        }
        if (user.isGray()) {
            holder.txtName.setTextColor(Color.GRAY);
        } else {
            holder.txtName.setTextColor(Color.BLACK);
        }

        holder.itemView.setOnClickListener(v -> {
            String uid = user.getId();

            if (selectedUserIds.contains(uid)) {
                selectedUserIds.remove(uid);
                holder.itemView.setAlpha(1f);
            } else {
                selectedUserIds.add(uid);
                holder.itemView.setAlpha(0.5f);
            }

            listener.onClick(user);
        });

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(holder.imgUser.getContext())
                    .load(user.getProfileImageUrl())
                    .placeholder(R.drawable.baseline_face_24)
                    .error(R.drawable.baseline_face_24)
                    .into(holder.imgUser);
        } else {
            holder.imgUser.setImageResource(R.drawable.baseline_face_24);
        }
    }
    public void clearGrayState() {
        for (User user : list) {
            user.setGray(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void markConfirmedUsers(List<String> confirmedIds) {
        for (User user : list) {
            if (confirmedIds.contains(user.getId())) {
                user.setGray(false);
            }
        }
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView txtName, txtLastMessage, txtTime;
        ImageView imgUser;

        VH(@NonNull View itemView) {
            super(itemView);
            txtName        = itemView.findViewById(R.id.txtName);
            txtLastMessage = itemView.findViewById(R.id.txtLastMessage);
            txtTime        = itemView.findViewById(R.id.txtTime);
            imgUser        = itemView.findViewById(R.id.imgUser);
        }
    }
}