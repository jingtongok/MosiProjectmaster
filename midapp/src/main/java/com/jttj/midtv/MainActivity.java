package com.jttj.midtv;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.jttj.sheet.FileUri;
import com.tencent.bugly.beta.Beta;
import com.tuacy.phonedemo.LetterIndexView;
import com.tuacy.phonedemo.PinnedSectionListView;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jttj.midtv.StrUtils.bytesToHexString;
import static yin.source.com.midimusicbook.midi.musicBook.MidiPlayer.PlayState.PAUSED;
import static yin.source.com.midimusicbook.midi.musicBook.MidiPlayer.PlayState.PLAYING;
import static yin.source.com.midimusicbook.midi.musicBook.MidiPlayer.PlayState.STOPPED;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mid_toolbar)
    Toolbar toolbar;
    @BindView(R.id.mid_001)
    ImageView mid001;
    @BindView(R.id.mid_002)
    ImageView mid002;
    @BindView(R.id.mid_003)
    ImageView mid003;

    /**
     * 搜索栏
     */
    @BindView(R.id.mid_edit_search)
    EditText edit_search;
    /**
     * 列表
     */
    @BindView(R.id.mid_listview)
    PinnedSectionListView listView;
    /**
     * 右边字母列表
     */
    @BindView(R.id.mid_LetterIndexView)
    LetterIndexView letterIndexView;
    /**
     * 中间显示右边按的字母
     */
    @BindView(R.id.mid_txt_center)
    TextView txt_center;
    /**
     * 列表是否显示
     */
    @BindView(R.id.mid_rel)
    RelativeLayout mid_rel;

    /**
     * 所有名字集合
     */
    private List<MidBean> list_all;
    /**
     * 显示名字集合
     */
    private ArrayList<MidBean> list_show;
    /**
     * 列表适配器
     */
    private MidAdapter adapter;
    /**
     * 保存名字首字母
     */
    public HashMap<String, Integer> map_IsHead;
    /**
     * item标识为0
     */
    public static final int ITEM = 0;
    /**
     * item标题标识为1
     */
    public static final int TITLE = 1;

    public static final int REQUEST_TAKE_PHOTO_CAMERA = 5;
    // 获取系统版本号
    private static String[] PERMISSIONS_CAMERA_AND_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, //存储权限
            Manifest.permission.MODIFY_AUDIO_SETTINGS //声音控制
    };

    SharedPreferences sp;

    /**
     * 判断 是否获取存储
     */
    private boolean canmer() {
        int storagePermission = ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int retmbaPermission = ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int volumePermission = ContextCompat.checkSelfPermission(MyApp.getContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS);

        if (storagePermission != PackageManager.PERMISSION_GRANTED || retmbaPermission != PackageManager.PERMISSION_GRANTED
                || volumePermission != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mContext = MainActivity.this;

        /***** 检查更新 *****/
        Beta.checkUpgrade();

        sp = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);

        initView();

        //获取权限
        if (canmer()) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_CAMERA_AND_STORAGE,
                    REQUEST_TAKE_PHOTO_CAMERA);
        }
        // 静音的权限
        getDoNotDisturb();

        initClick();

        initData();

    }

    /**
     * 初始化页面
     */
    private void initView() {

        // Logo
        toolbar.setLogo(R.drawable.ic_launcher);

        // 主标题
        toolbar.setTitle(this.getResources().getString(R.string.app_names));


        //加载默认音乐
        String bl = sp.getString("MID", "mid.jks");

        if (bl.equals("mid.jks")) {
            initDB();
            sp.edit().putString("MID", "end").commit();
        }

    }

    /**
     * 加载默认音乐
     */
    private void initDB() {

        File fileFromAssets0 = FileUri.getFileFromAssets("示例音乐1.mid", this);
        MidBean mMidBean0 = new MidBean();
        mMidBean0.setName("示例音乐1");
        mMidBean0.setMidAddress(fileFromAssets0.getPath());

        addDate(mMidBean0);

        File fileFromAssets1 = FileUri.getFileFromAssets("示例音乐2.MID", this);
        MidBean mMidBean1 = new MidBean();
        mMidBean1.setName("示例音乐2");
        mMidBean1.setMidAddress(fileFromAssets1.getPath());

        addDate(mMidBean1);

        File fileFromAssets2 = FileUri.getFileFromAssets("示例音乐3.MID", this);
        MidBean mMidBean2 = new MidBean();
        mMidBean2.setName("示例音乐3");
        mMidBean2.setMidAddress(fileFromAssets2.getPath());

        addDate(mMidBean2);

        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.mid_003), "添加乐谱", "下载.mid格式音乐文件到手机，点击添加进app即可播放")
                        // All options below are optional
                        .outerCircleColor(R.color.col_ffffff)           //指定外圆的颜色
                        .outerCircleAlpha(0.96f)                 //指定外圆的alpha值
                        .targetCircleColor(R.color.colorAccent)   //指定目标圆的颜色
                        .titleTextSize(20)                       //指定标题文本的大小（以sp为单位）
                        .titleTextColor(R.color.colorAccent)      //指定标题文本的颜色
                        .descriptionTextSize(10)                 //指定描述文本的大小（以sp为单位）
                        .descriptionTextColor(R.color.col_ffffff)       //指定描述文本的颜色
                        .textColor(R.color.colorAccent)           //指定标题和说明文本的颜色
                        .dimColor(R.color.colorAccent)           //如果已设置，则在视图后面变暗，不透明度为给定颜色的30%
                        .drawShadow(true)                        // 是否绘制投影
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
//                        doSomething();
                    }
                });
    }

    protected void initData() {

        list_all = new ArrayList<MidBean>();
        list_show = new ArrayList<MidBean>();
        map_IsHead = new HashMap<String, Integer>();
        adapter = new MidAdapter(MainActivity.this, list_show, map_IsHead);
        listView.setAdapter(adapter);
        listView.setShadowVisible(true);

        // 开启异步加载数据
        new Thread(runnable).start();
    }

    /**
     * 初始化列表
     */
    private void initClick() {
        // 输入监听
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i,
                                          int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1,
                                      int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                list_show.clear();
                map_IsHead.clear();
                //把输入的字符改成大写
                String search = editable.toString().trim().toUpperCase();

                if (TextUtils.isEmpty(search)) {
                    for (int i = 0; i < list_all.size(); i++) {
                        MidBean bean = list_all.get(i);
                        //中文字符匹配首字母和英文字符匹配首字母
                        if (!map_IsHead.containsKey(bean.getHeadChar())) {// 如果不包含就添加一个标题
                            MidBean bean1 = new MidBean();
                            // 设置名字
                            bean1.setName(bean.getName());
                            // 设置标题type
                            bean1.setType(MainActivity.TITLE);
                            list_show.add(bean1);
                            // map的值为标题的下标
                            map_IsHead.put(bean1.getHeadChar(),
                                    list_show.size() - 1);
                        }
                        // 设置Item type
                        bean.setType(MainActivity.ITEM);
                        list_show.add(bean);
                    }
                } else {
                    for (int i = 0; i < list_all.size(); i++) {
                        MidBean bean = list_all.get(i);
                        //中文字符匹配首字母和英文字符匹配首字母
                        if (bean.getName().indexOf(search) != -1 || bean.getName_en().indexOf(search) != -1) {
                            if (!map_IsHead.containsKey(bean.getHeadChar())) {// 如果不包含就添加一个标题
                                MidBean bean1 = new MidBean();
                                // 设置名字
                                bean1.setName(bean.getName());
                                // 设置标题type
                                bean1.setType(MainActivity.TITLE);
                                list_show.add(bean1);
                                // map的值为标题的下标
                                map_IsHead.put(bean1.getHeadChar(),
                                        list_show.size() - 1);
                            }
                            // 设置Item type
                            bean.setType(MainActivity.ITEM);
                            list_show.add(bean);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        // 右边字母竖排的初始化以及监听
        letterIndexView.init(new LetterIndexView.OnTouchLetterIndex() {
            //实现移动接口
            @Override
            public void touchLetterWitch(String letter) {
                // 中间显示的首字母
                txt_center.setVisibility(View.VISIBLE);
                txt_center.setText(letter);
                // 首字母是否被包含
                if (adapter.map_IsHead.containsKey(letter)) {
                    // 设置首字母的位置
                    listView.setSelection(adapter.map_IsHead.get(letter));
                }
            }

            //实现抬起接口
            @Override
            public void touchFinish() {
                txt_center.setVisibility(View.GONE);
            }
        });

        /** listview点击事件 */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (list_show.get(i).getType() == MainActivity.ITEM) {// 标题点击不给操作
//                    Toast.makeText(MainActivity.this, list_show.get(i).getMidAddress(), Toast.LENGTH_LONG).show();

                    StrUtils.showToast("正在加载中，请稍后...");

                    // 第二种方法¬
                    Intent intent = new Intent(mContext, MidPalyActivity.class);
                    intent.putExtra(StrUtils.MidiTitleID, list_show.get(i).getMidAddress());
                    intent.putExtra(StrUtils.MidiTitleName, list_show.get(i).getName());
//                    startActivity(intent);
                    startActivityForResult(intent, 1314);
                }
            }
        });
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (StrUtils.isListEmpty(list_all)) {
                mid_rel.setVisibility(View.GONE);
                txt_center.setVisibility(View.VISIBLE);
            } else {
                mid_rel.setVisibility(View.VISIBLE);
                txt_center.setVisibility(View.GONE);

//            Collections.reverse(list_all);

                Log.i(StrUtils.TAG, "sum:" + list_all.size());

            }
            adapter.notifyDataSetChanged();
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!StrUtils.isListEmpty(list_all)) {
                list_all.clear();
            }
            if (!StrUtils.isListEmpty(list_show)) {
                list_show.clear();
            }
            if (!StrUtils.isHashEmpty(map_IsHead)) {
                map_IsHead.clear();
            }
            initLatileDate();

            Log.i(StrUtils.TAG, "list_all:" + list_all.size());

            //按拼音排序
            MemberSortUtil sortUtil = new MemberSortUtil();
            Collections.sort(list_all, sortUtil);

            // 初始化数据，顺便放入把标题放入map集合
            for (int i = 0; i < list_all.size(); i++) {
                MidBean cityBean = list_all.get(i);
                if (!map_IsHead.containsKey(cityBean.getHeadChar())) {// 如果不包含就添加一个标题
                    MidBean cityBean1 = new MidBean();
                    // 设置名字
                    cityBean1.setName(cityBean.getName());
                    cityBean1.setMidAddress(cityBean.getMidAddress());
                    // 设置标题type
                    cityBean1.setType(MainActivity.TITLE);
                    list_show.add(cityBean1);

                    // map的值为标题的下标
                    map_IsHead.put(cityBean1.getHeadChar(), list_show.size() - 1);
                }
                list_show.add(cityBean);
            }

            handler.sendMessage(handler.obtainMessage());

        }
    };


    //获取Do not disturb权限,才可进行音量操作
    private void getDoNotDisturb() {
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && !notificationManager.isNotificationPolicyAccessGranted()) {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }
    }

    /**
     * ************************************数据库操作*******************************
     */
    /**
     * 初始化数据，读取数据库中数据
     */
    private void initLatileDate() {

        list_all = DataSupport.findAll(MidBean.class);

    }

    /**
     * 添加数据库中数据
     */
    private void addDate(MidBean mMidBean) {

        List<MidBean> mSearchList = DataSupport.where("midAddress=?", mMidBean.getMidAddress()).find(MidBean.class); //读取content的数据
        if (StrUtils.isListEmpty(mSearchList)) { //看数据库中是否存在，
            mMidBean.save();
            return;
        }

        StrUtils.showToast("文件已存在");
    }

    /***
     * **************************************顶部*****************************************
     * @param view
     */

    @OnClick({R.id.mid_001, R.id.mid_002, R.id.mid_003})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.mid_001:

                startActivity(new Intent(mContext, WeChartActivity.class));

                break;
            case R.id.mid_002:
                Log.i(StrUtils.TAG, "2222222");
                break;
            case R.id.mid_003:

                if (canmer()) {
                    //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_CAMERA_AND_STORAGE,
                            REQUEST_TAKE_PHOTO_CAMERA);
                } else {
                    showFileChooser();
                }
                break;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, RESULT_CANCELED);
        } catch (android.content.ActivityNotFoundException ex) {
            StrUtils.showToast("请先安装文件管理器");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CANCELED && resultCode == RESULT_OK && data != null) {
//            Uri uri = data.getData();
//            String path = StrUtils.getPath(this, uri);
            Uri uri = data.getData();
            if (uri != null) {
                String path = getPath(this, uri);
                if (path != null) {
                    File file = new File(path);
                    if (file.exists()) {
                        String upLoadFilePath = file.toString();
                        String upLoadFileName = file.getName();

                        Log.i(StrUtils.TAG, "地址是：" + upLoadFilePath);
                        Log.i(StrUtils.TAG, "名称：" + upLoadFileName);

                        FileInputStream is = null;
                        String photo = "";
                        try {
                            is = new FileInputStream(upLoadFilePath);
                            byte[] b = new byte[3];
                            is.read(b, 0, b.length);
                            photo = bytesToHexString(b);
                        } catch (Exception e) {
                        }
//                        String name = TypeDict.checkType(photo);
                        Log.i(StrUtils.TAG, photo);
                        if (!"4d5468".equals(photo)) {
                            StrUtils.showToast("文件格式错误，请重新选择");
                            return;
                        }

                        MidBean mMidBean = new MidBean();
                        mMidBean.setName(upLoadFileName);
                        mMidBean.setMidAddress(upLoadFilePath);
                        addDate(mMidBean);

                        // 开启异步刷新数据
                        new Thread(runnable).start();
                    }

                }
            }

        } else if (requestCode == 1314 && resultCode == 520) {
            // 开启异步刷新数据
            new Thread(runnable).start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
//                Log.i(TAG,"isExternalStorageDocument***"+uri.toString());
//                Log.i(TAG,"docId***"+docId);
//                以下是打印示例：
//                isExternalStorageDocument***content://com.android.externalstorage.documents/document/primary%3ATset%2FROC2018421103253.wav
//                docId***primary:Test/ROC2018421103253.wav
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
//                Log.i(TAG,"isDownloadsDocument***"+uri.toString());
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
//                Log.i(TAG,"isMediaDocument***"+uri.toString());
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"content***"+uri.toString());
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            Log.i(TAG,"file***"+uri.toString());
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
