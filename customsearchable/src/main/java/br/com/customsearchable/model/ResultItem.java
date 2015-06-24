package br.com.customsearchable.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the set of information contained in one row
 */
public class ResultItem implements Parcelable {

    private String header;
    private String subHeader;
    private Integer leftIcon;
    private Integer rightIcon;

    // Constructors ________________________________________________________________________________
    public ResultItem () {
        this.header = "";
        this.subHeader = "";
        this.leftIcon = -1;
        this.rightIcon = -1;
    }

    public ResultItem (String header, String subHeader, Integer leftIcon, Integer rightIcon) {
        this.header = header;
        this.subHeader = subHeader;
        this.leftIcon = leftIcon;
        this.rightIcon = rightIcon;
    }

    // Getters and Setters__________________________________________________________________________
    public Integer getLeftIcon() {
        return leftIcon;
    }

    public void setLeftIcon(Integer leftIcon) {
        this.leftIcon = leftIcon;
    }

    public Integer getRightIcon() {
        return rightIcon;
    }

    public void setRightIcon(Integer rightIcon) {
        this.rightIcon = rightIcon;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSubHeader() {
        return subHeader;
    }

    public void setSubHeader(String subHeader) {
        this.subHeader = subHeader;
    }

    // Parcelable contract implementation __________________________________________________________
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(header);
        dest.writeString(subHeader);
        dest.writeInt(leftIcon);
        dest.writeInt(rightIcon);
    }

    // Parcelable Creator Implementation ___________________________________________________________
    public static final Creator<ResultItem> CREATOR = new Creator<ResultItem>() {

        public ResultItem createFromParcel(Parcel in) {
            ResultItem resultItem = new ResultItem();

            resultItem.setHeader(in.readString());
            resultItem.setSubHeader(in.readString());
            resultItem.setLeftIcon(in.readInt());
            resultItem.setRightIcon(in.readInt());

            return resultItem;
        }

        public ResultItem[] newArray(int size) {
            return new ResultItem[size];
        }
    };
}
