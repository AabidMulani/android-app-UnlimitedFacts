package com.probytemedia.amazingfacts.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.probytemedia.amazingfacts.activity.MainActivity;
import com.probytemedia.amazingfacts.models.FavModel;
import com.probytemedia.amazingfacts.utils.AppConstants;
import com.probytemedia.unlimitedfacts.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by AABID on 20/4/14.
 */
public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener {
    @InjectView(R.id.pager)
    ViewPager pager;
    private ArrayList<String> quoteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_mainfragment_layout, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        quoteList = AppConstants.getAllList();
        pager.setAdapter(new QuotesAdapter(getActivity()));
        pager.setCurrentItem(MainActivity.previousSelection);
        pager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MainActivity.previousSelection = position;
        if (position % 7 == 0) {
            ((MainActivity) getActivity()).showAdBuzzAds();
        }
        if (position % 10 == 0) {
            ((MainActivity) getActivity()).showInterstitial();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class QuotesAdapter extends PagerAdapter {
        private LayoutInflater inflater;
        private Context context;

        public QuotesAdapter(Context context) {
            this.context = context;
            this.inflater = (LayoutInflater) context.getSystemService(Context
                    .LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return quoteList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.inflater_pager_listview, container, false);
            final TextView quoteTxt = (TextView) view.findViewById(R.id.textView);
            final TextView quoteNumber = (TextView) view.findViewById(R.id.number_quote);
            final Button shareButton = (Button) view.findViewById(R.id.share_button);
            final Button gotoBtn = (Button) view.findViewById(R.id.goto_button);
            final ImageButton favButton = (ImageButton) view.findViewById(R.id.fav_button);
            quoteTxt.setText(quoteList.get(position));
            if (AppConstants.favListDB.get(position).isFavorites()) {
                favButton.setSelected(true);
            } else {
                favButton.setSelected(false);
            }
            quoteNumber.setText("Fact: " + (position + 1));
            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<FavModel> tempFavObj = FavModel.find(FavModel.class, " position = ?",
                            String.valueOf(position));
                    FavModel favObj = tempFavObj.get(0);
                    boolean tempIsFav = AppConstants.favListDB.get(position).isFavorites();
                    favObj.setFavorites(!tempIsFav);
                    favObj.save();
                    AppConstants.favListDB.get(position).setFavorites(!tempIsFav);
                    favButton.setSelected(!tempIsFav);
                    notifyDataSetChanged();
                }
            });

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.setType("text/plain");
                    shareIntent
                            .putExtra(
                                    Intent.EXTRA_TEXT,
                                    "\"" + quoteList.get(position) + "\"");
                    startActivity(shareIntent);
                }
            });

            gotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showGotoDialog();
                }
            });

            ((ViewPager) container).addView(view, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }


    private void showGotoDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.goto_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setTitle("Enter Fact No:");
        final EditText userInput = (EditText) promptsView.findViewById(R.id.gotoCount);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Go..",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (userInput.getText().toString().length() != 0) {
                                    try {
                                        int count = Integer.parseInt(userInput.getText().toString
                                                ());
                                        if (count == 0 || count > AppConstants.allQuotesList.size
                                                ()) {
                                            userInput.setError("Enter Count [1-500]");
                                        } else {
                                            Toast.makeText(getActivity(), "Fact No: " + count,
                                                    Toast.LENGTH_LONG);
                                            dialog.cancel();
                                            pager.setCurrentItem(count - 1);
                                        }
                                    } catch (Exception ex) {
                                        dialog.dismiss();
                                    }
                                } else {
                                    userInput.setError("Enter Count [1-500]");
                                }
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
