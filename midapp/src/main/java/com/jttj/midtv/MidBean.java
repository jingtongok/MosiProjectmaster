package com.jttj.midtv;

import android.text.TextUtils;
import android.util.Log;

import com.tuacy.phonedemo.HanziToPinyin;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @content: Author: gjt66888
 * Description:
 * Time: 2019/6/8
 */
public class MidBean extends DataSupport implements Serializable {
    /**
     * mid.jks 文件路径地址
     */
    private String midAddress;

    private int midImg;
    /**
     * mid名字首字母
     */
    private String headChar;

    /**
     * mid名字
     */
    private String name;
    /**
     * mid字母名字
     */
    private String name_en;
    /**
     * 是否是标题
     */
    private int type;


    public String getMidAddress() {
        return midAddress;
    }

    public void setMidAddress(String midAddress) {
        this.midAddress = midAddress;
    }

    public int getMidImg() {
        return midImg;
    }

    public void setMidImg(int midImg) {
        this.midImg = midImg;
    }

    public String getHeadChar() {
        return headChar;
    }

    public String getName() {
        return name;
    }

    public String getName_en() {
        return name_en;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
        name_en = getPinYin(name);//获取字母名称
        name_en = name_en.toUpperCase();//把小写字母换成大写字母

        Log.i(StrUtils.TAG, "name_en:" + name_en);
        if (!TextUtils.isEmpty(name_en)) {
            char head = name_en.charAt(0);
            if (head < 'A' || head > 'Z') {
                head = '#';
            }
            headChar = head + "";
        }
    }

    /**
     * 汉字转换拼音，字母原样返回，都转换为小写
     */
    public String getPinYin(String input) {
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(input);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (token.type == HanziToPinyin.Token.PINYIN) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }


        return sb.toString().toLowerCase();


    }
}
