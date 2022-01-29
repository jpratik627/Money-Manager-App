package com.android.moneymanagerapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.util.Calendar;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    private TextView totBudgetAmount;
    private FirebaseAuth mAuth;
    private DatabaseReference entryreference, personalRef;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    TextView midItem, midAmount, midComment, midDate;
    private String post_key = "";
    private String item = "";
    private String comment = "";
    private int amount = 0;
    private Toolbar settingsToolbar;
    Dialog mDialog;
    ImageView itemImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_budget);
        settingsToolbar = findViewById(R.id.aToolbar);
        setSupportActionBar(settingsToolbar);
        recyclerView = findViewById(R.id.recyclerview);
        totBudgetAmount = findViewById(R.id.tvBudgetAmount);
        mAuth = FirebaseAuth.getInstance();
        entryreference = FirebaseDatabase.getInstance().getReference().child("Entry").child(mAuth.getCurrentUser().getUid());
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(mAuth.getCurrentUser().getUid());

        progressDialog = new ProgressDialog(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        entryreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totAmount = 0;
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Data data = snap.getValue(Data.class);
                        if (data != null) {
                            totAmount += data.getAmount();
                        }
                        String strTotal = "Month Budget: \u20B9" + totAmount;
                        totBudgetAmount.setText(strTotal);
                    }
                    int weeklyBudget = totAmount / 4;
                    int dailyBudget = totAmount / 30;
                    personalRef.child("budget").setValue(totAmount);
                    personalRef.child("weeklyBudget").setValue(weeklyBudget);
                    personalRef.child("dailyBudget").setValue(dailyBudget);

                } else {
                    personalRef.child("budget").setValue(0);
                    personalRef.child("weeklyBudget").setValue(0);
                    personalRef.child("dailyBudget").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab = findViewById(R.id.bFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                additem();
            }
        });

        getMonthTransportBudgetRatios();
        getMonthFoodBudgetRatios();
        getMonthHouseBudgetRatios();
        getMonthEntBudgetRatios();
        getMonthEduBudgetRatios();
        getMonthCharityBudgetRatios();
        getMonthAppBudgetRatios();
        getMonthHealthBudgetRatios();
        getMonthPerBudgetRatios();
        getMonthOtherBudgetRatios();
    }

    private void additem() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_laout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemspinner = myView.findViewById(R.id.spinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText eComment = myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bgAmount = amount.getText().toString();
                String bgItem = itemspinner.getSelectedItem().toString();
                comment = eComment.getText().toString();
                if (TextUtils.isEmpty(bgAmount)) {
                    amount.setError("Amount is required!");
                    return;
                }
                if (bgItem.equals("Select Category")) {
                    Toast.makeText(BudgetActivity.this, "Select Category", Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog.setMessage("Adding Entry...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String id = entryreference.push().getKey();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Months months = Months.monthsBetween(epoch, now);

                String itemNday = bgItem + date;
                String itemNweek = bgItem + weeks.getWeeks();
                String itemNmonth = bgItem + months.getMonths();


                Data data = new Data(bgItem, date, id, itemNday, itemNweek, itemNmonth, Integer.parseInt(bgAmount), weeks.getWeeks(), months.getMonths(), comment);
                entryreference.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(BudgetActivity.this, "Entry Added SuccessFully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

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
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
                .setQuery(entryreference, Data.class)
                .build();

        FirebaseRecyclerAdapter<Data, myViewHolder> adapter = new FirebaseRecyclerAdapter<Data, myViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull myViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Data model) {
                holder.setAmount("Budget for "+model.getItem()+" \u20B9 " + model.getAmount());
                holder.setDate("On:" + model.getDate());
                holder.setItem("Category:" + model.getItem());
                holder.comment.setText("Comment:" + model.getComment());
                //holder.comment.setVisibility(View.GONE);

                switch (model.getItem()) {
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.ic_transport);
                        break;
                    case "Food":
                        holder.imageView.setImageResource(R.drawable.ic_food);
                        break;
                    case "House Expenses":
                        holder.imageView.setImageResource(R.drawable.ic_home);
                        break;
                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.ic_entertainment);
                        break;
                    case "Education":
                        holder.imageView.setImageResource(R.drawable.ic_education);
                        break;
                    case "Charity":
                        holder.imageView.setImageResource(R.drawable.ic_consultancy);
                        break;
                    case "Apparel and Services":
                        holder.imageView.setImageResource(R.drawable.ic_shirt);
                        break;
                    case "Health":
                        holder.imageView.setImageResource(R.drawable.ic_health);
                        break;
                    case "Personal Expenses":
                        holder.imageView.setImageResource(R.drawable.ic_personalcare);
                        break;
                    case "Other":
                        holder.imageView.setImageResource(R.drawable.ic_other);
                        break;


                }

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        item = model.getItem();
                        amount = model.getAmount();
                        comment = model.getComment();
                        itemDetails();
                    }
                });
            }

            @NonNull
            @Override
            public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_itemview, parent, false);
                return new myViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();

    }


    public class myViewHolder extends RecyclerView.ViewHolder {
        View mview;
        public ImageView imageView;
        public TextView comment;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            mview = itemView;
            imageView = itemView.findViewById(R.id.imageView2);
            comment = itemView.findViewById(R.id.comment);

        }

        public void setAmount(String itemAmount) {
            TextView amount = mview.findViewById(R.id.reamount);
            amount.setText(itemAmount);
        }

        public void setDate(String date) {
            TextView Date = mview.findViewById(R.id.date);
            Date.setText(date);
        }

        public void setItem(String itemName) {
            TextView item = mview.findViewById(R.id.item);
            item.setText(itemName);
        }

    }

    private void updateDate() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.layout_update, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mComment = mView.findViewById(R.id.note);
        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mComment.setText(comment);
        mAmount.setSelection(String.valueOf(amount).length());
        //Button deleteBtn = mView.findViewById(R.id.deleteBtn);
        Button updateBtn = mView.findViewById(R.id.updateBtn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mAmount.getText().toString());
                DateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Weeks weeks = Weeks.weeksBetween(epoch, now);
                Months months = Months.monthsBetween(epoch, now);
                String itemNday = item + date;
                String itemNweek = item + weeks.getWeeks();
                String itemNmonth = item + months.getMonths();


                Data data = new Data(item, date, post_key, itemNday, itemNweek, itemNmonth, amount, weeks.getWeeks(), months.getMonths(), mComment.getText().toString());

                entryreference.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            midItem.setText(item);
                            midAmount.setText(String.valueOf(amount));
                            //mAmount.setSelection(String.valueOf(amount).length());
                            midComment.setText(mComment.getText().toString());
                            midDate.setText(date);
                            Toast.makeText(BudgetActivity.this, "Updated SuccessFully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void itemDetails() {

        mDialog = new Dialog(BudgetActivity.this);
        mDialog.setContentView(R.layout.activity_item_details);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        midItem = mDialog.findViewById(R.id.tvType);
        midAmount = mDialog.findViewById(R.id.amount);
        midComment = mDialog.findViewById(R.id.tvComment);
        midDate = mDialog.findViewById(R.id.date);
        midDate = mDialog.findViewById(R.id.date);
        itemImg=mDialog.findViewById(R.id.imageView2);
        midItem.setText(item);
        midAmount.setText("\u20B9 "+String.valueOf(amount));
        //mAmount.setSelection(String.valueOf(amount).length());
        midComment.setText(comment);
        midDate.setText(date);
        //mComment.setSelection(Comment.length());
        ImageButton deleteBtn1 = mDialog.findViewById(R.id.deleteBtn);
        ImageButton updateBtn1 = mDialog.findViewById(R.id.updateBtn);
        switch (item) {
            case "Transport":
                itemImg.setImageResource(R.drawable.ic_transport);
                break;
            case "Food":
                itemImg.setImageResource(R.drawable.ic_food);
                break;
            case "House Expenses":
                itemImg.setImageResource(R.drawable.ic_home);
                break;
            case "Entertainment":
                itemImg.setImageResource(R.drawable.ic_entertainment);
                break;
            case "Education":
                itemImg.setImageResource(R.drawable.ic_education);
                break;
            case "Charity":
                itemImg.setImageResource(R.drawable.ic_consultancy);
                break;
            case "Apparel and Services":
                itemImg.setImageResource(R.drawable.ic_shirt);
                break;
            case "Health":
                itemImg.setImageResource(R.drawable.ic_health);
                break;
            case "Personal Expenses":
                itemImg.setImageResource(R.drawable.ic_personalcare);
                break;
            case "Other":
                itemImg.setImageResource(R.drawable.ic_other);
                break;
        }

        updateBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDate();
            }
        });
        deleteBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new android.app.AlertDialog.Builder(BudgetActivity.this)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Entry").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(BudgetActivity.this, "Deleted SuccessFully", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                dialog.dismiss();
                                mDialog.dismiss();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


        mDialog.show();

    }


    private void getMonthTransportBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("Transport");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayTransRatio = pTotal / 30;
                    int weekTransRatio = pTotal / 4;
                    int monthTransRatio = pTotal;

                    personalRef.child("dayTransRatio").setValue(dayTransRatio);
                    personalRef.child("weekTransRatio").setValue(weekTransRatio);
                    personalRef.child("monthTransRatio").setValue(monthTransRatio);

                } else {
                    personalRef.child("dayTransRatio").setValue(0);
                    personalRef.child("weekTransRatio").setValue(0);
                    personalRef.child("monthTransRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthFoodBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("Food");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayFoodRatio = pTotal / 30;
                    int weekFoodRatio = pTotal / 4;
                    int monthFoodRatio = pTotal;

                    personalRef.child("dayFoodRatio").setValue(dayFoodRatio);
                    personalRef.child("weekFoodRatio").setValue(weekFoodRatio);
                    personalRef.child("monthFoodRatio").setValue(monthFoodRatio);

                } else {
                    personalRef.child("dayFoodRatio").setValue(0);
                    personalRef.child("weekFoodRatio").setValue(0);
                    personalRef.child("monthFoodRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthHouseBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("House Expenses");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayHouseRatio = pTotal / 30;
                    int weekHouseRatio = pTotal / 4;
                    int monthHouseRatio = pTotal;

                    personalRef.child("dayHouseRatio").setValue(dayHouseRatio);
                    personalRef.child("weekHouseRatio").setValue(weekHouseRatio);
                    personalRef.child("monthHouseRatio").setValue(monthHouseRatio);

                } else {
                    personalRef.child("dayHouseRatio").setValue(0);
                    personalRef.child("weekHouseRatio").setValue(0);
                    personalRef.child("monthHouseRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEntBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("Entertainment");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayEntRatio = pTotal / 30;
                    int weekEntRatio = pTotal / 4;
                    int monthEntRatio = pTotal;

                    personalRef.child("dayEntRatio").setValue(dayEntRatio);
                    personalRef.child("weekEntRatio").setValue(weekEntRatio);
                    personalRef.child("monthEntRatio").setValue(monthEntRatio);

                } else {
                    personalRef.child("dayEntRatio").setValue(0);
                    personalRef.child("weekEntRatio").setValue(0);
                    personalRef.child("monthEntRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEduBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("Education");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayEduRatio = pTotal / 30;
                    int weekEduRatio = pTotal / 4;
                    int monthEduRatio = pTotal;

                    personalRef.child("dayEduRatio").setValue(dayEduRatio);
                    personalRef.child("weekEduRatio").setValue(weekEduRatio);
                    personalRef.child("monthEduRatio").setValue(monthEduRatio);

                } else {
                    personalRef.child("dayEduRatio").setValue(0);
                    personalRef.child("weekEduRatio").setValue(0);
                    personalRef.child("monthEduRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthCharityBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("Charity");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayCharRatio = pTotal / 30;
                    int weekCharRatio = pTotal / 4;
                    int monthCharRatio = pTotal;

                    personalRef.child("dayCharRatio").setValue(dayCharRatio);
                    personalRef.child("weekCharRatio").setValue(weekCharRatio);
                    personalRef.child("monthCharRatio").setValue(monthCharRatio);

                } else {
                    personalRef.child("dayCharRatio").setValue(0);
                    personalRef.child("weekCharRatio").setValue(0);
                    personalRef.child("monthCharRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthAppBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("Apparel and Services");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayAppRatio = pTotal / 30;
                    int weekAppRatio = pTotal / 4;
                    int monthAppRatio = pTotal;

                    personalRef.child("dayAppRatio").setValue(dayAppRatio);
                    personalRef.child("weekAppRatio").setValue(weekAppRatio);
                    personalRef.child("monthAppRatio").setValue(monthAppRatio);

                } else {
                    personalRef.child("dayAppRatio").setValue(0);
                    personalRef.child("weekAppRatio").setValue(0);
                    personalRef.child("monthAppRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthHealthBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("Health");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayHealthRatio = pTotal / 30;
                    int weekHealthRatio = pTotal / 4;
                    int monthHealthRatio = pTotal;

                    personalRef.child("dayHealthRatio").setValue(dayHealthRatio);
                    personalRef.child("weekHealthRatio").setValue(weekHealthRatio);
                    personalRef.child("monthHealthRatio").setValue(monthHealthRatio);

                } else {
                    personalRef.child("dayHealthRatio").setValue(0);
                    personalRef.child("weekHealthRatio").setValue(0);
                    personalRef.child("monthHealthRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthPerBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("Personal Expenses");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayPerRatio = pTotal / 30;
                    int weekPerRatio = pTotal / 4;
                    int monthPerRatio = pTotal;

                    personalRef.child("dayPerRatio").setValue(dayPerRatio);
                    personalRef.child("weekPerRatio").setValue(weekPerRatio);
                    personalRef.child("monthPerRatio").setValue(monthPerRatio);

                } else {
                    personalRef.child("dayPerRatio").setValue(0);
                    personalRef.child("weekPerRatio").setValue(0);
                    personalRef.child("monthPerRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthOtherBudgetRatios() {
        Query query = entryreference.orderByChild("item").equalTo("Other");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int pTotal = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayOtherRatio = pTotal / 30;
                    int weekOtherRatio = pTotal / 4;
                    int monthOtherRatio = pTotal;

                    personalRef.child("dayOtherRatio").setValue(dayOtherRatio);
                    personalRef.child("weekOtherRatio").setValue(weekOtherRatio);
                    personalRef.child("monthOtherRatio").setValue(monthOtherRatio);

                } else {
                    personalRef.child("dayOtherRatio").setValue(0);
                    personalRef.child("weekOtherRatio").setValue(0);
                    personalRef.child("monthOtherRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(BudgetActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}