package rcub.zinfo.barcodescanner;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ivan.radojevic on 07.05.2015..
 *
 * Klasa koja reprezentuje korisnika u ZINFO aplikaciji.
 *
 */
public class ZinfoLoginKorisnik implements Serializable, Parcelable  {

    private static final long serialVersionUID = -8392407005981380059L;
    private Long id;
    private String username;
    private String password;

    /**
     * Prazan konstruktor koji se poziva kada se ne dobiju podaci.
     */
    public ZinfoLoginKorisnik() {
    }

    public ZinfoLoginKorisnik(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(username);
        out.writeString(password);
    }

    public static final Parcelable.Creator<ZinfoLoginKorisnik> CREATOR
            = new Parcelable.Creator<ZinfoLoginKorisnik>() {
        public ZinfoLoginKorisnik createFromParcel(Parcel in) {
            return new ZinfoLoginKorisnik(in);
        }

        public ZinfoLoginKorisnik[] newArray(int size) {
            return new ZinfoLoginKorisnik[size];
        }
    };

    private ZinfoLoginKorisnik(Parcel in) {
        id = in.readLong();
        username = in.readString();
        password = in.readString();
    }
}
