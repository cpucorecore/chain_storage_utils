package com.ancun.chain_storage.requests;

import com.alibaba.fastjson.annotation.JSONField;

public class Metadata {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public Metadata(String name, long tokenId, String description, String image) {
        this.name = name;
        this.tokenId = tokenId;
        this.description = description;
        this.image = image;
    }

    @JSONField(ordinal = 1)
    private String name;

    @JSONField(ordinal = 2)
    private long tokenId;

    @JSONField(ordinal = 3)
    private String description;

    @JSONField(ordinal = 4)
    private String image;

    @JSONField(ordinal = 5)
    private String ext;
}
