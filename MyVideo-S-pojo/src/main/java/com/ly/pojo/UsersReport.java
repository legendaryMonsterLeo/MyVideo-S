package com.ly.pojo;

import java.util.Date;
import javax.persistence.*;

@Table(name = "users_report")
public class UsersReport {
    @Id
    private String id;

    /**
     * 被举报的用户ID
     */
    @Column(name = "deal_user_id")
    private String dealUserId;

    /**
     * 被举报视频的ID
     */
    @Column(name = "deal_video_id")
    private String dealVideoId;

    /**
     * 类型标题，让用户选择，详情见 枚举
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 被举报人的ID
     */
    private String userid;

    /**
     * 举报时间
     */
    @Column(name = "create_date")
    private Date createDate;

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
     * 获取被举报的用户ID
     *
     * @return deal_user_id - 被举报的用户ID
     */
    public String getDealUserId() {
        return dealUserId;
    }

    /**
     * 设置被举报的用户ID
     *
     * @param dealUserId 被举报的用户ID
     */
    public void setDealUserId(String dealUserId) {
        this.dealUserId = dealUserId;
    }

    /**
     * 获取被举报视频的ID
     *
     * @return deal_video_id - 被举报视频的ID
     */
    public String getDealVideoId() {
        return dealVideoId;
    }

    /**
     * 设置被举报视频的ID
     *
     * @param dealVideoId 被举报视频的ID
     */
    public void setDealVideoId(String dealVideoId) {
        this.dealVideoId = dealVideoId;
    }

    /**
     * 获取类型标题，让用户选择，详情见 枚举
     *
     * @return title - 类型标题，让用户选择，详情见 枚举
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置类型标题，让用户选择，详情见 枚举
     *
     * @param title 类型标题，让用户选择，详情见 枚举
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取内容
     *
     * @return content - 内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取被举报人的ID
     *
     * @return userid - 被举报人的ID
     */
    public String getUserid() {
        return userid;
    }

    /**
     * 设置被举报人的ID
     *
     * @param userid 被举报人的ID
     */
    public void setUserid(String userid) {
        this.userid = userid;
    }

    /**
     * 获取举报时间
     *
     * @return create_date - 举报时间
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 设置举报时间
     *
     * @param createDate 举报时间
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}