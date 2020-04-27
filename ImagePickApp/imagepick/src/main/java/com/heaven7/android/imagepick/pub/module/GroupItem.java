package com.heaven7.android.imagepick.pub.module;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.java.visitor.collection.KeyValuePair;

import java.util.List;

public class GroupItem extends BaseSelector {

    private final KeyValuePair<String, List<IImageItem>> pair;

    public GroupItem(KeyValuePair<String, List<IImageItem>> pair) {
        this.pair = pair;
    }
    public KeyValuePair<String, List<IImageItem>> getPair(){
        return pair;
    }

    public String getGroupName() {
        return pair.getKey();
    }
    public List<IImageItem> getItems() {
        return pair.getValue();
    }
}