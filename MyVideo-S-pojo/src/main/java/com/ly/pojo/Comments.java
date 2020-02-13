package com.ly.pojo;

import java.util.Date;
import javax.persistence.*;

public class Comments {
    @Id
    private String id;

    @Column(name = "father_comment_id")
    private String fatherCommentId;

    @Column(name = "to_user_id")
    private String toUserId;

    /**
     * 视频ID
     */
    @Column(name = "video_id")
    private String videoId;

    /**
     * 留言用户的ID
     */
    @Column(name = "from_user_id")
    private String fromUserId;

    /**
     * 评论的时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 评论内容
     */
    private String comment;

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
     * @return father_comment_id
     */
    public String getFatherCommentId() {
        return fatherCommentId;
    }

    /**
     * @param fatherCommentId
     */
    public void setFatherCommentId(String fatherCommentId) {
        this.fatherCommentId = fatherCommentId;
    }

    /**
     * @return to_user_id
     */
    public String getToUserId() {
        return toUserId;
    }

    /**
     * @param toUserId
     */
    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    /**
     * 获取视频ID
     *
     * @return video_id - 视频ID
     */
    public String getVideoId() {
        return videoId;
    }

    /**
     * 设置视频ID
     *
     * @param videoId 视频ID
     */
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    /**
     * 获取留言用户的ID
     *
     * @return from_user_id - 留言用户的ID
     */
    public String getFromUserId() {
        return fromUserId;
    }

    /**
     * 设置留言用户的ID
     *
     * @param fromUserId 留言用户的ID
     */
    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    /**
     * 获取评论的时间
     *
     * @return create_time - 评论的时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置评论的时间
     *
     * @param createTime 评论的时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取评论内容
     *
     * @return comment - 评论内容
     */
    public String getComment() {
        return comment;
    }

    /**
     * 设置评论内容
     *
     * @param comment 评论内容
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}