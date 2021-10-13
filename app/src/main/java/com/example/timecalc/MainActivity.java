package com.example.timecalc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalTime;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class MainActivity extends AppCompatActivity {
    TextInputLayout txtInpLayt1, txtInpLayt2;
    TextInputEditText eTxtTime1, eTxtTime2;
    TextView txtViewResult;
    SwitchCompat switchSign;
    ImageButton imgBtnChange;
    Button btnCalculate;
    boolean can_calculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //инициализация
        init();

        //смена названия и знака операции
        switchSign.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchSign.isChecked()){
                    switchSign.setText(R.string.SwitchPlus);
                }
                else {
                    switchSign.setText(R.string.SwitchMinus);
                }
            }
        });

        //ротация текстовых полей 1 и 2
        imgBtnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strTemp = eTxtTime1.getText().toString();
                eTxtTime1.setText(eTxtTime2.getText());
                eTxtTime2.setText(strTemp);
            }
        });

        Slot[] slots1 = new UnderscoreDigitSlotsParser().parseSlots("__:__:__");
        FormatWatcher formatWatcher1 = new MaskFormatWatcher( // форматирование текста
                MaskImpl.createTerminated(slots1)
        );
        formatWatcher1.installOn(eTxtTime1); // устанавливаем форматтер на editText1

        Slot[] slots2 = new UnderscoreDigitSlotsParser().parseSlots("__:__:__");
        FormatWatcher formatWatcher2 = new MaskFormatWatcher( // форматирование текста
                MaskImpl.createTerminated(slots2)
        );
        formatWatcher2.installOn(eTxtTime2); // устанавливаем форматтер на editText2

        eTxtTime1.addTextChangedListener(new TextValidator(eTxtTime1) {
            @Override
            public void validate(TextView textView, String text) {
                checkTime(text, txtInpLayt1);

            }
        });

        eTxtTime2.addTextChangedListener(new TextValidator(eTxtTime2) {
            @Override
            public void validate(TextView textView, String text) {
                checkTime(text, txtInpLayt2);
            }
        });

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (can_calculate){
                    LocalTime time1 = LocalTime.parse(eTxtTime1.getText().toString());
                    String[] strTime2 = eTxtTime2.getText().toString().split(":");
                    if (switchSign.isChecked()){                                                          //+
                        LocalTime timeRes = time1.plusHours(Long.parseLong(strTime2[0]));
                        timeRes = timeRes.plusMinutes(Long.parseLong(strTime2[1]));
                        timeRes = timeRes.plusSeconds(Long.parseLong(strTime2[2]));
                        txtViewResult.setText(timeRes.toString());
                    }
                    else{                                                               //-
                        LocalTime timeRes = time1.minusHours(Long.parseLong(strTime2[0]));
                        timeRes = timeRes.minusMinutes(Long.parseLong(strTime2[1]));
                        timeRes = timeRes.minusSeconds(Long.parseLong(strTime2[2]));
                        txtViewResult.setText(timeRes.toString());
                    }
                }
            }
        });
    }

    protected void checkTime(String text, TextInputLayout textInputLayout){
        can_calculate = false;
        if (text.length() < 8) textInputLayout.setError("Введите полностью.");
        else {
            String[] strTime = text.split(":");
            if (Integer.parseInt(strTime[0]) > 23
                    && Integer.parseInt(strTime[1]) > 59
                    && Integer.parseInt(strTime[2]) > 59) textInputLayout.setError("Всё неправильно.");
            else if (Integer.parseInt(strTime[0]) > 23
                    && Integer.parseInt(strTime[1]) > 59) textInputLayout.setError("Часы и минуты неправильно");
            else if (Integer.parseInt(strTime[1]) > 59
                    && Integer.parseInt(strTime[2]) > 59) textInputLayout.setError("Минуты и секунды неправильно");
            else if (Integer.parseInt(strTime[0]) > 23
                    && Integer.parseInt(strTime[2]) > 59)textInputLayout.setError("Часы и секунды неправильно");
            else if (Integer.parseInt(strTime[0]) > 23) textInputLayout.setError("Часы меньше 24");
            else if (Integer.parseInt(strTime[1]) > 59) textInputLayout.setError("Минуты меньше 60");
            else if (Integer.parseInt(strTime[2]) > 59) textInputLayout.setError("Секунды меньше 60");
            else {
                textInputLayout.setError(null);
                can_calculate = true;
            }
        }
    }

    protected void init(){
        txtInpLayt1 = findViewById(R.id.eTxtLayoutTime1);
        txtInpLayt2 = findViewById(R.id.eTxtLayoutTime2);
        eTxtTime1 = findViewById(R.id.eTxtTime1);
        eTxtTime2 = findViewById(R.id.eTxtTime2);
        txtViewResult = findViewById(R.id.txtViewResult);
        switchSign = findViewById(R.id.switchSign);
        imgBtnChange = findViewById(R.id.imgBtnChange);
        btnCalculate = findViewById(R.id.btnCalculate);
    }


}