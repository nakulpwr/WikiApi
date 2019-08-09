package com.nakul.wikiapi.model;

import java.util.List;

public class NetworkResponseModel {

    private String responseStr = "";
    private int responseCode = 200;
    private List<SearchResultModel> responseModelList;

    public NetworkResponseModel() {

    }

    public String getErrorResponseStr() {
        return responseStr;
    }

    public void setResponseStr(String responseStr) {
        this.responseStr = responseStr;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public List<SearchResultModel> getResponseModelList() {
        return responseModelList;
    }

    public void setResponseModelSet(List<SearchResultModel> responseModelList) {
        this.responseModelList = responseModelList;
    }
}
