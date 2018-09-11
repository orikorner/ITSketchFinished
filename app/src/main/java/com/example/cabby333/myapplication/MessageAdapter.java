package com.example.cabby333.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.cabby333.myapplication.PortfolioActivity.STATES;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MessageAdapter";
    final List<MessagePojo> data;
    private STATES mType;
    private final OnClickListener listener;
    private OnDeletedItemDirListener deletedItemFileListener;

    MessageAdapter(List<MessagePojo> input, OnClickListener listener, STATES type) {
        this.data = input;
        this.listener = listener;
        this.deletedItemFileListener = (OnDeletedItemDirListener)listener;
        this.mType = type;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder");

        final RecyclerView.ViewHolder holder;
        View view;

        switch (viewType) {
            case R.layout.card_item_house:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_house, parent, false);
                holder = new HouseViewHolder(view);
                ((HouseViewHolder)holder).setOnClickListener(listener);
                break;
            case R.layout.card_item_kitchen:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_kitchen, parent, false);
                holder = new KitchenViewHolder(view);
                ((KitchenViewHolder)holder).setOnClickListener(listener);
                break;
            case R.layout.card_item_bathroom:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_bathroom, parent, false);
                holder = new BathroomViewHolder(view);
                ((BathroomViewHolder)holder).setOnClickListener(listener);
                break;
            case R.layout.card_item_bedroom:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_bedroom, parent, false);
                holder = new BedroomViewHolder(view);
                ((BedroomViewHolder)holder).setOnClickListener(listener);
                break;
            case R.layout.card_item_livingroom:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_livingroom, parent, false);
                holder = new LivingroomViewHolder(view);
                ((LivingroomViewHolder)holder).setOnClickListener(listener);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item_default, parent, false);
                holder = new DefaultViewHolder(view);
                ((DefaultViewHolder)holder).setOnClickListener(listener);
                break;

        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagePojo msg = data.get(position);
        if (holder instanceof HouseViewHolder)
            ((HouseViewHolder)holder).name.setText(msg.getName());
        else if (holder instanceof KitchenViewHolder)
            ((KitchenViewHolder)holder).name.setText(msg.getName());
        else if (holder instanceof BathroomViewHolder)
            ((BathroomViewHolder)holder).name.setText(msg.getName());
        else if (holder instanceof BedroomViewHolder)
            ((BedroomViewHolder)holder).name.setText(msg.getName());
        else if (holder instanceof LivingroomViewHolder)
            ((LivingroomViewHolder)holder).name.setText(msg.getName());
        else
            ((DefaultViewHolder)holder).name.setText(msg.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public int getItemViewType(int position) {
        MessagePojo msg = data.get(position);

        switch (msg.getRoomType()) {
            case HOUSE:
                return R.layout.card_item_house;
            case KITCHEN:
                return R.layout.card_item_kitchen;
            case BEDROOM:
                return R.layout.card_item_bedroom;
            case BATHROOM:
                return R.layout.card_item_bathroom;
            case LIVINGROOM:
                return R.layout.card_item_livingroom;
            default:
                return R.layout.card_item_default;
        }
    }

    public void addMessage(MessagePojo msg) {
        data.add(msg);
        notifyItemInserted(data.size() - 1);
    }

    public void removeItem(MessagePojo msg) {
        for (int i = 0, size = data.size(); i < size; i++) {
            if (msg.equals(data.get(i))) {
                Log.i(TAG, "Removed item in index: " + i);
                data.remove(i);
                notifyItemRemoved(i);
                deletedItemFileListener.onDeleteItemDirByName(msg.getName(), msg.getFileName());
                return;
            }
        }
        throw new IllegalArgumentException("item is not in dataset");
    }

    class HouseViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView rightArrow;
        OnClickListener listener;

        HouseViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rightArrow = itemView.findViewById(R.id.rightArrow);

            if (mType != STATES.PHOTOS) {
                rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            MessagePojo clickedMsg = data.get(getAdapterPosition());
                            final int size = data.size();
                            data.clear();
                            notifyItemRangeRemoved(0, size);
                            listener.onClick(mType, clickedMsg);
                        }
                    }
                });
            }

        }

        void setOnClickListener(OnClickListener onClickListener) {
            this.listener = onClickListener;
        }
    }

    class DefaultViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView rightArrow;
        OnClickListener listener;

        DefaultViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rightArrow = itemView.findViewById(R.id.rightArrow);

            if (mType != STATES.PHOTOS) {
                rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            MessagePojo clickedMsg = data.get(getAdapterPosition());
                            final int size = data.size();
                            data.clear();
                            notifyItemRangeRemoved(0, size);
                            listener.onClick(mType, clickedMsg);
                        }
                    }
                });
            }

        }

        void setOnClickListener(OnClickListener onClickListener) {
            this.listener = onClickListener;
        }
    }

    class KitchenViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView rightArrow;
        OnClickListener listener;

        KitchenViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rightArrow = itemView.findViewById(R.id.rightArrow);

            if (mType != STATES.PHOTOS) {
                rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            MessagePojo clickedMsg = data.get(getAdapterPosition());
                            final int size = data.size();
                            data.clear();
                            notifyItemRangeRemoved(0, size);
                            listener.onClick(mType, clickedMsg);
                        }
                    }
                });
            }

        }

        void setOnClickListener(OnClickListener onClickListener) {
            this.listener = onClickListener;
        }
    }

    class BathroomViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView rightArrow;
        OnClickListener listener;

        BathroomViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rightArrow = itemView.findViewById(R.id.rightArrow);

            if (mType != STATES.PHOTOS) {
                rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            MessagePojo clickedMsg = data.get(getAdapterPosition());
                            final int size = data.size();
                            data.clear();
                            notifyItemRangeRemoved(0, size);
                            listener.onClick(mType, clickedMsg);
                        }
                    }
                });
            }

        }

        void setOnClickListener(OnClickListener onClickListener) {
            this.listener = onClickListener;
        }
    }

    class BedroomViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView rightArrow;
        OnClickListener listener;

        BedroomViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rightArrow = itemView.findViewById(R.id.rightArrow);

            if (mType != STATES.PHOTOS) {
                rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            MessagePojo clickedMsg = data.get(getAdapterPosition());
                            final int size = data.size();
                            data.clear();
                            notifyItemRangeRemoved(0, size);
                            listener.onClick(mType, clickedMsg);
                        }
                    }
                });
            }

        }

        void setOnClickListener(OnClickListener onClickListener) {
            this.listener = onClickListener;
        }
    }

    class LivingroomViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView rightArrow;
        OnClickListener listener;

        LivingroomViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            rightArrow = itemView.findViewById(R.id.rightArrow);

            if (mType != STATES.PHOTOS) {
                rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            MessagePojo clickedMsg = data.get(getAdapterPosition());
                            final int size = data.size();
                            data.clear();
                            notifyItemRangeRemoved(0, size);
                            listener.onClick(mType, clickedMsg);
                        }
                    }
                });
            }

        }

        void setOnClickListener(OnClickListener onClickListener) {
            this.listener = onClickListener;
        }
    }

    interface OnClickListener {
        void onClick(STATES state, MessagePojo message);
    }

    interface OnDeletedItemDirListener {
        void onDeleteItemDirByName(String name, String filePath);
    }

}