package com.kasunjay.miigras_app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kasunjay.miigras_app.data.model.ChatMessage;
import com.kasunjay.miigras_app.databinding.ChatRecentConversionContainerBinding;
import com.kasunjay.miigras_app.databinding.ItemContainerReceivedMessageBinding;

import java.util.List;

public class RecentConversionAdapter extends RecyclerView.Adapter<RecentConversionAdapter.ConversionHolder> {

    private final List<ChatMessage> chatMessages;

    public RecentConversionAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
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
        }
    }
}
