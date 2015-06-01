package rcub.zinfo.barcodescanner;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Klasa koja reprezentuje paket sa proizvodom i numeracijom.
 */
public class ZinfoPaket implements Serializable, Parcelable {

    private static final long serialVersionUID = -8392407005981380059L;
    private String proizvod;
    private Long brojPaketa;
    private String numOd;
    private String numDo;
    private Long idPaket;
    private Long idProizvod;

    /**
     * Prazan konstruktor koji se poziva kada se ne dobiju podaci.
     */
    public ZinfoPaket() {
        this.proizvod = "Nema Podataka";
        this.brojPaketa = -1L;
        this.numOd = "";
        this.numDo = "";
        this.idPaket = null;
        this.idProizvod = null;
    }

    public ZinfoPaket(String proizvod, Long brojPaketa, String numOd,
                      String numDo, Long idPaket, Long idProizvod) {
        this.proizvod = proizvod;
        this.brojPaketa = brojPaketa;
        this.numOd = numOd;
        this.numDo = numDo;
        this.idPaket = idPaket;
        this.idProizvod = idProizvod;
    }

    public String getProizvod() {
        return proizvod;
    }

    public void setProizvod(String proizvod) {
        this.proizvod = proizvod;
    }

    public Long getBrojPaketa() {
        return brojPaketa;
    }

    public void setBrojPaketa(Long brojPaketa) {
        this.brojPaketa = brojPaketa;
    }

    public String getNumOd() {
        return numOd;
    }

    public void setNumOd(String numOd) {
        this.numOd = numOd;
    }

    public String getNumDo() {
        return numDo;
    }

    public void setNumDo(String numDo) {
        this.numDo = numDo;
    }

    public Long getIdPaket() {
        return idPaket;
    }

    public void setIdPaket(Long idPaket) {
        this.idPaket = idPaket;
    }

    public Long getIdProizvod() {
        return idProizvod;
    }

    public void setIdProizvod(Long idProizvod) {
        this.idProizvod = idProizvod;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(proizvod);
        out.writeLong(brojPaketa);
        out.writeString(numOd);
        out.writeString(numDo);
        out.writeLong(idPaket);
        out.writeLong(idProizvod);
    }

    public static final Parcelable.Creator<ZinfoPaket> CREATOR
            = new Parcelable.Creator<ZinfoPaket>() {
        public ZinfoPaket createFromParcel(Parcel in) {
            return new ZinfoPaket(in);
        }

        public ZinfoPaket[] newArray(int size) {
            return new ZinfoPaket[size];
        }
    };

    private ZinfoPaket(Parcel in) {
        proizvod = in.readString();
        brojPaketa = in.readLong();
        numOd = in.readString();
        numDo = in.readString();
        idPaket = in.readLong();
        idProizvod = in.readLong();
    }
}