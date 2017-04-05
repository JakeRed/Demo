package com.kaihong.demo.entity;

import java.io.Serializable;

/**
 * Created by kaihong.zheng on 2017/4/3.
 */

public class AnimatorEntity implements Serializable {
    private String title;
    private String desc;

    public AnimatorEntity(String title, String desc) {
        this.title=title;
        this.desc=desc;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
