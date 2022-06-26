/*
 * Created by Kultala Aki on 6/26/22, 6:19 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/26/22, 6:02 PM
 */

package kultalaaki.vpkapuri.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kultalaaki.vpkapuri.R;

public class WebviewFragment extends Fragment {

    public WebView mWebView;
    String urlAddress;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        readBundle(getArguments());
        return inflater.inflate(R.layout.webview_fragment, parent, false);
    }

    public static WebviewFragment newInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        WebviewFragment webviewFragment = new WebviewFragment();
        webviewFragment.setArguments(bundle);
        return webviewFragment;
    }

    private void readBundle(Bundle bundle) {
        if(bundle != null) {
            urlAddress = bundle.getString("url");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mWebView = view.findViewById(R.id.webview);
    }

    @Override
    public void onStart() {
        super.onStart();

        mWebView.loadUrl(urlAddress);
        mWebView.setWebViewClient(new WebViewClient());
    }
}
