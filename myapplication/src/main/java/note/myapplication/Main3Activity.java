package note.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import note.myapplication.view.ScaleRulerView;

public class Main3Activity extends AppCompatActivity {

    ScaleRulerView mHeightWheelView;
    TextView mHeightValue;
    ScaleRulerView mWeightWheelView;
    TextView mWeightValue;

    private float mHeight = 170;
    private float mMaxHeight = 220;
    private float mMinHeight = 100;


    private float mWeight = 65;
    private float mMaxWeight = 200;
    private float mMinWeight = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        initView();
        addListener();
    }

    private void addListener() {
        mHeightWheelView.initViewParam(mHeight, mMaxHeight, mMinHeight);
        mHeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mHeightValue.setText((int) value + "");
                mHeight = value;
            }
        });

        mWeightWheelView.initViewParam(mWeight, mMaxWeight, mMinWeight);
        mWeightWheelView.setValueChangeListener(new ScaleRulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mWeightValue.setText(value + "");
                mWeight = value;
            }
        });
    }

    private void initView() {
        mHeightWheelView= (ScaleRulerView) findViewById(R.id.scaleWheelView_height);
        mHeightValue= (TextView) findViewById(R.id.tv_user_height_value);
        mWeightWheelView= (ScaleRulerView) findViewById(R.id.scaleWheelView_weight);
        mWeightValue= (TextView) findViewById(R.id.tv_user_weight_value);
    }

    public void submitMesssage(View view) {
        Toast.makeText(this, "选择身高： " + mHeight + " 体重： " + mWeight, Toast.LENGTH_LONG).show();
    }
}
