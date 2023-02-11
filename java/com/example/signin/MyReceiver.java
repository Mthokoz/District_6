package com.example.signin;


import android.content.ClipData;
import android.net.Uri;
import android.os.Build;
import android.util.Pair;
import android.view.ContentInfo;
import android.view.OnReceiveContentListener;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.view.ContentInfoCompat;

// (1) Define the listener
@RequiresApi(api = Build.VERSION_CODES.S)
public class MyReceiver implements OnReceiveContentListener {
    public static final String[] MIME_TYPES = new String[] {"image/*", "video/*"};

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public ContentInfo onReceiveContent(View view, ContentInfo payload) {
        Pair<ContentInfo, ContentInfo> split =
                ContentInfoCompat.partition(payload, item -> item.getUri() != null);
        ContentInfo uriContent = split.first;
        ContentInfo remaining = split.second;
        if (uriContent != null) {
            ClipData clip = uriContent.getClip();
            for (int i = 0; i < clip.getItemCount(); i++) {
                Uri uri = clip.getItemAt(i).getUri();
                // ... app-specific logic to handle the URI ...
            }
        }
        // Return anything that we didn't handle ourselves. This preserves the default platform
        // behavior for text and anything else for which we are not implementing custom handling.
        return remaining;
    }
}


