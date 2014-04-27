package com.probytemedia.amazingfacts.models;

import android.content.Context;

import com.orm.SugarRecord;

/**
 * Created by AABID on 19/4/14.
 */
public class FavModel extends SugarRecord<FavModel>{

    private int position;
    private boolean favorites;

    public FavModel(Context context) {
        super(context);
    }

    public FavModel(Context context, int position, boolean favorites) {
        super(context);
        this.favorites=favorites;
        this.position=position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isFavorites() {
        return favorites;
    }

    public void setFavorites(boolean favorites) {
        this.favorites = favorites;
    }
}
