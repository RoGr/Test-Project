package com.example.ethan.myproject;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.example.ethan.myproject.model.People;
import com.example.ethan.myproject.network.GetDataService;
import com.example.ethan.myproject.network.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    public int NUM_ITEMS_PAGE = 10;//Количество элементов при пагинации
    protected List<People> peopleListAdapter = new ArrayList<>();
    boolean isScrolling;
    int currentItems, totalItems, scrollOutItems;
    int paginationCounter = 0;//Уже показанные элементы массива
    private RecyclerCustomAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private String textSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startDownload();
    }

    private void startDownload() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Идёт загрузка данных....");
        progressDialog.show();

        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        retrofit2.Call<List<People>> call = service.getAllPeople();

        call.enqueue(new Callback<List<People>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<People>> call, @NonNull Response<List<People>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful())
                    setRecyclerViewData(response.body());
                else {
                    Toast.makeText(MainActivity.this, "Error " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<List<People>> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Возникла ошибка", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerViewData(final List<People> people) {
        recyclerView = findViewById(R.id.list);

        peopleListAdapter = new ArrayList<>();

        paginationCounter += NUM_ITEMS_PAGE;
        for (int i = paginationCounter - NUM_ITEMS_PAGE; i < paginationCounter; i++) {
            if (i < people.size() && people.get(i) != null)
                peopleListAdapter.add(people.get(i));
        }

        adapter = new RecyclerCustomAdapter(peopleListAdapter, new RecyclerCustomAdapter.ViewHolder.ItemClickListener() {
            @Override
            public void onItemClick(People people) {
                FragmentManager fm = getFragmentManager();
                FullInfoDialogFragment.newInstance(people).show(fm, "fullInfoDialogFragment");
            }
        });
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        //пролема медленного скролинга
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(40);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //Пагинация списка
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //Юзер продолжает скролить
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = layoutManager.getChildCount();
                totalItems = layoutManager.getItemCount();
                scrollOutItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    fetchData();
                }
            }

            private void fetchData() {
                //имитация подгузки данных
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        paginationCounter += NUM_ITEMS_PAGE;

                        for (int i = paginationCounter - NUM_ITEMS_PAGE; i < paginationCounter; i++) {
                            if (i >= people.size())
                                return;

                            if (i < people.size() && people.get(i) != null) {
                                peopleListAdapter.add(people.get(i));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, 900);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String search) {
        textSearch = search;
        List<People> newList = new ArrayList<>();

        for (People people : peopleListAdapter) {

            if (people.getFirstName().toLowerCase().contains(textSearch.toLowerCase())) {
                newList.add(people);
            }
        }
        adapter.updateList(newList);
        adapter.notifyDataSetChanged();
        isScrolling = TextUtils.isEmpty(textSearch);

        return true;
    }

    @Override
    public boolean onClose() {
        isScrolling = TextUtils.isEmpty(textSearch);
        return true;
    }
}
