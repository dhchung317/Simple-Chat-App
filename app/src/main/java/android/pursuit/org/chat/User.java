package android.pursuit.org.chat;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    String uid;
    String username;
    String imageUrl;

    public User(String uid, String username, String imageUrl) {
        this.uid = uid;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public User() {
    }

    protected User(Parcel in) {
        uid = in.readString();
        username = in.readString();
        imageUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(username);
        dest.writeString(imageUrl);
    }
}
