package com.example.cabby333.myapplication;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import com.example.cabby333.myapplication.PortfolioActivity.STATES;

public class ItemListFragment extends Fragment {

    private static final String TAG = "ItemListFragment";
    private static final String MESSAGES = "messages";
    public static final String KEY_TYPE = "type";
    public static final String KEY_PATH = "path";
    public static final String KEY_NAME = "name";

    MessageAdapter.OnClickListener mCallback;
    PortfolioActivity mCurrActivity;
    RecyclerView mList;
    private MessageAdapter mAdapter;
    private STATES mType;
    private String mCurrDirPath;
    private String mCurrName;
    private View mView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_portfolio_navigator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated");

        mView = view;

        Bundle args = getArguments();
        if (args != null) {
            mType = STATES.valueOf(args.getString(KEY_TYPE));
            mCurrDirPath = args.getString(KEY_PATH);
            mCurrName = args.getString(KEY_NAME);
        } else {
            Log.e(TAG, "Did not receive arguments");
        }

        if (mType != STATES.ROOMS && mCurrName != null)
        {
            File currDir = new File(mCurrDirPath, mCurrName);
            if (!currDir.exists()) {
                currDir.mkdirs();
                mCurrDirPath = currDir.getPath();
                Log.i(TAG, "New House Directory created in path: " + mCurrDirPath);
            }
        }

        ArrayList<MessagePojo> input = getInput(savedInstanceState);

        mList = view.findViewById(R.id.rec_list_activity);
        mList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mAdapter = new MessageAdapter(input, mCallback, mType);

        loadItems();

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_create_new_item);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "New item clicked");
                // TODO add new property
                ((OnAppendItemListener)mCurrActivity).onAppendNewItem();

            }
        });
    }

    public void loadItems() {
        Log.i(TAG, "loadItems");
        if (mType == STATES.ESTATES) {
            if (mCurrActivity.getCurrItemsArr(mCurrName) != null) {
                for (String name : mCurrActivity.getCurrItemsArr(mCurrName)) {
                    mAdapter.addMessage(new MessagePojo(name, PortfolioActivity.ROOM_TYPES.HOUSE));
                }
            }
        } else {
            if (mCurrActivity.getmRoomsArr(mCurrName) != null) {
                for (Pair<String, String> pair : mCurrActivity.getmRoomsArr(mCurrName)) {
                    String roomName = pair.first;
                    PortfolioActivity.ROOM_TYPES roomType = PortfolioActivity.ROOM_TYPES.valueOf(pair.second);
                    mAdapter.addMessage(new MessagePojo(roomName, roomType));
                }
            }
        }
        mList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.i(TAG, "OnSwiped");
                MessagePojo clickedItem = mAdapter.data.get(viewHolder.getAdapterPosition());
                mAdapter.removeItem(clickedItem);
            }

        });

        itemTouchHelper.attachToRecyclerView(mList);

    }

    public String getmCurrName() {
        return mCurrName;
    }

    public void addItem(String name, PortfolioActivity.ROOM_TYPES roomType) {
        mAdapter.addMessage(new MessagePojo(name, roomType));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"Attached!");
        try {
            mCallback = (MessageAdapter.OnClickListener) getActivity();
            mCurrActivity = (PortfolioActivity)getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnClickListener");
        }
    }

    public static ItemListFragment newInstance(PortfolioActivity.STATES state, MessagePojo message, String currPath) {

        Bundle args = new Bundle();

        args.putString(KEY_TYPE, state.toString());
        args.putString(KEY_PATH, currPath + "/");
        if (message != null)
            args.putString(KEY_NAME, message.getName());

        ItemListFragment fragment = new ItemListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<MessagePojo> getInput(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "getInput");
        if (savedInstanceState == null) {
            Log.e(TAG, "null1");
            return new ArrayList<>();
        }
        if (savedInstanceState.getStringArrayList(MESSAGES) == null) {
            Log.e(TAG, "null2");
            return new ArrayList<>();
        }
        ArrayList<MessagePojo> output = new ArrayList<>();
        for (String singleMessage : Objects.requireNonNull(savedInstanceState.getStringArrayList(MESSAGES))) {
            try {
                JSONObject msg = new JSONObject(singleMessage);
                output.add(new MessagePojo(msg.getString("name"), null));
            } catch (JSONException e) {
                Log.e(TAG, "getInput: ", e);
            }
        }
        return output;
    }


    public interface OnAppendItemListener {
        void onAppendNewItem();
    }
}
