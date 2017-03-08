package ay3524.com.qubagtest.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ashish on 07-03-2017.
 */

public class Items implements Parcelable {
    String title,image_url,price,size;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.image_url);
        dest.writeString(this.price);
        dest.writeString(this.size);
    }

    public Items() {
    }

    protected Items(Parcel in) {
        this.title = in.readString();
        this.image_url = in.readString();
        this.price = in.readString();
        this.size = in.readString();
    }

    public static final Parcelable.Creator<Items> CREATOR = new Parcelable.Creator<Items>() {
        @Override
        public Items createFromParcel(Parcel source) {
            return new Items(source);
        }

        @Override
        public Items[] newArray(int size) {
            return new Items[size];
        }
    };
}
