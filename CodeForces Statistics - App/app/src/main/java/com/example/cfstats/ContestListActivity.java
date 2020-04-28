package com.example.cfstats;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ContestListActivity extends AppCompatActivity
{
    ProgressDialog dialog;
    final String API_CONTEST = "https://codeforces.com/api/contest.list?";

    ArrayList<ContestListView> contestsList=new ArrayList<>();
    ArrayList<String> contestID=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contest_list_layout);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Upcoming Contests");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(),"Network Unavailable :(", Toast.LENGTH_SHORT).show();
            Log.d("Network Availability","false");

        } else {
            Log.d("Network Availability","true");
            new JsonContest().execute();
        }
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
                        Uri.parse("https://www.codeforces.com/contests")));
                break;
            case R.id.conList:
                startActivity(new Intent(ContestListActivity.this,
                        com.example.cfstats.ContestListActivity.class));
                break;

            case R.id.devSite:
                startActivity(new Intent(ContestListActivity.this,
                        com.example.cfstats.DeveloperPage.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        boolean isAvailable=false;
        ConnectivityManager manager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo network=manager.getActiveNetworkInfo();
        if(network!=null && network.isConnected()) isAvailable=true;
        Log.d("Network ",Boolean.toString(isAvailable));
        return isAvailable;
    }

    private class JsonContest extends AsyncTask<String,Void,Exception>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(ContestListActivity.this);
            dialog.setMessage("Loading contest list...");
            dialog.show();
        }

        @Override
        protected Exception doInBackground(String... strings) {
            Exception exception=null;
            try {
                URL url=new URL(API_CONTEST);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(in));
                String line="",data="";
                while(line!=null) {
                    line=br.readLine();
                    data += line;
                }
                // int pos=data.lastIndexOf('{');
                // String jData= data.substring(0,pos+1);
                // jData+="]}";

                // Log.d("Upcoming data ",data);

                JSONObject root=new JSONObject(data);
                JSONArray result=root.getJSONArray("result");
                for(int i=0;i<result.length();i++)
                {
                    JSONObject object=result.getJSONObject(i);
                    if((object.getString("phase")).equals("FINISHED")) {
                        break;
                    }
                   String id=Integer.toString(object.getInt("id"));
                    contestID.add(id);

                    String name=object.getString("name"); name="  "+name;

                    int dur=object.getInt("durationSeconds");
                        int h=dur/3600, m=(dur%3600)/60;
                    String duration=Integer.toString(h)+" hr "+Integer.toString(m)+" mins "; duration="  "+duration;

                    long start=object.getLong("startTimeSeconds"); start*=1000L;
                        Calendar cal=Calendar.getInstance();
                        Date date=new Date(start);
                        cal.setTime(date);
                        SimpleDateFormat jdf = new SimpleDateFormat("dd-MM-yyyy HH:mm zz");
                        jdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        jdf.setTimeZone(TimeZone.getDefault());
                    String startTime = jdf.format(cal.getTime()); startTime="  "+startTime;

                    contestsList.add(new ContestListView(name,duration,startTime));
                }

                /*
                for(int i=0;i<contestsList.size();i++)
                {
                    Log.d("Name ",contestsList.get(i).getName());
                    Log.d("Duration ",contestsList.get(i).getDuration());
                    Log.d("Start time ",contestsList.get(i).getStart());
                    Log.d(" ","\n");
                }
                */

                /*
                ContestAdapter adapter = new ContestAdapter(ContestListActivity.this, contestsList);
                ListView myListView = findViewById(R.id.myListView);
                myListView.setAdapter(adapter);
                */

            } catch (Exception e) {
                e.printStackTrace();
                exception=e;
                Toast.makeText(getApplicationContext(),"Some problem occurred :(",Toast.LENGTH_LONG).show();
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);

            ContestAdapter adapter = new ContestAdapter(ContestListActivity.this, contestsList);
            ListView myListView = findViewById(R.id.myListView);
            myListView.setAdapter(adapter);

            dialog.dismiss();
        }
    }
}
