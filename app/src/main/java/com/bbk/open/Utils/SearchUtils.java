package com.bbk.open.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.MediaStore;

import com.bbk.open.model.FileInfo;

import java.util.ArrayList;
import java.util.List;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * 搜索工具类
 * Created by Administrator on 2016/7/13.
 */
public class SearchUtils {

    /**
     * 搜索结果返回所有应用
     *
     * @param context
     * @return
     */
    public static List<FileInfo> SearchApps(Context context) {
        List<FileInfo> appList = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            Drawable icon = resolveInfo.loadIcon(context.getPackageManager());
            String iconstr = (DrawableAndString.drawableToByte(icon));
            String name = resolveInfo.loadLabel(context.getPackageManager()).toString();
            String path = resolveInfo.activityInfo.packageName;
            String nameToPinyin = PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE);
            FileInfo info = new FileInfo(name, path, nameToPinyin, iconstr, FileInfo.TYPE_APP);
            appList.add(info);
        }
        return appList;
    }

    /**
     * 搜索结果返回所有联系人
     *
     * @param context
     * @return
     */
    public static List<FileInfo> SearchContacts(Context context) {
        List<FileInfo> contactList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(Contacts.CONTENT_URI, new String[]{Contacts._ID, Contacts.DISPLAY_NAME}, null, null, null);
        while (cursor.moveToNext()) {
            String phoneNumber = "";
            int _id = cursor.getInt(0);
            String name = cursor.getString(1);
            String nameToPinyin = PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE);
            Cursor phoneNumberCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{"data1"}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "==" + _id, null, null);
            while (phoneNumberCursor.moveToNext()) {
                phoneNumber = phoneNumber + " " + phoneNumberCursor.getString(0);
            }
            FileInfo info = new FileInfo(name+"|"+nameToPinyin, _id + "", phoneNumber.trim(),"", FileInfo.TYPE_CONTACT);
            contactList.add(info);
            phoneNumberCursor.close();
        }
        cursor.close();
        return contactList;
    }

    /**
     * 搜索结果返回所有短信
     *
     * @param context
     * @return
     */
    public static List<FileInfo> SearchSms(Context context) {
        Uri uri = Uri.parse("content://sms/");
        List<FileInfo> smsList = new ArrayList<>();
        String[] projection = {"_id", "address", "body"};
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        while (cursor.moveToNext()) {
            int _id = cursor.getInt(0);
            String path = cursor.getString(1);
            String body = cursor.getString(2);
            String name = "";
            Uri personUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                    Uri.encode(path));
            Cursor cur = context.getContentResolver().query(personUri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if (cur.moveToFirst()) {
                name = cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                cur.close();
                FileInfo info = new FileInfo(name, path, path, body, FileInfo.TYPE_SMS);
                smsList.add(info);
            }
        }
        cursor.close();
        return smsList;
    }


    /**
     * 搜索结果返回所有图片
     *
     * @param context
     * @return
     */
    public static List<FileInfo> SearchImages(Context context) {
        List<FileInfo> imageList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor thumbnailCursor = resolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA}, null, null, null);
        while (thumbnailCursor.moveToNext()) {
            int _id = thumbnailCursor.getInt(0);
            String thumbnailPath = thumbnailCursor.getString(1);
            Cursor imageCursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME}, MediaStore.Images.Media._ID + "=" + _id, null, null);
            if (imageCursor != null) {
                imageCursor.moveToFirst();
                String imagePath = imageCursor.getString(1);
                String name = imageCursor.getString(2);
                FileInfo info = new FileInfo(name,imagePath,"",thumbnailPath,FileInfo.TYPE_IMAGE);
                imageList.add(info);
                imageCursor.close();
            }
        }
        thumbnailCursor.close();
        return imageList;
    }

    /**
     * 搜索结果返回所有视频
     *
     * @param context
     * @return
     */
    public static List<FileInfo> SearchVedio(Context context) {
        List<FileInfo> videoList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor videoCursor = resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME}, null, null, null);
        while (videoCursor.moveToNext()) {
            int _id = videoCursor.getInt(0);
            String videoPath = videoCursor.getString(1);
            String name = videoCursor.getString(2);
            Cursor thumbnailsCursor = resolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Video.Thumbnails.VIDEO_ID, MediaStore.Video.Thumbnails.DATA}, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + _id, null, null);
            if (thumbnailsCursor != null) {
                thumbnailsCursor.moveToFirst();
                String thumbnailPath = thumbnailsCursor.getString(1);
                FileInfo info = new FileInfo(name,videoPath,"",thumbnailPath,FileInfo.TYPE_VIDEO);
                videoList.add(info);
                thumbnailsCursor.close();
            }
        }
        videoCursor.close();
        return videoList;
    }

    /**
     * 搜索结果返回所有音频
     *
     * @param context
     * @return
     */
    public static List<FileInfo> SearchAudio(Context context) {
        List<FileInfo> audioList = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        Cursor audioCursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null, null);
        while (audioCursor.moveToNext()) {
            int _id = audioCursor.getInt(0);
            String name = audioCursor.getString(1);
            String path = audioCursor.getString(2);
            String nameToPinyin = "";
            if (name != null) {
                nameToPinyin = PinyinHelper.convertToPinyinString(name, "", PinyinFormat.WITHOUT_TONE);
                FileInfo info = new FileInfo(name,path,nameToPinyin,"",FileInfo.TYPE_AUDIO);
                audioList.add(info);
            }
        }
        audioCursor.close();
        return audioList;
    }

    /**
     * 搜索结果返回后缀名为zip，apk，txt，pdf，word，xml，html文件的集合，跟据type区分不同
     *
     * @param context
     * @return
     */
    public static List<FileInfo> SearchPiecemealInfo(Context context) {
        List<FileInfo> piecemealInfos = new ArrayList<>();
        String[] projection = new String[]{MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE
        };
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://media/external/file"),
                projection,
                MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ? or "
                        + MediaStore.Files.FileColumns.DATA + " like ?",
                new String[]{"%.zip", "%.apk", "%.txt", "%.pdf", "%.word", "%.xls", "%.ppt", "%.html"},
                null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int idindex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns._ID);
                int dataindex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int sizeindex = cursor
                        .getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                do {
                    String id = cursor.getString(idindex);
                    String path = cursor.getString(dataindex);
                    String size = cursor.getString(sizeindex);
                    int dot = path.lastIndexOf("/");
                    String name = path.substring(dot + 1);
                    FileInfo info= new FileInfo(name,path,"","",ConvertUtil.convertType(name));
                    piecemealInfos.add(info);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return piecemealInfos;
    }

}
