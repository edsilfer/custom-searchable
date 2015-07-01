package br.com.customsearchable.model;

import android.os.Parcel;
import android.os.Parcelable;

import br.com.customsearchable.R;

/**
 * Represents the set of information contained in one result row
 */
public class ResultItem implements Parcelable {

    private String header;
    private String subHeader;
    private Integer leftIcon;
    private Integer rightIcon;

    // Constructors ________________________________________________________________________________
    public ResultItem () {
        this.header = "Error";
        this.subHeader = "Error";
        this.leftIcon = R.drawable.clock_icon;
        this.rightIcon = R.drawable.arrow_left_up_icon;
    }

    public ResultItem (String header, String subHeader, Integer leftIcon, Integer rightIcon) {
        this.setHeader(header);
        this.setSubHeader(subHeader);
        this.setLeftIcon(leftIcon);
        this.setRightIcon(rightIcon);
    }

    // Getters and Setters__________________________________________________________________________
    public Integer getLeftIcon() {
        return leftIcon;
    }

    public void setLeftIcon(Integer leftIcon) {
        if (leftIcon != null && leftIcon != 0 && leftIcon != -1) {
            this.leftIcon = leftIcon;
        } else {
            this.leftIcon = R.drawable.clock_icon;
        }
    }

    public Integer getRightIcon() {
        return rightIcon;
    }

    public void setRightIcon(Integer rightIcon) {
        if (rightIcon != null && rightIcon != 0 && rightIcon != -1) {
            this.rightIcon = rightIcon;
        } else {
            this.rightIcon = R.drawable.arrow_left_up_icon;
        }
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
        if (subHeader != null && !"".equals(subHeader)) {
            this.subHeader = subHeader;
        } else {
            this.subHeader = "Error (Empty data)";
        }
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
