package com.android.moneymanagerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TodaySpendingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView totalAmountSpent;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private String onLineUserId="";

    private TodayItemAdapter todayItemAdapter;
    private List<Data>myDataList;

    private DatabaseReference expenseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_today_spending);

        toolbar=findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Todays Expenses");
        totalAmountSpent=findViewById(R.id.tsTotalAmountSpend);
        progressBar=findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.recyclerview);
        fab=findViewById(R.id.bFab);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        onLineUserId=mAuth.getCurrentUser().getUid();
        expenseRef = FirebaseDatabase.getInstance().getReference().child("Expense").child(onLineUserId);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemSpentOn();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        readItems();
        myDataList=new ArrayList<>();
        todayItemAdapter=new TodayItemAdapter(TodaySpendingActivity.this,myDataList);
        recyclerView.setAdapter(todayItemAdapter);

    }

    private void readItems() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expense").child(onLineUserId);
        Query query=reference.orderByChild("date").endAt(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                todayItemAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totAmount= 0;
                for (DataSnapshot ds:snapshot.getChildren()){
                    Map<String,Object> map=(Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totAmount+=pTotal;
                    totalAmountSpent.setText("Today's Expense \u20B9 "+totAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addItemSpentOn() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_laout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemspinner = myView.findViewById(R.id.spinner);
        final EditText amount = myView.findViewById(R.id.amount);

        final EditText note = myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);
        note.setVisibility(View.VISIBLE);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Amount = amount.getText().toString();
                String Item = itemspinner.getSelectedItem().toString();
                String comment = note.getText().toString();
                if (TextUtils.isEmpty(Amount)) {
                    amount.setError("Amount is required!");
                    return;
                }
//                if(amount.toString().length()>=1){
//                    note.setVisibility(View.VISIBLE);
//                    return;
//                }
                if(TextUtils.isEmpty(comment)){
                    note.setError("Comment is required!");
                }

                if (Item.equals("Select Category")) {
                    Toast.makeText(TodaySpendingActivity.this, "Select Category", Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog.setMessage("Adding Expense...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                readItems();
                String id = expenseRef.push().getKey();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Months months = Months.monthsBetween(epoch, now);
                String itemNday = Item+date;
                String itemNweek = Item+weeks.getWeeks();
                String itemNmonth = Item+months.getMonths();


                Data data = new Data(Item, date, id, itemNday,itemNweek,itemNmonth, Integer.parseInt(Amount),weeks.getWeeks(),months.getMonths(),comment);
                //Data data = new Data(Item, date, id, comment, Integer.parseInt(Amount), months.getMonths(),weeks.getWeeks());
                expenseRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(TodaySpendingActivity.this, "Expense Added SuccessFully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TodaySpendingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                        progressDialog.dismiss();
                    }
                });
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(TodaySpendingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}