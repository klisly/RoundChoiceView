package imdao.cn.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import imdao.cn.roundchoice.RoundChoiceView;

public class MainActivity extends AppCompatActivity {

    RoundChoiceView rippleChoiceView;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rippleChoiceView.setmNumber(rippleChoiceView.getmNumber() + 1);
            if(rippleChoiceView.getmNumber() == 99){
                return;
            }
            handler.postDelayed(runnable, 2000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rippleChoiceView = (RoundChoiceView) findViewById(R.id.choicview);
        rippleChoiceView.setEnabled(false);
        RoundChoiceView rippleChoiceView2 = (RoundChoiceView) findViewById(R.id.choicview2);
        rippleChoiceView2.setOnCheckedChangeListener(new RoundChoiceView.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RoundChoiceView view, boolean isChecked) {
                Log.i("onCheckedChanged", "onCheckedChanged:" + isChecked);
                Toast.makeText(getApplicationContext(), "isChecked:"+isChecked, Toast.LENGTH_SHORT).show();
            }
        });
        handler.postDelayed(runnable, 2000);
    }
}
