package rcub.zinfo.barcodescanner;

/**
 * Created by ivan.radojevic on 01.06.2015..
 */
public class ZinfoNeispravnaNumeracija {

    private Long id;
    private String numeracija;
    private Long status;
    private Long idPaket;

    public ZinfoNeispravnaNumeracija(){}

    public ZinfoNeispravnaNumeracija(Long id, String numeracija, Long status, Long idPaket) {
        this.id = id;
        this.numeracija = numeracija;
        this.status = status;
        this.idPaket = idPaket;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeracija() {
        return numeracija;
    }

    public void setNumeracija(String numeracija) {
        this.numeracija = numeracija;
    }

    public Long getIdPaket() {
        return idPaket;
    }

    public void setIdPaket(Long idPaket) {
        this.idPaket = idPaket;
    }
}
