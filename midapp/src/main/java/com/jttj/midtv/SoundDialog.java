package com.jttj.midtv;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @content: Author: gjt66888
 * Description:
 * Time: 2019/6/18
 */
public class SoundDialog {

    private Context context;
    private Dialog dialog;
    private Display display;

    private TextView sound001;
    private TextView sound002;
    private TextView sound003;
    private TextView sound004;

    private LinearLayout lLayout_bg;


    public SoundDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    /**
     * diolog
     *
     * @return
     */
    public SoundDialog builder() {
        // 获取Dialog布局
        View weatherPopview = LayoutInflater.from(context).inflate(R.layout.pop_sound, null);

        sound001 = weatherPopview.findViewById(R.id.sound_01);
        sound002 = weatherPopview.findViewById(R.id.sound_02);
        sound003 = weatherPopview.findViewById(R.id.sound_03);
        sound004 = weatherPopview.findViewById(R.id.sound_04);

        lLayout_bg = weatherPopview.findViewById(R.id.lLayout_bg);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(weatherPopview);
        //设置点击屏幕不消失
        dialog.setCanceledOnTouchOutside(true);
        //设置点击返回键不消失
        dialog.setCancelable(true);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
                .getWidth() * 0.8), LinearLayout.LayoutParams.WRAP_CONTENT));

        return this;
    }

    public SoundDialog setSound01Button(final View.OnClickListener listener) {
        sound001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public SoundDialog setSound02Button(final View.OnClickListener listener) {
        sound002.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public SoundDialog setSound03Button(final View.OnClickListener listener) {
        sound003.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public SoundDialog setSound04Button(final View.OnClickListener listener) {
        sound004.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}

