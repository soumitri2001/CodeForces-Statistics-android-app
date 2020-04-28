package com.example.cfstats;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class DeveloperPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer_layout);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Developer Details");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView devDet=findViewById(R.id.devDet);
        String dev="\n\n\n This app is created by: \n\n Soumitri Chattopadhyay \n Information Technology (UG 1), \n Jadavpur University \n\n Batch of '23 \n\n\n\n\n\n\n\n\n\n";

        devDet.animate().alpha(0).setDuration(500).alpha(1).setDuration(500);
        devDet.setText(dev);

        TextView git=findViewById(R.id.github);
        git.setText("\t\t\t Visit Github");
        git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/soumitri2001/CodeForces-Statistics-android-app")));
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
                startActivity(new Intent(DeveloperPage.this,
                        com.example.cfstats.ContestListActivity.class));
                break;

            case R.id.devSite:
                startActivity(new Intent(DeveloperPage.this,
                        com.example.cfstats.DeveloperPage.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
