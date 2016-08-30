package com.piczler.piczler;


import android.graphics.Bitmap;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;

import ly.img.android.sdk.configuration.AbstractConfig;

/**
 * Created by matiyas on 11/19/15.
 */
public class GettersAndSetters{
    String name;
    int followers;
    long id;
    String cover;
    boolean followed;
    boolean loading;
    String jsonString;
      int type;
    RelativeLayout rlout;
    RippleView rpV;
    TextView txtV;
    boolean click;
    String from;
    Bitmap bitmap;
    String imageUrl;
    String comment;
    String serverID;
    String commentID;
    String nameWithAt;
    int layouttype;
    int contenttype;
    int position;
    boolean showIcon;
    boolean selected;
    String phoneNum;
    String code;
    int fileType;
    int counter;




    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isShowIcon() {
        return showIcon;
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    AbstractConfig.ImageFilterInterface filter;


    public AbstractConfig.ImageFilterInterface getFilter() {
        return filter;
    }

    public void setFilter(AbstractConfig.ImageFilterInterface filter) {
        this.filter = filter;
    }

    public int getLayouttype() {
        return layouttype;
    }

    public void setLayouttype(int layouttype) {
        this.layouttype = layouttype;
    }

    public int getContenttype() {
        return contenttype;
    }

    public void setContenttype(int contenttype) {
        this.contenttype = contenttype;
    }

    public String getNameWithAt() {
        return nameWithAt;
    }

    public void setNameWithAt(String nameWithAt) {
        this.nameWithAt = nameWithAt;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public boolean isClick() {
        return click;
    }




    public void setClick(boolean click) {
        this.click = click;
    }

    public RelativeLayout getRlout() {
        return rlout;
    }

    public void setRlout(RelativeLayout rlout) {
        this.rlout = rlout;
    }

    public RippleView getRpV() {
        return rpV;
    }

    public void setRpV(RippleView rpV) {
        this.rpV = rpV;
    }

    public TextView getTxtV() {
        return txtV;
    }

    public void setTxtV(TextView txtV) {
        this.txtV = txtV;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }





}
