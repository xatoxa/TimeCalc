package com.example.timecalc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalTime;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class MainActivity extends AppCompatActivity {
    TextInputLayout txtInpLayt1, txtInpLayt2;
    TextInputEditText eTxtTime1, eTxtTime2;
    TextView txtViewResult;
    SwitchCompat switchSign;
    ImageButton imgBtnChange, imgBtnCopy;
    Button btnCalculate, btnClear1, btnClear2;
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

        //кнопки очистки текстовых полей
        btnClear1.setOnClickListener(new View.OnClickListener() {                                       //текстовое поле 1
            @Override
            public void onClick(View v) {
                eTxtTime1.getText().clear();
            }
        });

        btnClear2.setOnClickListener(new View.OnClickListener() {                                       //текстовое поле 2
            @Override
            public void onClick(View v) {
                eTxtTime2.getText().clear();
            }
        });

        //кнопка копировать в буфер обмена
        imgBtnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("TextView", txtViewResult.getText().toString());
                clipboard.setPrimaryClip(clip);
            }
        });

        //создание маски для текстовых полей
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

        //валидация текстовых полей
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

        //нажатие кнопки =
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (can_calculate){
                    //убираем фокус с текстовых полей
                    eTxtTime1.clearFocus();
                    eTxtTime2.clearFocus();

                    // сокрытие клавиатуры
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btnCalculate.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    LocalTime time1 = LocalTime.parse(eTxtTime1.getText().toString());
                    String[] strTime2 = eTxtTime2.getText().toString().split(":");                  //разбивка на чч:мм:сс
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

    //проверка правильности ввода времени
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

    //инициализация объектов
    protected void init(){
        txtInpLayt1 = findViewById(R.id.eTxtLayoutTime1);
        txtInpLayt2 = findViewById(R.id.eTxtLayoutTime2);
        eTxtTime1 = findViewById(R.id.eTxtTime1);
        eTxtTime2 = findViewById(R.id.eTxtTime2);
        txtViewResult = findViewById(R.id.txtViewResult);
        switchSign = findViewById(R.id.switchSign);
        imgBtnChange = findViewById(R.id.imgBtnChange);
        imgBtnCopy = findViewById(R.id.imgBtnCopy);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnClear1 = findViewById(R.id.btnClear1);
        btnClear2 = findViewById(R.id.btnClear2);
    }
}