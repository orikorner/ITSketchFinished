package com.example.cabby333.myapplication;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Objects;

import static com.example.cabby333.myapplication.ItemListFragment.KEY_TYPE;

public class NewPortfolioFragment extends Fragment {

    OnNewItemListener mCallback;

    private String TAG = "NewPortfolioFragment";
    private PortfolioActivity.STATES mState;
    private PortfolioActivity.ROOM_TYPES mRoomType;
    private EditText mEditText;
    private RadioGroup mRadioGroup;


    public static NewPortfolioFragment newInstance(PortfolioActivity.STATES currState) {

        Bundle args = new Bundle();

        if (currState == null)
        {
            args.putString(KEY_TYPE, PortfolioActivity.STATES.ESTATES.toString());
        } else {
            args.putString(KEY_TYPE, currState.toString());
        }

        NewPortfolioFragment newPortFrag = new NewPortfolioFragment();
        newPortFrag.setArguments(args);

        return newPortFrag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_portfolio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        mEditText = view.findViewById(R.id.name);
        mRadioGroup = view.findViewById(R.id.radio_group);

        Bundle args = getArguments();
        if (args != null) {
            mState = PortfolioActivity.STATES.valueOf(args.getString(KEY_TYPE));
            if (mState == PortfolioActivity.STATES.ROOMS) {
                mRadioGroup.setVisibility(View.VISIBLE);
                mRoomType = PortfolioActivity.ROOM_TYPES.DEFAULT; // Default value
            } else {
                mRoomType = PortfolioActivity.ROOM_TYPES.HOUSE;
            }
        } else {
            Log.e(TAG, "Did not receive arguments");
        }

        Button applyButton = view.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Apply button clicked");
                // TODO make a new portfolio with inName
                String inName = mEditText.getText().toString();
                if (mCallback == null)
                    Log.e(TAG, "Null callback");
                else
                    mCallback.onApplyNewItem(inName, mRoomType);

            }
        });

        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Canecl button clicked");
                getActivity().onBackPressed();
            }
        });

        if (mState == PortfolioActivity.STATES.ROOMS)
        {
            mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // checkId is the RadioButton selected
                    switch (checkedId) {
                        case R.id.radio_bathroom:
                            mRoomType = PortfolioActivity.ROOM_TYPES.BATHROOM;
                            break;
                        case R.id.radio_bedroom:
                            mRoomType = PortfolioActivity.ROOM_TYPES.BEDROOM;
                            break;
                        case R.id.radio_kitchen:
                            mRoomType = PortfolioActivity.ROOM_TYPES.KITCHEN;
                            break;
                        case R.id.radio_livingroom:
                            mRoomType = PortfolioActivity.ROOM_TYPES.LIVINGROOM;
                            break;
                        default:
                            mRoomType = PortfolioActivity.ROOM_TYPES.DEFAULT;
                    }
                }
            });
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"Attached!");
        try {
            mCallback = (OnNewItemListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnNewPortfolioListener");
        }
    }

    public interface OnNewItemListener {
        void onApplyNewItem(String name, PortfolioActivity.ROOM_TYPES roomType);
    }

}
