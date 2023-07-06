package com.example.ecs2;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    TextView actualValue;
    Button predictionsBut;
    Button sensor;

    private FirebaseFirestore db;
    private CollectionReference predictionsReference;
    private ListenerRegistration listenerRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Home Page");
        ArrayList<Long> predictions=new ArrayList<>();;
        actualValue=findViewById(R.id.actualValue);
        predictionsBut=findViewById(R.id.predictions);
        sensor=findViewById(R.id.sensor);

        db=FirebaseFirestore.getInstance();
        predictionsReference=db.collection("predictions");

        listenerRegistration=predictionsReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Toast.makeText(MainActivity.this, "Database data fetching error", Toast.LENGTH_SHORT).show();
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
                        float sum=0;
                        for(int x=0;x<predictions.size();x++)
                            sum+=predictions.get(x);
                        actualValue.setText((Integer.toString((int)((sum/5)*100))+"%"));
                    }
                }
            }
        });

        predictionsBut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,PredictionsActivity.class);
                startActivity(intent);
            }
        });

        sensor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent2=new Intent(MainActivity.this,SensorActivity.class);
                startActivity(intent2);
            }
        });
    }
}