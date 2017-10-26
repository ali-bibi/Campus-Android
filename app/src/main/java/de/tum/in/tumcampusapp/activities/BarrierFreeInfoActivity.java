package de.tum.in.tumcampusapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.activities.generic.BaseActivity;


public class BarrierFreeInfoActivity extends BaseActivity {
    ListView listView;

    public BarrierFreeInfoActivity(){
        super(R.layout.activity_barrier_free_info);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listView = (ListView) findViewById(R.id.activity_barrier_free_list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    case 0:
                        intent = new Intent(BarrierFreeInfoActivity.this, BarrierFreeContactActivity.class);
                        break;
                    case 1:
                        intent = new Intent(BarrierFreeInfoActivity.this, BarrierFreeFacilitiesActivity.class);
                        break;
                    case 2:
                        intent = new Intent(BarrierFreeInfoActivity.this, BarrierFreeMoreInfoActivity.class);
                        break;
                    default:
                        intent = new Intent();
                }
                startActivity(intent);
            }
        });
    }

}