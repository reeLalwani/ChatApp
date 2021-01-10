package com.example.chatapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Chat;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    private Context mcontext;
    private List<Chat> mChat;
    private String imageUrl;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context mcontext,List<Chat> mChat,String imageUrl){
        this.mcontext=mcontext;
        this.mChat=mChat;
        this.imageUrl=imageUrl;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view= LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view= LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat=mChat.get(position);
        holder.show_message.setText(chat.getMessage());

        if(imageUrl.equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Picasso.with(mcontext).load(imageUrl).into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    //inner class-view holder
    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public CircleImageView profile_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

}
