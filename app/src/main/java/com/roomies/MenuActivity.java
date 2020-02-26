package com.roomies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MenuActivity extends AppCompatActivity {

    private Button mMessagesBtn;
    private Button mShoppingBtn;
    private Button mBillsBtn;
    private Button mAssignmentsBtn;
    private Button mFinancialBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mMessagesBtn = findViewById(R.id.messagesButton);
        mShoppingBtn = findViewById(R.id.shoppingButton);
        mBillsBtn = findViewById(R.id.billsButton);
        mAssignmentsBtn = findViewById(R.id.assignmentsButton);
        mFinancialBtn = findViewById(R.id.financialButton);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

//        mMessagesBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, MessagesActivity.class));
//        });
//
//        mShoppingBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, ShoppingActivity.class));
//        });
//
//        mBillsBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, BiilsActivity.class));
//        });
//
//        mAssignmentsBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, AssignmentsActivity.class));
//        });
//
//        mFinancialBtn.setOnClickListener(view -> {
//            startActivity(new Intent(MenuActivity.this, FinancialActivity.class));
//        });
    }
}