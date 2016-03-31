package com.carsmart.lockpattern.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.carsmart.lockpattern.LockPatternView;
import com.carsmart.lockpattern.PatternView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    PatternView pattern;
    LockPatternView lockPattern;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pattern = (PatternView) findViewById(R.id.pattern);
        lockPattern = (LockPatternView) findViewById(R.id.lockPattern);

        lockPattern.setContent(new LockPatternView.OnSettingListener() {
            boolean isFirst = true;
            String password = null;

            @Override
            public void onSetting(List<String> records) {
                String result = getPassword(records);
                if (isFirst) {
                    isFirst = false;
                    pattern.setContent(new ArrayList<>(records));
                    password = result;
                    lockPattern.gotoNormal();
                } else {
                    if (result.equals(password)) {
                        lockPattern.gotoNormal();
                        show("设置成功");
                        finish();
                    } else {
                        lockPattern.gotoWarn();
                        show("设置错误");
                    }
                }
            }
        });
    }

    protected void show(String string) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT);
        } else {
            toast.setText(string);
        }
        toast.show();
    }

    private String getPassword(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        return sb.toString();
    }

}
