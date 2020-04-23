package com.example.mytodolist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mytodolist.todoListView;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        EditText nameField=findViewById(R.id.nameField);
        final TextView greeting=findViewById(R.id.greeting);
        Button viewListButton=findViewById(R.id.viewListButton);


        TextWatcher nameWatcher=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                greeting.setText("Greetings "+s.toString());
            }
        };

        nameField.addTextChangedListener(nameWatcher);

        viewListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewListIntent= new Intent(v.getContext(), todoListView.class);
                startActivity(viewListIntent);
            }
        });
    }
}
