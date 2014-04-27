package com.probytemedia.amazingfacts.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.probytemedia.amazingfacts.R;
import com.probytemedia.amazingfacts.models.FavModel;
import com.probytemedia.amazingfacts.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by AABID on 19/4/14.
 */
public class FavoritesListFragment extends Fragment {

    private View parentView;
    @InjectView(R.id.listView)
    ListView listView;

    private List<FavModel> favoriteList;
    private ArrayList<String> localQuoteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fr_favorite_layout, container, false);
        ButterKnife.inject(this,parentView);
        return parentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setCacheColorHint(Color.TRANSPARENT);
        localQuoteList = AppConstants.getAllList();
        refreshListView();
    }

    private void refreshListView() {
        favoriteList = FavModel.find(FavModel.class, " favorites = ? ", String.valueOf(true));
        if (favoriteList.size() > 0) {
            listView.setAdapter(new CarouselListAdapter(getActivity()));
        } else {
            listView.setAdapter(new EmptyAdaptor(getActivity()));
        }
        List<FavModel> allFavoriteList= FavModel.listAll(FavModel.class);
        AppConstants.favListDB=allFavoriteList;
        parentView.requestLayout();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private class CarouselListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int lastPosition = -1;
        private Context mContext;

        public CarouselListAdapter(Context context) {
            this.mContext = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final int favPosition = favoriteList.get(position).getPosition();
            View view = null;
            if (convertView == null) {
                view = inflater.inflate(R.layout.inflater_main_listview, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.quoteTxt = (TextView) view.findViewById(R.id.textView);
                viewHolder.shareButton = (ImageButton) view.findViewById(R.id.share_button);
                viewHolder.favButton = (ImageButton) view.findViewById(R.id.fav_button);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.quoteTxt.setText(localQuoteList.get(favPosition));
            holder.favButton.setSelected(true);

            holder.favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<FavModel> tempFavObj = FavModel.find(FavModel.class, " position = ?", String.valueOf(favPosition));
                    FavModel favObj = tempFavObj.get(0);
                    favObj.setFavorites(false);
                    favObj.save();
                    AppConstants.favListDB.get(favPosition).setFavorites(false);
                    refreshListView();
                    holder.favButton.setSelected(false);
                }
            });

            holder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("text/plain");
                    shareIntent
                            .putExtra(
                                    Intent.EXTRA_TEXT,
                                    "\"" + localQuoteList.get(favoriteList.get(position).getPosition()) + "\"");
                    startActivity(shareIntent);
                }
            });


            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            view.startAnimation(animation);
            lastPosition = position;
            return view;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return favoriteList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }


    private static final class ViewHolder {
        TextView quoteTxt;
        ImageButton shareButton, favButton;
        RelativeLayout emptyLinear;
    }


    class EmptyAdaptor extends BaseAdapter {

        private LayoutInflater inflater;
        private int lastPosition = -1;
        private Context mContext;

        public EmptyAdaptor(Context context) {
            this.mContext = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = null;
            if (convertView == null) {
                view = inflater.inflate(R.layout.inflater_empty_layou, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.quoteTxt = (TextView) view.findViewById(R.id.textView);
                viewHolder.emptyLinear = (RelativeLayout) view.findViewById(R.id.main_empty_layout);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            final ViewHolder holder = (ViewHolder) view.getTag();
            holder.quoteTxt.setText("You have No Favorites.");
            Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
            view.startAnimation(animation);
            lastPosition = position;
            return view;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 1;
        }
    }

}
