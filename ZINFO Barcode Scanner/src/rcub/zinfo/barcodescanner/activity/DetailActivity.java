package rcub.zinfo.barcodescanner.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import rcub.zinfo.barcodescanner.R;
import rcub.zinfo.barcodescanner.ZinfoPaket;

public class DetailActivity extends BarcodeScannerBaseActivity {

    public static String KEY_PAKET = "DetailPaket";

    TextView proizvod;
    TextView numOd;
    TextView numDo;
    TextView paketBroj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        View view = findViewById(R.id.detail_header);

        proizvod = (TextView) findViewById(R.id.detail_paket_proizvod);
        numOd = (TextView) findViewById(R.id.detail_paket_num_od);
        numDo = (TextView) findViewById(R.id.detail_paket_num_do);
        paketBroj = (TextView) findViewById(R.id.detail_paket_broj);

        ZinfoPaket paket = getIntent().getExtras().getParcelable(KEY_PAKET);

        proizvod.setText(paket.getProizvod());
        numOd.setText(paket.getNumOd());
        numDo.setText(paket.getNumDo());
        paketBroj.setText(paket.getBrojPaketa().toString());

        setLayoutAnim_slidedownfromtop((ViewGroup)view, this);
    }

    public static void setLayoutAnim_slidedownfromtop(ViewGroup panel, Context ctx) {

        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(100);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(500);
        set.addAnimation(animation);

        LayoutAnimationController controller =
                new LayoutAnimationController(set, 0.25f);
        panel.setLayoutAnimation(controller);

    }}
