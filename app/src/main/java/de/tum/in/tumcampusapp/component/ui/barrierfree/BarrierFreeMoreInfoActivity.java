package de.tum.in.tumcampusapp.component.ui.barrierfree;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.app.TUMCabeClient;
import de.tum.in.tumcampusapp.component.other.generic.activity.ActivityForLoadingInBackground;
import de.tum.in.tumcampusapp.component.ui.barrierfree.model.BarrierfreeMoreInfo;
import de.tum.in.tumcampusapp.utils.Const;
import de.tum.in.tumcampusapp.utils.Utils;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class BarrierFreeMoreInfoActivity
        extends ActivityForLoadingInBackground<Void, List<BarrierfreeMoreInfo>>
        implements AdapterView.OnItemClickListener {

    private Toolbar toolbar;
    private StickyListHeadersListView listView;

    public List<BarrierfreeMoreInfo> infos;
    public BarrierfreeMoreInfoAdapter adapter;

    public BarrierFreeMoreInfoActivity() {
        super(R.layout.activity_barrier_free_list_info);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.activity_barrier_info_list_view);
        startLoading();
    }

    @Override
    protected void onLoadFinished(List<BarrierfreeMoreInfo> result) {
        showLoadingEnded();
        if (result == null || result.isEmpty()) {
            showErrorLayout();
            return;
        }

        infos = result;
        adapter = new BarrierfreeMoreInfoAdapter(this, infos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                // Free ad space
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                boolean canScrollUp = absListView.canScrollList(Const.SCROLL_DIRECTION_UP);
                toolbar.setSelected(canScrollUp);
            }
        });
    }

    @Override
    protected List<BarrierfreeMoreInfo> onLoadInBackground(Void... arg) {
        showLoadingStart();
        List<BarrierfreeMoreInfo> result = new ArrayList<>();
        try {
            result = TUMCabeClient.getInstance(this)
                                  .getMoreInfoList();
        } catch (IOException e) {
            Utils.log(e);
            return result;
        }
        return result;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = infos.get(position)
                          .getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

    }
}
