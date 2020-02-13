package com.ly.pojo;

import javax.persistence.*;

public class Bgm {
    @Id
    private String id;

    /**
     * 作者
     */
    private String author;

    /**
     * bgm名字
     */
    private String name;

    /**
     * 播放地址
     */
    private String path;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取作者
     *
     * @return author - 作者
     */
    public String getAuthor() {
        return author;
    }

    /**
     * 设置作者
     *
     * @param author 作者
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * 获取bgm名字
     *
     * @return name - bgm名字
     */
    public String getName() {
        return name;
    }

    /**
     * 设置bgm名字
     *
     * @param name bgm名字
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取播放地址
     *
     * @return path - 播放地址
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置播放地址
     *
     * @param path 播放地址
     */
    public void setPath(String path) {
        this.path = path;
    }
}