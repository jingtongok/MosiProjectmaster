package com.jttj.midtv;

import java.text.Collator;
import java.util.Comparator;

/**
 * @content: Author: gjt66888
 * Description:
 * Time: 2019/6/8
 */
public class MemberSortUtil implements Comparator<MidBean> {
    /**
     * 按拼音排序
     */
    @Override
    public int compare(MidBean lhs, MidBean rhs) {
        Comparator<Object> cmp = Collator
                .getInstance(java.util.Locale.CHINA);
        return cmp.compare(lhs.getName_en(), rhs.getName_en());
    }
}