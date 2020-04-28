package com.example.cfstats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etHandle;
    Button submit;
    String handle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("CodeForces Statistics");
        actionBar.setDisplayShowHomeEnabled(true);

        final EditText etHandle = findViewById(R.id.handle);
        etHandle.setTextIsSelectable(true);
        etHandle.setSelection(etHandle.length());

        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handle = etHandle.getText().toString().trim();
                Log.d("Clicked", "Handle is: " + handle);

                if (handle.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter handle !", Toast.LENGTH_SHORT).show();
                } else {
                    etHandle.getText().clear();
                    Log.d("Text cleared","");
                    Intent intent = new Intent(MainActivity.this, com.example.cfstats.StatsActivity.class);
                    intent.putExtra("handle", handle);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.cfSite:
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.codeforces.com")));
                break;
            case R.id.conList:
                startActivity(new Intent(MainActivity.this,
                        com.example.cfstats.ContestListActivity.class));
                break;

            case R.id.devSite:
                startActivity(new Intent(MainActivity.this,
                        com.example.cfstats.DeveloperPage.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
