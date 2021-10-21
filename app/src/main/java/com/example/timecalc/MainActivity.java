package com.example.timecalc;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class MainActivity extends AppCompatActivity {
    TextInputLayout txtInpLayout1, txtInpLayout2;
    TextInputEditText eTxtTime1, eTxtTime2;
    TextView txtViewResult;
    SwitchCompat switchSign;
    Button btnCalculate, btnClear1, btnClear2, btnCopy, btnCopyTo1, btnCopyTo2, btnChange;
    boolean can_calculate;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //инициализация объектов
        init();

        //инициализация анимаций
        final Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.rotate_btn_change);
        //final Animation animAlphaDesc = AnimationUtils.loadAnimation(this, R.anim.alpha_btn_descent);
        //final Animation animAlphaAsc = AnimationUtils.loadAnimation(this, R.anim.alpha_btn_ascent);

        //смена названия и знака операции
        switchSign.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (switchSign.isChecked()){
                switchSign.setText(R.string.SwitchPlus);
            }
            else {
                switchSign.setText(R.string.SwitchMinus);
            }
        });

        //ротация текстовых полей 1 и 2
        btnChange.setOnClickListener(v -> {
            if (!(Objects.requireNonNull(eTxtTime1.getText()).length() == 0
                    && Objects.requireNonNull(eTxtTime2.getText()).length() == 0)) {
                //перевернуть кнопку
                v.startAnimation(animRotate);

                //замена "пузырьком"
                String strTemp = eTxtTime1.getText().toString();
                eTxtTime1.setText(eTxtTime2.getText());
                eTxtTime2.setText(strTemp);
            }
        });

        //скрытие кнопки очитстить1
        eTxtTime1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    //btnClear1.setAnimation(animAlphaAsc);
                    btnClear1.setVisibility(View.VISIBLE);
                }
                else {
                    //btnClear1.setAnimation(animAlphaDesc);
                    btnClear1.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //скрытие кнопки очистить2
        eTxtTime2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    //btnClear2.setAnimation(animAlphaAsc);
                    btnClear2.setVisibility(View.VISIBLE);
                }
                else {
                    //btnClear2.setAnimation(animAlphaDesc);
                    btnClear2.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //кнопки очистки текстовых полей
        //текстовое поле 1
        btnClear1.setOnClickListener(v -> Objects.requireNonNull(eTxtTime1.getText()).clear());

        //текстовое поле 2
        btnClear2.setOnClickListener(v -> Objects.requireNonNull(eTxtTime2.getText()).clear());

        //кнопка копировать в буфер обмена
        btnCopy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TextView", txtViewResult.getText().toString());
            clipboard.setPrimaryClip(clip);
        });

        //кнопка копировать в editText1
        btnCopyTo1.setOnClickListener(v -> eTxtTime1.setText(txtViewResult.getText()));

        //кнопка копировать в editText2
        btnCopyTo2.setOnClickListener(v -> eTxtTime2.setText(txtViewResult.getText()));

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
                checkTime(text, txtInpLayout1);
            }
        });

        eTxtTime2.addTextChangedListener(new TextValidator(eTxtTime2) {
            @Override
            public void validate(TextView textView, String text) {
                checkTime(text, txtInpLayout2);
            }
        });

        //нажатие кнопки =
        btnCalculate.setOnClickListener(v -> {
            if (can_calculate){
                //убираем фокус с текстовых полей
                eTxtTime1.clearFocus();
                eTxtTime2.clearFocus();

                // сокрытие клавиатуры
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btnCalculate.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                LocalTime time1 = LocalTime.
                        parse(Objects.requireNonNull(eTxtTime1.getText()).toString());
                String[] strTime2 = Objects.
                        requireNonNull(eTxtTime2.getText()).toString().split(":");                  //разбивка на чч:мм:сс
                LocalTime timeRes;
                if (switchSign.isChecked()){                                                          //+
                    timeRes = time1.plusHours(Long.parseLong(strTime2[0]));
                    timeRes = timeRes.plusMinutes(Long.parseLong(strTime2[1]));
                    timeRes = timeRes.plusSeconds(Long.parseLong(strTime2[2]));
                }
                else{                                                                                   //-
                    timeRes = time1.minusHours(Long.parseLong(strTime2[0]));
                    timeRes = timeRes.minusMinutes(Long.parseLong(strTime2[1]));
                    timeRes = timeRes.minusSeconds(Long.parseLong(strTime2[2]));
                }
                if (timeRes.getSecond() == 0)
                    txtViewResult.setText(timeRes.truncatedTo(ChronoUnit.SECONDS).toString() + ":00");
                else
                    txtViewResult.setText(timeRes.truncatedTo(ChronoUnit.SECONDS).toString());
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
        txtInpLayout1 = findViewById(R.id.eTxtLayoutTime1);
        txtInpLayout2 = findViewById(R.id.eTxtLayoutTime2);
        eTxtTime1 = findViewById(R.id.eTxtTime1);
        eTxtTime2 = findViewById(R.id.eTxtTime2);
        txtViewResult = findViewById(R.id.txtViewResult);
        switchSign = findViewById(R.id.switchSign);
        btnChange = findViewById(R.id.btnChange);
        btnCopy = findViewById(R.id.btnCopy);
        btnCopyTo1 = findViewById(R.id.btnCopyTo1);
        btnCopyTo2 = findViewById(R.id.btnCopyTo2);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnClear1 = findViewById(R.id.btnClear1);
        btnClear2 = findViewById(R.id.btnClear2);
    }
}