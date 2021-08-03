package com.vn.iambulance.testingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.vn.iambulance.testingapp.Constant;
import com.vn.iambulance.testingapp.R;
import com.vn.iambulance.testingapp.activity.RepositoryActivity;
import com.vn.iambulance.testingapp.model.UserModel;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersHolder> {

    Context mContext;
    List<UserModel> usersList;

    public UsersAdapter(Context context, List<UserModel> usersList) {
        this.mContext = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UsersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        holder.login.setText(usersList.get(position).getUserInfo().getLogin() + " Id: " + usersList.get(position).getId());
        if (usersList.get(position).getChangesCount() != 0) {
            holder.changesCount.setVisibility(View.VISIBLE);
            holder.changesCount.setText(String.valueOf(usersList.get(position).getChangesCount()));
        }else{
            holder.changesCount.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, RepositoryActivity.class);
            intent.putExtra(Constant.USER_ID_MESSAGE, usersList.get(position).getId());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setUsers(List<UserModel> userModels) {
        this.usersList = userModels;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    protected class UsersHolder extends RecyclerView.ViewHolder {
        TextView login, changesCount;
        public UsersHolder(@NonNull View itemView) {
            super(itemView);

            login = itemView.findViewById(R.id.user_login);
            changesCount = itemView.findViewById(R.id.user_changes_count);
        }
    }
}
