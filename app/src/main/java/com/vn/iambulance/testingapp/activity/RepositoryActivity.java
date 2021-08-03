package com.vn.iambulance.testingapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Bundle;
import android.view.View;
import com.vn.iambulance.testingapp.Constant;
import com.vn.iambulance.testingapp.R;
import com.vn.iambulance.testingapp.adapter.RepositoriesAdapter;
import com.vn.iambulance.testingapp.databinding.ActivityUserDetailBinding;
import com.vn.iambulance.testingapp.db.DBService;
import com.vn.iambulance.testingapp.model.UserModel;

public class RepositoryActivity extends AppCompatActivity {

    DBService mDBService = new DBService();
    RepositoriesAdapter mAdapter;
    ActivityUserDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Integer userId = (Integer) getIntent().getSerializableExtra(Constant.USER_ID_MESSAGE);
        UserModel userModel = mDBService.getById(userId, UserModel.class);
        initRecyclerView(userModel);
    }

    private void initRecyclerView(UserModel userModel) {
        if (userModel.getUserInfo().getRepositories().isEmpty()) {
            binding.userRepositoriesRecyclerview.setVisibility(View.GONE);
            binding.noRepositories.setText(getString(
                    R.string.user_does_not_have_repositories,
                    userModel.getUserInfo().getLogin())
            );
            binding.noRepositories.setVisibility(View.VISIBLE);
        } else {
            mAdapter = new RepositoriesAdapter(userModel.getUserInfo().getRepositories());
            binding.userRepositoriesRecyclerview.setLayoutManager(new
                    LinearLayoutManager(this));
            binding.userRepositoriesRecyclerview.setHasFixedSize(true);
            binding.userRepositoriesRecyclerview.setAdapter(mAdapter);
        }
    }
}