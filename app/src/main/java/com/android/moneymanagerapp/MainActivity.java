package com.android.moneymanagerapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
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

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private CardView budgetCardView,TodaysSpening,weekSpending,monthSpending,analyticsCardView,histroyCardView;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private DatabaseReference budgetRef, expensesRef, personalRef;
    private String onlineUserID = "";
    private TextView totalBudget,totalMonthSpend;
    private int totalAmountMonth = 0;
    private  int totalAmountBudgetB = 0;
    private  int totalAmountBudgetC = 0;

    private int totalAmountBudget = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);
        drawerLayout=findViewById(R.id.drawer);
        toolbar=findViewById(R.id.aToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Money Manager");
        budgetCardView=findViewById(R.id.budgetCardView);
        TodaysSpening=findViewById(R.id.todayCardView);
        weekSpending=findViewById(R.id.weekCardView);
        monthSpending=findViewById(R.id.monthCardView);
        analyticsCardView=findViewById(R.id.analyticsCardView);
        histroyCardView=findViewById(R.id.histroyCardView);
        totalBudget=findViewById(R.id.totIncomeAmt);
        totalMonthSpend=findViewById(R.id.totSpendMonth);

        onlineUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        budgetRef = FirebaseDatabase.getInstance().getReference("Entry").child(onlineUserID);
        expensesRef = FirebaseDatabase.getInstance().getReference("Expense").child(onlineUserID);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserID);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                Fragment fragment=null;
                switch (id)
                {
                    case R.id.home:
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.budget:
                        Intent intent1=new Intent(getApplicationContext(),BudgetActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.todaysEx:
                        Intent intent2=new Intent(getApplicationContext(),TodaySpendingActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.weeklyEx:
                        Intent intent3=new Intent(getApplicationContext(),WeekSpendingActivity.class);
                        intent3.putExtra("type","week");
                        startActivity(intent3);
                        break;
                    case R.id.monthlyEx:
                        Intent intent4=new Intent(getApplicationContext(),WeekSpendingActivity.class);
                        intent4.putExtra("type","month");
                        startActivity(intent4);
                        break;
                    case R.id.analytics:
                        Intent intent5=new Intent(getApplicationContext(),ChooseAnalyticActivity.class);
                        startActivity(intent5);
                        break;
                    case R.id.history:
                        Intent intent6=new Intent(getApplicationContext(),HistoryActivity.class);
                        startActivity(intent6);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });




        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds :  snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudgetB+=pTotal;
                    }
                    totalAmountBudgetC = totalAmountBudgetB;
                    personalRef.child("budget").setValue(totalAmountBudgetC);
                }else {
                    personalRef.child("budget").setValue(0);
                    Toast.makeText(MainActivity.this, "Please Set a BUDGET ", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,BudgetActivity.class);
                startActivity(intent);
            }
        });

        TodaysSpening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,TodaySpendingActivity.class);
                startActivity(intent);
            }
        });

        weekSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,WeekSpendingActivity.class);
                intent.putExtra("type","week");
                startActivity(intent);
            }
        });

        monthSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(MainActivity.this,WeekSpendingActivity.class);
                intent.putExtra("type","month");
                startActivity(intent);
            }
        });
        analyticsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChooseAnalyticActivity.class);
                startActivity(intent);
            }
        });
        histroyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
        getBudgetAmount();
        getMonthSpentAmount();

    }
    private void getBudgetAmount() {
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot ds :  snapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudget+=pTotal;
                        totalBudget.setText("\u20B9 "+String.valueOf(totalAmountBudget));
                    }
                }else {
                    totalAmountBudget=0;
                    totalBudget.setText("\u20B9 "+String.valueOf(0));


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthSpentAmount(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        DateTime now = new DateTime();
        Months months = Months.monthsBetween(epoch, now);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expense").child(onlineUserID);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds :  dataSnapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount+=pTotal;
                    totalMonthSpend.setText("\u20B9 "+ totalAmount);

                }
                personalRef.child("month").setValue(totalAmount);
                totalAmountMonth = totalAmount;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.account){
            Intent intent  = new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}