package com.probytemedia.amazingfacts.utils;

import com.probytemedia.amazingfacts.models.FavModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AABID on 19/4/14.
 */
public class AppConstants {

    public static ArrayList<String> allQuotesList=new ArrayList<String>();
    public static List<FavModel> favListDB=new ArrayList<FavModel>();

    public static ArrayList<String> getAllList(){
        if(allQuotesList==null || allQuotesList.size()==0){
            new InitializeArrayList();
        }
        return allQuotesList;
    }
}
