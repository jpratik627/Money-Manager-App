package com.android.moneymanagerapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayItemAdapter extends RecyclerView.Adapter<TodayItemAdapter.viewHolder> {
    private Context mcontext;
    private List<Data> myDataList;
    private String post_key = "";
    private String item = "";
    private String comment = "";
    private int amount = 0;
    TextView midItem, midAmount, midComment, midDate;
    ImageView itemImg;
    public TodayItemAdapter(Context mcontext, List<Data> myDataList) {
        this.mcontext = mcontext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_itemview, parent, false);
        return new TodayItemAdapter.viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        final Data data = myDataList.get(position);
        holder.item.setText("Category: " + data.getItem());
        holder.amount.setText("Amount:\u20B9 "+data.getAmount() );
        holder.date.setText("On:" + data.getDate());
        holder.comment.setText("Comment:" + data.getComment());

        switch (data.getItem()) {
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_key = data.getId();
                item = data.getItem();
                amount = data.getAmount();
                comment = data.getComment();
                itemDetails();
            }
        });
    }

    private void itemDetails() {

        Dialog mdialog = new Dialog(mcontext);
        mdialog.setContentView(R.layout.activity_item_details);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        midItem = mdialog.findViewById(R.id.tvType);
        midAmount = mdialog.findViewById(R.id.amount);
        midComment = mdialog.findViewById(R.id.tvComment);
        midDate = mdialog.findViewById(R.id.date);
        itemImg=mdialog.findViewById(R.id.imageView2);
        midItem.setText(item);
        midAmount.setText("\u20B9 "+String.valueOf(amount));
        //mAmount.setSelection(String.valueOf(amount).length());
        midComment.setText(comment);
        midDate.setText(date);
        //mComment.setSelection(Comment.length());
        ImageButton deleteBtn1 = mdialog.findViewById(R.id.deleteBtn);
        ImageButton updateBtn1 = mdialog.findViewById(R.id.updateBtn);
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
                new AlertDialog.Builder(mcontext)
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expense").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mcontext, "Deleted SuccessFully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(mcontext, task.getException().toString(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                                dialog.dismiss();
                                mdialog.dismiss();
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


        mdialog.show();

    }

    private void updateDate() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(mcontext);
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View mView = inflater.inflate(R.layout.layout_update, null);
        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mComment = mView.findViewById(R.id.note);
        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());
        mComment.setText(comment);
        mComment.setSelection(comment.length());
        //Button deleteBtn = mView.findViewById(R.id.deleteBtn);
        Button updateBtn = mView.findViewById(R.id.updateBtn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mAmount.getText().toString());
                comment = mComment.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
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


                Data data = new Data(item, date, post_key, itemNday, itemNweek, itemNmonth, amount, weeks.getWeeks(), months.getMonths(), comment);

                //Data data = new Data(item, date, post_key, Comment, amount,weeks.getWeeks(),months.getMonths());
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expense").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            midItem.setText(item);
                            midAmount.setText(String.valueOf(amount));
                            //mAmount.setSelection(String.valueOf(amount).length());
                            midComment.setText(comment);
                            Toast.makeText(mcontext, "Updated SuccessFully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mcontext, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                dialog.dismiss();
            }
        });
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Expense").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(mcontext, "Deleted SuccessFully", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(mcontext, task.getException().toString(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });
//                dialog.dismiss();
//            }
//        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView item, amount, date, comment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            amount = itemView.findViewById(R.id.reamount);
            imageView = itemView.findViewById(R.id.imageView2);
            date = itemView.findViewById(R.id.date);
            comment = itemView.findViewById(R.id.comment);

        }
    }
}
