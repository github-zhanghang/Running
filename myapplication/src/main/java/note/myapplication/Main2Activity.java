package note.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    ImageView girlImageView,boyImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        addListener();
    }

    private void addListener() {
        girlImageView.setOnClickListener(this);
        boyImageView.setOnClickListener(this);
    }

    private void initView() {
        girlImageView= (ImageView) findViewById(R.id.girl);
        boyImageView= (ImageView) findViewById(R.id.boy);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.girl:
                Toast.makeText(this,"您选择了：女", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Main2Activity.this,Main3Activity.class);
                startActivity(intent);
                break;
            case R.id.boy:
                Toast.makeText(this,"您选择了：男", Toast.LENGTH_SHORT).show();
                Intent intent2=new Intent(Main2Activity.this,Main3Activity.class);
                startActivity(intent2);
                break;
        }

    }
}
