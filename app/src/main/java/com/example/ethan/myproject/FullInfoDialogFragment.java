package com.example.ethan.myproject;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ethan.myproject.model.People;
import com.squareup.picasso.Picasso;

public class FullInfoDialogFragment extends DialogFragment {

    private static final String PEOPLE = "people";
    TextView firstName;
    TextView lastName;
    TextView currentId;
    TextView phone;
    TextView email;
    TextView gender;
    ImageView photo;

    public static FullInfoDialogFragment newInstance(People people) {
        Bundle args = new Bundle();
        args.putSerializable(PEOPLE, people);

        FullInfoDialogFragment fragment = new FullInfoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setDialogWindow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.info_dialog_fragment, container, false);

        firstName = v.findViewById(R.id.first_name);
        lastName = v.findViewById(R.id.last_name);
        currentId = v.findViewById(R.id.current_id);
        phone = v.findViewById(R.id.phone);
        email = v.findViewById(R.id.email);
        photo = v.findViewById(R.id.photo);
        gender = v.findViewById(R.id.gender);

        return v;
    }

    private void setDialogWindow() {
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.y = -200;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.windowAnimations = android.R.anim.fade_out;
            window.setAttributes(layoutParams);

            window.setBackgroundDrawableResource(android.R.color.transparent);

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        People p = getArguments().getParcelable(PEOPLE);

        firstName.setText(p.getFirstName());
        lastName.setText(p.getLastName());
        phone.setText(p.getPhoneNumber());
        currentId.setText(p.getId().toString());
        email.setText(p.getEmail());
        gender.setText(p.getGender());
        Picasso.with(view.getContext()).load(p.getPhoto()).into(photo);
    }
}