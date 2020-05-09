package com.project.space;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {
    ArrayList<CreateUser> nameList;
    Context c;
    MembersAdapter(ArrayList<CreateUser>nameList, Context c){
        this.nameList = nameList;
        this.c = c;
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    @NonNull
    @Override
    public MembersAdapter.MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout,parent,false);
        MembersViewHolder membersViewHolder = new MembersViewHolder(v, c,nameList);
        return membersViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdapter.MembersViewHolder holder, int position) {
        CreateUser currentUserObj = nameList.get(position);
        holder.name_txt.setText(currentUserObj.name);

        if(currentUserObj.isSharing.equals("false")){
            holder.status.setImageResource(R.drawable.notok);
        }else{
           holder.status.setImageResource(R.drawable.ok);
        }
    }


    public static class MembersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name_txt;
        View v;
        ImageView status;
        Context c;
        ArrayList<CreateUser> nameArrayList;
        FirebaseAuth mAuth;
        FirebaseUser user;

        public MembersViewHolder(@NonNull View itemView, Context c, ArrayList<CreateUser> nameArrayList) {
            super(itemView);
            this.c = c;
            this.nameArrayList = nameArrayList;

            itemView.setOnClickListener(this);
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            name_txt = itemView.findViewById(R.id.item_title);
            status = itemView.findViewById(R.id.status_img);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
