package com.gracecode.android.presentation.helper;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class IntentHelper {
    public static void sendMail(Context context, String to, String subject, String content) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, to);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, content);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(i, "Send Email"));
    }


    /**
     * 通过系统 Intent 分享到其他程序
     *
     * @param message   String
     * @param imagePath Uri
     */
    public static void openShareIntentWithImage(Context context, String message, Uri imagePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.putExtra(Intent.EXTRA_STREAM, imagePath);
        intent.setType("image/jpeg");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(Intent.createChooser(intent, ""));
    }

    public static void openWithBrowser(Context context, String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
