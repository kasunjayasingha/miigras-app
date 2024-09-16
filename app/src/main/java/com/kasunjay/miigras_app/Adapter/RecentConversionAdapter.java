package com.kasunjay.miigras_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasunjay.miigras_app.data.model.ChatMessage;
import com.kasunjay.miigras_app.data.model.ChatUser;
import com.kasunjay.miigras_app.databinding.ChatRecentConversionContainerBinding;
import com.kasunjay.miigras_app.databinding.ItemContainerReceivedMessageBinding;
import com.kasunjay.miigras_app.listeners.ConversionalListener;

import java.util.List;

public class RecentConversionAdapter extends RecyclerView.Adapter<RecentConversionAdapter.ConversionHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversionalListener conversionalListener;

    public RecentConversionAdapter(List<ChatMessage> chatMessages, ConversionalListener conversionalListener) {
        this.chatMessages = chatMessages;
        this.conversionalListener = conversionalListener;
    }

    @NonNull
    @Override
    public ConversionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionHolder(
                ChatRecentConversionContainerBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionHolder holder, int position) {
        holder.setData(chatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionHolder extends RecyclerView.ViewHolder {

        ChatRecentConversionContainerBinding binding;

        public ConversionHolder(ChatRecentConversionContainerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textRecentMessage.setText(chatMessage.getMessage());
            binding.chatUserName.setText(chatMessage.getConversionName());
            binding.getRoot().setOnClickListener(v -> {
                ChatUser chatUser = new ChatUser();
                chatUser.setUserId(Long.parseLong(chatMessage.getConversionId()));
                chatUser.setName(chatMessage.getConversionName());

                conversionalListener.onConversionalClick(chatUser);
            });
        }
    }
}
