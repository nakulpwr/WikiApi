package com.nakul.wikiapi.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class SearchResultModel implements Parcelable {

    private String pageid;
    private String title;
    private String thumbImage;
    private String extract;


    private SearchResultModel(Parcel in) {
        pageid = in.readString();
        title = in.readString();
        thumbImage = in.readString();
        extract = in.readString();
    }

    public String getExtract() {
        return extract;
    }

    public void setExtract(String extract) {
        this.extract = extract;
    }

    public static final Creator<SearchResultModel> CREATOR = new Creator<SearchResultModel>() {
        @Override
        public SearchResultModel createFromParcel(Parcel in) {
            return new SearchResultModel(in);
        }

        @Override
        public SearchResultModel[] newArray(int size) {
            return new SearchResultModel[size];
        }
    };

    public String getPageid() {
        return pageid;
    }

    public void setPageid(String pageid) {
        this.pageid = pageid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
        Log.e(getClass().getSimpleName(), thumbImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pageid);
        dest.writeString(title);
        dest.writeString(thumbImage);
        dest.writeString(extract);
    }
}
