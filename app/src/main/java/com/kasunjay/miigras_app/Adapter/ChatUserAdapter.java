package com.kasunjay.miigras_app.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasunjay.miigras_app.data.model.ChatUser;
import com.kasunjay.miigras_app.databinding.ChatUserContainerBinding;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.UserViewHolder> {

    private final List<ChatUser> chatUserList;

    public ChatUserAdapter(List<ChatUser> chatUserList) {
        this.chatUserList = chatUserList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatUserContainerBinding chatUserContainerBinding = ChatUserContainerBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UserViewHolder(chatUserContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(chatUserList.get(position));
    }

    @Override
    public int getItemCount() {
        return chatUserList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        ChatUserContainerBinding binding;
        TextView chatUserName, chatUserRole;
        public UserViewHolder(ChatUserContainerBinding chatUserContainerBinding) {
            super(chatUserContainerBinding.getRoot());
            binding = chatUserContainerBinding;
        }

        void setUserData(ChatUser chatUser) {
            binding.chatUserName.setText(chatUser.getName());
            binding.chatUserRole.setText(chatUser.getJobType());
        }
    }
}
