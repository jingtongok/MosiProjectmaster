package com.jttj.midtv;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * @content: Author: gjt66888
 * Description:
 * Time: 2019/7/14
 */
public class WeChartActivity extends AppCompatActivity {

    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wechart);

        toolbar = findViewById(R.id.wechart_paly_toolbar);

        // 主标题
        toolbar.setTitle("打赏");

        toolbar.setNavigationIcon(R.drawable.ic_return);
        //点击左边返回按钮监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//返回
            }
        });
    }
}
