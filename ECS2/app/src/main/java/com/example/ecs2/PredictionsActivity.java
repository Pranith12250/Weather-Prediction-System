package com.example.ecs2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;

public class PredictionsActivity extends AppCompatActivity
{
    private FirebaseFirestore db;
    private CollectionReference predictionsReference;
    private ListenerRegistration listenerRegistration;
    String names[]={"Bernoulli Naive Bayes","Decision Tree","Gaussian Naive Bayes","SVM Linear","SVM Polynomial"};
    ArrayList<Long> predictions=new ArrayList<>();
    RecyclerView recyclerView2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_predictions);
        getSupportActionBar().setTitle("Prediction values");

        db=FirebaseFirestore.getInstance();
        predictionsReference=db.collection("predictions");

        listenerRegistration=predictionsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Toast.makeText(PredictionsActivity.this, "Database data fetching error", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (DocumentChange documentChange : value.getDocumentChanges())
                {
                    if (documentChange.getType() == DocumentChange.Type.ADDED)
                    {
                        predictions.clear();
                        QueryDocumentSnapshot document = documentChange.getDocument();
                        predictions.add(document.getLong("Bernoulli Naive Bayes"));
                        predictions.add(document.getLong("Decision Tree"));
                        predictions.add(document.getLong("Gaussian Naive Bayes"));
                        predictions.add(document.getLong("SVM Linear"));
                        predictions.add(document.getLong("SVM Polynomial"));
                        recyclerView2 = findViewById(R.id.recyclerView2);
                        PredictionsActivity.MyAdapter2 myAdapter2 = new MyAdapter2(PredictionsActivity.this, predictions);
                        recyclerView2.setLayoutManager(new LinearLayoutManager(PredictionsActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView2.setAdapter(myAdapter2);
                    }
                }
            }
        });
    }

    class MyAdapter2 extends RecyclerView.Adapter<PredictionsActivity.MyAdapter2.Myholder2>
    {
        Context myContext;

        MyAdapter2(Context context,ArrayList<Long> predictions)
        {
            myContext = context;
        }

        @NonNull
        @Override
        public PredictionsActivity.MyAdapter2.Myholder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(myContext);
            View view = layoutInflater.inflate(R.layout.cardview, parent, false);
            return new PredictionsActivity.MyAdapter2.Myholder2(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PredictionsActivity.MyAdapter2.Myholder2 myholder2, int position)
        {
            myholder2.yesno.setText(Long.toString(predictions.get(position)));
            myholder2.name.setText(names[position]);
        }

        @Override
        public int getItemCount()
        {
            return predictions.size();
        }

        public class Myholder2 extends RecyclerView.ViewHolder
        {
            TextView name;
            TextView yesno;

            public Myholder2(@NonNull View itemView)
            {
                super(itemView);
                name = itemView.findViewById(R.id.modelname);
                yesno=itemView.findViewById(R.id.yesno);
            }
        }
    }
}