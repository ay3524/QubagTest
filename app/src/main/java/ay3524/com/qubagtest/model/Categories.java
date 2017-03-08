package ay3524.com.qubagtest.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ashish on 06-03-2017.
 */

public class Categories implements Parcelable {
    String title,image_url;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.image_url);
    }

    public Categories() {
    }

    protected Categories(Parcel in) {
        this.title = in.readString();
        this.image_url = in.readString();
    }

    public static final Parcelable.Creator<Categories> CREATOR = new Parcelable.Creator<Categories>() {
        @Override
        public Categories createFromParcel(Parcel source) {
            return new Categories(source);
        }

        @Override
        public Categories[] newArray(int size) {
            return new Categories[size];
        }
    };
}
