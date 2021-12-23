package com.android.notes.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import com.android.notes.R;
import com.android.notes.domain.AccountInfo;
import com.android.notes.utils.NotesLogger;

public class ProfileFragment extends Fragment {

    private GoogleSignInClient googleSignInClient;
    private GoogleSignInAccount account;
    private final int RC_GOOGLE_SIGN_IN = 1;
    private final int RC_VK_SIGN_IN = 282;
    private final String AUTH = "AUTH";
    private final AccountInfo accountInfo = AccountInfo.INSTANCE;
    private NavController navController;
    TextView prof_name;
    TextView prof_mail;
    AppCompatImageView prof_image;
    SignInButton authBtn;
    MaterialButton authOutBtn;
    MaterialButton authVKBtn;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        // Получаем клиента для регистрации и данные по клиенту
        googleSignInClient = GoogleSignIn.getClient(getContext(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        init(root);

        if (accountInfo.isEmpty()) {
            updateUI(false);
        } else {
            updateUI(true);
        }
        return root;
    }

    void init(View view) {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        prof_name = view.findViewById(R.id.profile_user_name);
        prof_mail = view.findViewById(R.id.profile_user_mail);
        prof_image = view.findViewById(R.id.profile_imageAvatar);
        authBtn = view.findViewById(R.id.signBtn);
        authBtn.setOnClickListener(v -> {
            signIn();
        });

        authOutBtn = view.findViewById(R.id.signOutBtn);
        authOutBtn.setOnClickListener(v -> {
            signOut();
        });

        authVKBtn = view.findViewById(R.id.signVKBtn);
        authVKBtn.setOnClickListener(v -> {
            signVKIn();
        });

        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer_layout);
        toolbar = view.findViewById(R.id.toolbar);
        //делаем бургер
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    // Инициируем регистрацию пользователя через Google
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    // Инициируем регистрацию пользователя через VK
    private void signVKIn() {
        List<VKScope> vcScopeList = new ArrayList<>();
        vcScopeList.add(VKScope.EMAIL);
        vcScopeList.add(VKScope.PHOTOS);
        vcScopeList.add(VKScope.STATUS);
        VK.login(getActivity(), vcScopeList);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        } else if (requestCode == RC_VK_SIGN_IN) {
            VKAuthCallback vkAuthCallback = new VKAuthCallback() {
                @Override
                public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                    handleVKSignInResult(vkAccessToken);
                }

                @Override
                public void onLoginFailed(int i) {
                }
            };
            VK.onActivityResult(requestCode, resultCode, data, vkAuthCallback);
        }
    }

    // Получаем данные пользователя VK
    private void handleVKSignInResult(VKAccessToken vkAccessToken) {
        if (vkAccessToken != null) {
            accountInfo.setAuthType(AuthType.VK);
            accountInfo.setName(String.valueOf(vkAccessToken.getUserId()));
            accountInfo.setMail(vkAccessToken.getEmail());
            updateUI(true);
            Toast.makeText(getContext(), "Вы вошли как " + accountInfo.getName(), Toast.LENGTH_SHORT).show();
            navController.popBackStack();
            navController.navigate(R.id.nav__item_notes);
        } else {
            accountInfo.clear();
            updateUI(false);
        }
    }

    // Получаем данные пользователя Google
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            if (account != null) {
                accountInfo.setAuthType(AuthType.GOOGLE);
                accountInfo.setName(account.getDisplayName());
                accountInfo.setMail(account.getEmail());
                accountInfo.setImageURL(account.getPhotoUrl().toString());
                updateUI(true);
                Toast.makeText(getContext(), "Вы вошли как " + accountInfo.getName(), Toast.LENGTH_SHORT).show();
                navController.popBackStack();
                navController.navigate(R.id.nav__item_notes);
            } else {
                accountInfo.clear();
                updateUI(false);
            }
        } catch (ApiException e) {
        }
    }

    void updateUI(boolean show) {
        try {
            if (show) {
                prof_name.setText(accountInfo.getName());
                prof_mail.setText(accountInfo.getMail());

                authBtn.setVisibility(View.GONE);
                authOutBtn.setVisibility(View.VISIBLE);
                authVKBtn.setVisibility(View.GONE);

                ((TextView) getActivity().findViewById(R.id.user_mail)).setText(accountInfo.getMail());
                ((TextView) getActivity().findViewById(R.id.user_name)).setText(accountInfo.getName());
                ImageView imageView = getActivity().findViewById(R.id.imageAvatar);
                if (accountInfo.getImageURL() != null) {
                    Glide.with(getContext())
                            .load(accountInfo.getImageURL())
                            .circleCrop()
                            .into(imageView);
                    Glide.with(getContext())
                            .load(accountInfo.getImageURL())
                            .circleCrop()
                            .into(prof_image);

                } else {
                    Glide.with(getContext())
                            .load(R.drawable.ic_header_avatar)
                            .circleCrop()
                            .into(imageView);
                    Glide.with(getContext())
                            .load(R.drawable.ic_header_avatar)
                            .circleCrop()
                            .into(prof_image);
                }

            } else {
                prof_name.setText(R.string.nav_header_user_name);
                prof_mail.setText(R.string.nav_header_user_mail);
                Glide.with(getContext())
                        .load(R.drawable.ic_header_avatar)
                        .circleCrop()
                        .into(prof_image);

                authBtn.setVisibility(View.VISIBLE);
                authVKBtn.setVisibility(View.VISIBLE);
                authOutBtn.setVisibility(View.GONE);
                ((TextView) getActivity().findViewById(R.id.user_mail)).setText(R.string.nav_header_user_mail);
                ((TextView) getActivity().findViewById(R.id.user_name)).setText(R.string.nav_header_user_name);
                ImageView imageView = getActivity().findViewById(R.id.imageAvatar);
                Glide.with(getContext())
                        .load(R.drawable.ic_header_avatar)
                        .circleCrop()
                        .into(imageView);
            }
        } catch (Exception err) {
            NotesLogger.printLog(err.getMessage());
        }
    }

    // Инициируем выход из учетки
    private void signOut() {
        switch (accountInfo.getAuthType()) {
            case VK:
                if (VK.isLoggedIn()) {
                    VK.logout();
                    accountInfo.clear();
                    updateUI(false);
                }
                break;
            case GOOGLE:
                googleSignInClient.signOut();
                accountInfo.clear();
                updateUI(false);
                break;
        }
    }
}
