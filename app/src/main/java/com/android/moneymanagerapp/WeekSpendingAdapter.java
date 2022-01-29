package com.android.moneymanagerapp;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class WeekSpendingAdapter extends RecyclerView.Adapter<WeekSpendingAdapter.viewHolder> {
    private Context context;
    private List<Data> myDataList;
    private String post_key = "";
    private String item = "";
    private String comment = "";
    private int amount = 0;
    TextView midItem, midAmount, midComment, midDate;
    ImageView itemImg;

    public WeekSpendingAdapter(Context context, List<Data> myDataList) {
        this.context = context;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_itemview, parent, false);
        return new WeekSpendingAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Data data = myDataList.get(position);
        holder.item.setText("Item:"+data.getItem());
        holder.amount.setText("Allocated Amount:Rs"+data.getAmount());
        holder.date.setText("On:" +data.getDate());
        holder.comment.setText("Comment:"+data.getComment());

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
                Dialog mdialog = new Dialog(context);
                mdialog.setContentView(R.layout.activity_item_details);
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                String date = dateFormat.format(cal.getTime());
                midItem = mdialog.findViewById(R.id.tvType);
                midAmount = mdialog.findViewById(R.id.amount);
                midComment = mdialog.findViewById(R.id.tvComment);
                midDate = mdialog.findViewById(R.id.date);
                itemImg=mdialog.findViewById(R.id.imageView2);
                midItem.setText(data.getItem());
                midAmount.setText("\u20B9 "+String.valueOf(data.getAmount()));
                //mAmount.setSelection(String.valueOf(amount).length());
                midComment.setText(data.getComment());
                midDate.setText(date);
                //mComment.setSelection(Comment.length());
                ImageButton deleteBtn1 = mdialog.findViewById(R.id.deleteBtn);
                ImageButton updateBtn1 = mdialog.findViewById(R.id.updateBtn);
                deleteBtn1.setVisibility(View.GONE);
                updateBtn1.setVisibility(View.GONE);
                switch (data.getItem()) {
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
                mdialog.show();
            }
        });
    }

//    @SuppressLint("SetTextI18n")
//    private void itemDetails() {
//
//        Dialog mdialog = new Dialog(context);
//        mdialog.setContentView(R.layout.activity_item_details);
//        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//        Calendar cal = Calendar.getInstance();
//        String date = dateFormat.format(cal.getTime());
//        midItem = mdialog.findViewById(R.id.tvType);
//        midAmount = mdialog.findViewById(R.id.amount);
//        midComment = mdialog.findViewById(R.id.tvComment);
//        midDate = mdialog.findViewById(R.id.date);
//        itemImg=mdialog.findViewById(R.id.imageView2);
//        midItem.setText(data.getItem());
//        midAmount.setText("\u20B9 "+String.valueOf(data.getAmount()));
//        //mAmount.setSelection(String.valueOf(amount).length());
//        midComment.setText(data.getComment());
//        midDate.setText(date);
//        //mComment.setSelection(Comment.length());
//        ImageButton deleteBtn1 = mdialog.findViewById(R.id.deleteBtn);
//        ImageButton updateBtn1 = mdialog.findViewById(R.id.updateBtn);
//        deleteBtn1.setVisibility(View.GONE);
//        updateBtn1.setVisibility(View.GONE);
//        switch (data.getItem()) {
//            case "Transport":
//                itemImg.setImageResource(R.drawable.ic_transport);
//                break;
//            case "Food":
//                itemImg.setImageResource(R.drawable.ic_food);
//                break;
//            case "House Expenses":
//                itemImg.setImageResource(R.drawable.ic_home);
//                break;
//            case "Entertainment":
//                itemImg.setImageResource(R.drawable.ic_entertainment);
//                break;
//            case "Education":
//                itemImg.setImageResource(R.drawable.ic_education);
//                break;
//            case "Charity":
//                itemImg.setImageResource(R.drawable.ic_consultancy);
//                break;
//            case "Apparel and Services":
//                itemImg.setImageResource(R.drawable.ic_shirt);
//                break;
//            case "Health":
//                itemImg.setImageResource(R.drawable.ic_health);
//                break;
//            case "Personal Expenses":
//                itemImg.setImageResource(R.drawable.ic_personalcare);
//                break;
//            case "Other":
//                itemImg.setImageResource(R.drawable.ic_other);
//                break;
//        }
//        mdialog.show();
//
//    }

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





































//package com.android.moneymanagerapp;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class WeekSpendingAdapter extends RecyclerView.Adapter<WeekSpendingAdapter.ViewHolder> {
//
//    private Context mContext;
//    private List<Data> myDataList;
//
//    public WeekSpendingAdapter(Context mContext, List<Data> myDataList) {
//        this.mContext = mContext;
//        this.myDataList = myDataList;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.input_laout, parent,false);
//        return new WeekSpendingAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//        final  Data data = myDataList.get(position);
//
//        holder.item.setText("Item: "+ data.getItem());
//        holder.amount.setText("Amount: "+ data.getAmount());
//        holder.date.setText("On "+data.getDate());
//        holder.comment.setText("Note: "+data.getComment());
//        switch (data.getItem()){
//            case "Transport":
//                holder.imageView.setImageResource(R.drawable.ic_transport);
//                break;
//            case "Food":
//                holder.imageView.setImageResource(R.drawable.ic_food);
//                break;
//            case "House":
//                holder.imageView.setImageResource(R.drawable.ic_house);
//                break;
//            case "Entertainment":
//                holder.imageView.setImageResource(R.drawable.ic_entertainment);
//                break;
//            case "Education":
//                holder.imageView.setImageResource(R.drawable.ic_education);
//                break;
//            case "Charity":
//                holder.imageView.setImageResource(R.drawable.ic_consultancy);
//                break;
//            case "Apparel":
//                holder.imageView.setImageResource(R.drawable.ic_shirt);
//                break;
//            case "Health":
//                holder.imageView.setImageResource(R.drawable.ic_health);
//                break;
//            case "Personal":
//                holder.imageView.setImageResource(R.drawable.ic_personalcare);
//                break;
//            case "Other":
//                holder.imageView.setImageResource(R.drawable.ic_other);
//                break;
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return myDataList.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder{
//
//        public TextView item, amount, date, comment;
//        public ImageView imageView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            item = itemView.findViewById(R.id.item);
//            amount = itemView.findViewById(R.id.reamount);
//            imageView = itemView.findViewById(R.id.imageView2);
//            date = itemView.findViewById(R.id.date);
//            comment = itemView.findViewById(R.id.comment);
//        }
//    }
//}