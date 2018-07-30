/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amyrobotics.fileexplorer.viewmodel;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.amyrobotics.fileexplorer.utils.MimeUtils;

import com.amyrobotics.fileexplorer.R;

public class IntentBuilder {

    public static void viewFile(final Context context, final String filePath) {
        String type = getMimeType(filePath);

        if (!TextUtils.isEmpty(type) && !TextUtils.equals(type, "*/*")) {
            /* 设置intent的file与MimeType */
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setDataAndType(getUriForFile(context, new File(filePath)), type);
            context.startActivity(intent);
        } else {
            // unknown MimeType
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            dialogBuilder.setTitle(R.string.dialog_select_type);

            CharSequence[] menuItemArray = new CharSequence[] {
                    context.getString(R.string.dialog_type_text),
                    context.getString(R.string.dialog_type_audio),
                    context.getString(R.string.dialog_type_video),
                    context.getString(R.string.dialog_type_image) };
            dialogBuilder.setItems(menuItemArray,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String selectType = "*/*";
                            switch (which) {
                            case 0:
                                selectType = "text/plain";
                                break;
                            case 1:
                                selectType = "audio/*";
                                break;
                            case 2:
                                selectType = "video/*";
                                break;
                            case 3:
                                selectType = "image/*";
                                break;
                            }
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(android.content.Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            intent.setDataAndType(getUriForFile(context, new File(filePath)), selectType);
                            context.startActivity(intent);
                        }
                    });
            dialogBuilder.show();
        }
    }

    public static Intent buildSendFile(ArrayList<FileInfo> files) {
        ArrayList<Uri> uris = new ArrayList<Uri>();

        String mimeType = "*/*";
        for (FileInfo file : files) {
            if (file.IsDir)
                continue;

            File fileIn = new File(file.filePath);
            mimeType = getMimeType(file.fileName);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }

        if (uris.size() == 0)
            return null;

        boolean multiple = uris.size() > 1;
        Intent intent = new Intent(multiple ? android.content.Intent.ACTION_SEND_MULTIPLE
                : android.content.Intent.ACTION_SEND);

        if (multiple) {
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        } else {
            intent.setType(mimeType);
            intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
        }

        return intent;
    }

    private static String getMimeType(String filePath) {
        int dotPosition = filePath.lastIndexOf('.');
        if (dotPosition == -1)
            return "*/*";

        String ext = filePath.substring(dotPosition + 1, filePath.length()).toLowerCase();
        String mimeType = MimeUtils.guessMimeTypeFromExtension(ext);
        if (ext.equals("mtz")) {
            mimeType = "application/miui-mtz";
        }

        return mimeType != null ? mimeType : "*/*";
    }

    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(),context.getApplicationContext().getPackageName() + ".file_provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

}
