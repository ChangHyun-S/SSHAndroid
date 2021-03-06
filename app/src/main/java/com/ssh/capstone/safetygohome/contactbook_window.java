package com.ssh.capstone.safetygohome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ssh.capstone.safetygohome.Database.DatabaseClass;

import static com.ssh.capstone.safetygohome.Database.PreParingDB.initDB;

public class contactbook_window extends AppCompatActivity {

    ArrayList<ContactData> items = new ArrayList<>();
    ArrayList<String> username = new ArrayList<>();
    ArrayList<String> usernum = new ArrayList<>();
    ArrayList<Boolean> item_position = null;
    String name = null, num = null;

    ListView contact_listView;
    ContactAdapter adapter;
    Button btn_delete;
    FloatingActionButton btn_add2;
    EditText edit_search;
    Intent FromAdd;
    Intent ToAdd;

    private DatabaseClass db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //타이틀바 없애기
        setContentView(R.layout.layout_contactbook);

        try {
            initDB(getResources(), false); // db 준비
        } catch (Exception e) {
            Log.w("Get DB Exception", e.getMessage());
        }

        setting();
        setlistener();
        addItem();
    }

    public void setting() {
        btn_add2 = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        edit_search = (EditText) findViewById(R.id.edit_search);
        contact_listView = (ListView) findViewById(R.id.contact_listview);
        ToAdd = new Intent(com.ssh.capstone.safetygohome.contactbook_window.this,
                com.ssh.capstone.safetygohome.listview_add.class);
        FromAdd = getIntent();
        adapter = new ContactAdapter();
        contact_listView.setAdapter(adapter);
        contact_listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        db = new DatabaseClass(this);
    }

    //listener 설정
    public void setlistener() {

        btn_add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToAdd.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ToAdd);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

        //editText 실시간 확인
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchItem(edit_search.getText().toString());
            }
        });

    }

    //db 파일에서 가져온 정보를 listview 로
    public void setlistview() {
        db.GetUser(username, usernum);

        for (int i = 0; i < username.size(); i++) {
            adapter.addItem(username.get(i), usernum.get(i));
            items.add(new ContactData(username.get(i), usernum.get(i)));
        }
        adapter.notifyDataSetChanged();
    }

    //새 정보 추가하기
    public void addItem() {
        name = FromAdd.getStringExtra("name");
        num = FromAdd.getStringExtra("num");

        if (name != null && num != null) {
            Log.i(name, num);
            db.SaveUser(name, num);
        }
        setlistview();
    }

    //정보 삭제하기
    public void deleteItem() {
        int count = adapter.getCount();

        item_position = adapter.getItems();

        for (int i = 0; i < items.size(); i++) {
            Log.i(i + "번째 : ", items.get(i).getName() + "");
            Log.i(i + "번째 : ", item_position.get(i) + "");
        }

        if (count > 0) {
            adapter.clear();
            db.DeleteUser(items, item_position);
            setlistview();
        }

    }

    //이름으로 검색하기
    public void searchItem(String name) {

        //검색을 위해 listview clear
        adapter.clear();

        //검색창이 비었을 경우 보든 데이터를 가져온다
        if (name.equals(""))
            setlistview();

            //검색창에 입력이 되면 해당 값을 검색함
        else {
            db.SearchUser(username, usernum, name);

            for (int i = 0; i < username.size(); i++) {
                adapter.addItem(username.get(i), usernum.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

}
