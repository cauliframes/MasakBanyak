package com.baskom.masakbanyak;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.app.Activity.RESULT_OK;
import static com.baskom.masakbanyak.Constants.verifyToken;

public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Customer mCustomer;
    private File mAvatar;

    private ProfileFragmentInteractionListener mListener;

    private CircleImageView mImageView;
    private EditText mEditTextName;
    private EditText mEditTextPhone;
    private EditText mEditTextEmail;
    private ConstraintLayout mConstraintLayout;
    private Button mButtonUpdateProfile;
    private Button mButtonLogout;
    private ProgressBar mProgressBar;

    private CanMakeServiceCall mMakeProfileCall = new CanMakeServiceCall() {
        @Override
        public void makeCall(final Context context, MasakBanyakService service, String access_token) {
            Call<Customer> call = service.profile("Bearer "+access_token);

            call.enqueue(new Callback<Customer>() {
                @Override
                public void onResponse(Call<Customer> call, Response<Customer> response) {
                    if(response.isSuccessful()){
                        mCustomer = response.body();

                        Picasso.get().load(mCustomer.getAvatar()).into(mImageView);

                        mEditTextName.setText(mCustomer.getName());
                        mEditTextPhone.setText(mCustomer.getPhone());
                        mEditTextEmail.setText(mCustomer.getEmail());

                        mProgressBar.setVisibility(View.GONE);
                        mConstraintLayout.setVisibility(View.VISIBLE);
                    }else{
                        try {
                            Toast.makeText(
                                    context,
                                    response.errorBody().string(),
                                    Toast.LENGTH_LONG
                            ).show();
                            mProgressBar.setVisibility(View.GONE);
                        } catch (IOException e) {
                            Toast.makeText(
                                    context,
                                    e.toString(),
                                    Toast.LENGTH_LONG
                            ).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Customer> call, Throwable t) {
                    Toast.makeText(
                            context,
                            t.toString(),
                            Toast.LENGTH_LONG
                    ).show();
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
    };

    private CanMakeServiceCall mMakeProfileUpdateCall = new CanMakeServiceCall() {
        @Override
        public void makeCall(final Context context, MasakBanyakService service, String access_token) {
            Call<ResponseBody> call = service.updateProfile(
                    "Bearer "+access_token,
                    mEditTextName.getText().toString(),
                    mEditTextPhone.getText().toString(),
                    mEditTextEmail.getText().toString()
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        try {
                            Toast.makeText(context, response.body().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private CanMakeServiceCall mMakeAvatarUploadCall = new CanMakeServiceCall() {
        @Override
        public void makeCall(final Context context, MasakBanyakService service, String access_token) {
            RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/*"), mAvatar);

            Call<ResponseBody> call = service.uploadAvatar(
                    "Bearer "+access_token,
                    fileRequestBody
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        try {
                            Toast.makeText(context, response.body().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        try {
                            Toast.makeText(context, response.errorBody().string(), Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mImageView = view.findViewById(R.id.image_view);
        mEditTextName = view.findViewById(R.id.edit_text_name);
        mEditTextPhone = view.findViewById(R.id.edit_text_phone);
        mEditTextEmail = view.findViewById(R.id.edit_text_email);

        mConstraintLayout = view.findViewById(R.id.constraint_layout_button_background);
        mConstraintLayout.setVisibility(View.INVISIBLE);

        mButtonUpdateProfile = view.findViewById(R.id.button_update);
        mButtonLogout = view.findViewById(R.id.button_logout);

        mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(
                                Intent.createChooser(intent, "Select Picture"),
                                PICK_IMAGE_REQUEST_CODE
                        );
                    }
                }
        );

        //Hide the keyboard when enter is pressed, but why it affects other editText object ass well?
        mEditTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mEditTextName.getWindowToken(),
                            0);
                    return true;
                }
                return false;
            }
        });

        mButtonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyToken(v.getContext(), mMakeProfileUpdateCall);
            }
        });

        mButtonLogout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logout(v.getContext());
                    }
                }
        );

        mProgressBar = view.findViewById(R.id.progress_bar);
        FoldingCube foldingCube = new FoldingCube();
        foldingCube.setColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
        mProgressBar.setIndeterminateDrawable(foldingCube);

        verifyToken(view.getContext(), mMakeProfileCall);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragmentInteractionListener) {
            mListener = (ProfileFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CateringFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {

            Uri uri = data.getData();
            mAvatar = new File(uri.getPath());

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(),
                        uri
                );

                mImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            verifyToken(getContext(), mMakeAvatarUploadCall);
        }
    }

    public interface ProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        void onProfileFragmentInteraction(Uri uri);
    }

    public void logout(final Context context){
        final SharedPreferences sharedPreferences = context.getSharedPreferences(
                getString(R.string.app_preference_key),
                Context.MODE_PRIVATE
        );
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        String accessToken = sharedPreferences.getString("access_token", null);
        String refreshToken = sharedPreferences.getString("refresh_token", null);
        String customer_id = new JWT(accessToken).getClaim("customer_id").asString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .build();

        MasakBanyakService service = retrofit.create(MasakBanyakService.class);

        Call<ResponseBody> call = service.logout(refreshToken, customer_id);

        call.enqueue(
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(
                            Call<ResponseBody> call,
                            Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            editor.clear();
                            editor.apply();

                            try {
                                Toast.makeText(
                                        context,
                                        response.body().string(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            } catch (IOException e) {
                                Toast.makeText(
                                        context,
                                        e.toString(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                            Intent intent = new Intent(context, PreLoginRegisterActivity.class);
                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK |
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        }else{
                            try {
                                Toast.makeText(
                                        context,
                                        response.errorBody().string(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            } catch (IOException e) {
                                Toast.makeText(
                                        context,
                                        e.toString(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ResponseBody> call,
                            Throwable t) {
                        Toast.makeText(
                                context,
                                t.toString(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

}
