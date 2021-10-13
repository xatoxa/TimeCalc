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

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class MainActivity extends AppCompatActivity {

    EditText eTxtTime1, eTxtTime2;
    TextView txtViewResult;
    SwitchCompat switchSign;
    ImageButton imgBtnChange;
    Button btnCalculate;
    boolean sign;

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
                    sign = true;
                    switchSign.setText(R.string.SwitchPlus);
                }
                else {
                    sign = false;
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

        Slot[] slots = new UnderscoreDigitSlotsParser().parseSlots("__:__:__");
        FormatWatcher formatWatcher = new MaskFormatWatcher( // форматирование текста
                MaskImpl.createTerminated(slots)
        );
        formatWatcher.installOn(eTxtTime1); // устанавливаем форматтер на editText1
        formatWatcher.installOn(eTxtTime2); // устанавливаем форматтер на editText2


    }

    protected void init(){
        eTxtTime1 = findViewById(R.id.eTxtTime1);
        eTxtTime2 = findViewById(R.id.eTxtTime2);
        txtViewResult = findViewById(R.id.txtViewResult);
        switchSign = findViewById(R.id.switchSign);
        imgBtnChange = findViewById(R.id.imgBtnChange);
        btnCalculate = findViewById(R.id.btnCalculate);
    }


}