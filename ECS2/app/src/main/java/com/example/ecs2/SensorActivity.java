package com.example.ecs2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SensorActivity extends AppCompatActivity
{
    private FirebaseFirestore db;
    private CollectionReference predictionsReference;
    private ListenerRegistration listenerRegistration;
    ArrayList<Double> sensordata=new ArrayList<>();
    String names[]={"MinTemp","MaxTemp","Humidity9am","Humidity3pm","Pressure9am","Pressure3pm","Temp9am","Temp3pm"};
    RecyclerView recyclerView3;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        getSupportActionBar().setTitle("Sensor Data");
        db=FirebaseFirestore.getInstance();
        predictionsReference=db.collection("sensordata");

        listenerRegistration=predictionsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Toast.makeText(SensorActivity.this, "Database data fetching error", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges())
                {
                    if (documentChange.getType() == DocumentChange.Type.ADDED)
                    {
                        sensordata.clear();
                        QueryDocumentSnapshot document = documentChange.getDocument();
                        sensordata.add(document.getDouble("MinTemp"));
                        sensordata.add(document.getDouble("MaxTemp"));
                        sensordata.add(document.getDouble("Humidity9am"));
                        sensordata.add(document.getDouble("Humidity3pm"));
                        sensordata.add(document.getDouble("Pressure9am"));
                        sensordata.add(document.getDouble("Pressure3pm"));
                        sensordata.add(document.getDouble("Temp9am"));
                        sensordata.add(document.getDouble("Temp3pm"));
                        recyclerView3 = findViewById(R.id.recyclerView3);
                        SensorActivity.MyAdapter2 myAdapter2 = new MyAdapter2(SensorActivity.this);
                        recyclerView3.setLayoutManager(new LinearLayoutManager(SensorActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView3.setAdapter(myAdapter2);
                    }
                }
            }
        });
    }
    class MyAdapter2 extends RecyclerView.Adapter<SensorActivity.MyAdapter2.Myholder2>
    {
        Context myContext;

        MyAdapter2(Context context)
        {
            myContext = context;
        }

        @NonNull
        @Override
        public SensorActivity.MyAdapter2.Myholder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(myContext);
            View view = layoutInflater.inflate(R.layout.cardview2, parent, false);
            return new SensorActivity.MyAdapter2.Myholder2(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SensorActivity.MyAdapter2.Myholder2 myholder2, int position)
        {
            myholder2.value.setText(Double.toString(sensordata.get(position)));
            myholder2.name.setText(names[position]);
        }

        @Override
        public int getItemCount()
        {
            return sensordata.size();
        }

        public class Myholder2 extends RecyclerView.ViewHolder
        {
            TextView name;
            TextView value;

            public Myholder2(@NonNull View itemView)
            {
                super(itemView);
                name = itemView.findViewById(R.id.sensorname);
                value=itemView.findViewById(R.id.value);
            }
        }
    }
}