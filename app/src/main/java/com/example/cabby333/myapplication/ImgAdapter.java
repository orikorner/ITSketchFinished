package com.example.cabby333.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.ViewHolder> {

    private static final String TAG = "ImgAdapter";
    private ArrayList<Bitmap> imgList;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private PortfolioActivity mLongClickListener;

    ImgAdapter(Context context, ArrayList<Bitmap> imgList) {
        this.mInflater = LayoutInflater.from(context);
        this.imgList = imgList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycleview_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the img to the ImageView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap curImg = imgList.get(position);
        holder.bindImgToViewHolder(curImg);
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    // the view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.image_box);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bindImgToViewHolder(Bitmap img) {

            myImageView.setImageBitmap(img);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Log.i(TAG, "onLongClick");

            AlertDialog.Builder builder = new AlertDialog.Builder((Activity)mClickListener);
            builder.setMessage("Are you sure you want to delete this image?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            Log.i(TAG, "Ok clicked - need to delete");
                            if (mLongClickListener != null)
                                removeItem(getAdapterPosition());
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            return false;
        }
    }

    private void removeItem(int id) {
        mLongClickListener.onDeleteItemFileByName(id);
        imgList.remove(id);
        notifyItemRemoved(id);

    }

    Bitmap getItem(int id) {
        return imgList.get(id);
    }

    void setCallbackListener(PortfolioActivity itemClickListener) {
        this.mClickListener = (ItemClickListener)itemClickListener;
        this.mLongClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}