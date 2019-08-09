package com.nakul.wikiapi.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nakul.wikiapi.R;
import com.nakul.wikiapi.databinding.ActivityMainBinding;
import com.nakul.wikiapi.databinding.ResultRowLayoutBinding;
import com.nakul.wikiapi.model.NetworkResponseModel;
import com.nakul.wikiapi.model.SearchResultModel;
import com.nakul.wikiapi.network.VolleyHelper;
import com.nakul.wikiapi.viewmodel.ResponseViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ResponseViewModel responseViewModel;
    private List<SearchResultModel> resultModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VolleyHelper.initVolley(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        responseViewModel = ViewModelProviders.of(this).get(ResponseViewModel.class);
        init();
    }

    private void init() {
        resultModelList = new ArrayList<>();
        final ResultAdapter adapter = new ResultAdapter();
        binding.resultList.setAdapter(adapter);

        responseViewModel.getSearchObservable().observe(this, new Observer<NetworkResponseModel>() {
            @Override
            public void onChanged(NetworkResponseModel genericResponseModel) {
                if (genericResponseModel.getResponseCode() == 200 && genericResponseModel.getErrorResponseStr().isEmpty()) {
                    resultModelList.clear();
                    resultModelList.addAll(genericResponseModel.getResponseModelList());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "error code : " + genericResponseModel.getResponseCode(), Toast.LENGTH_LONG).show();
                }
            }
        });


        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        binding.resultList.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());

                if (childView != null && mGestureDetector.onTouchEvent(e)) {
                    loadResultPage(rv.getChildAdapterPosition(childView));
                }
                return false;
            }
        });
    }

    private void loadResultPage(int position) {
        Intent pageIntent = new Intent(this, SelectedResultViewActivity.class);
        pageIntent.putExtra("pageid", resultModelList.get(position).getPageid());
        startActivity(pageIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.mi_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s != null && s.length() > 0) {
                    startSearch(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s != null && s.length() > 2) {
                    startSearch(s);
                }
                return false;
            }
        });
        searchView.onActionViewExpanded();
        return true;
    }

    private void startSearch(String query) {
        responseViewModel.getSearchResult(query);
    }


    private class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

        @NonNull
        @Override
        public ResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ResultRowLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.result_row_layout, parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ResultAdapter.ViewHolder holder, int position) {
            holder.binding.setModel(resultModelList.get(holder.getAdapterPosition()));
        }

        @Override
        public int getItemCount() {
            return resultModelList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ResultRowLayoutBinding binding;

            ViewHolder(@NonNull ResultRowLayoutBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

}
