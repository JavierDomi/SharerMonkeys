package com.dam.sharermonkeys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dam.sharermonkeys.adapterutils.FairShareListAdapter;
import com.dam.sharermonkeys.pojos.FairShare;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FairShareListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference reference;
    FairShareListAdapter adapter;
    ArrayList<FairShare> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fairshare_list);

        reference = FirebaseDatabase.getInstance().getReference("FairShares");

        recyclerView = findViewById(R.id.rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        adapter = new FairShareListAdapter(list, this);

        recyclerView.setAdapter(adapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    FairShare fairShare = dataSnapshot.getValue(FairShare.class);
                    list.add(fairShare);

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(FairShareListActivity.this, R.string.fairShare_list_cancelled, Toast.LENGTH_SHORT).show();

            }
        });


    }
}