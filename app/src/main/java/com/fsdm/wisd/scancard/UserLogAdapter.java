package com.fsdm.wisd.scancard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserLogAdapter extends RecyclerView.Adapter<UserLogAdapter.ClassHolder> {
    Context con;

    ArrayList<UserLog> usersLogArray;

    public UserLogAdapter(Context con, ArrayList<UserLog> _usersLogArray){
        this.usersLogArray=_usersLogArray;
        this.con=con;
    }
    @NonNull
    @Override
    public  ClassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(con);
        View v=inflater.inflate(R.layout.itemlog,parent,false);
        return new ClassHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassHolder holder, int position) {
        holder.username.setText(usersLogArray.get(position).getName());
        holder.userUid.setText(usersLogArray.get(position).getUid());
        holder.date.setText(usersLogArray.get(position).getDate());

    }




    @Override
    public int getItemCount() {
        return usersLogArray.size();
    }

    public static final class ClassHolder extends RecyclerView.ViewHolder {
        TextView username,date,userUid;

        public ClassHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.name);
            date=itemView.findViewById(R.id.date);
            userUid=itemView.findViewById(R.id.uid);


        }
    }

}
