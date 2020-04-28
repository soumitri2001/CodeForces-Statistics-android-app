package com.example.cfstats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class StatsActivity extends AppCompatActivity
{
    String handle;
    String api_url = "https://codeforces.com/api/user.status?handle=";
    String rating_url = "https://codeforces.com/api/user.rating?handle=";
    String userInfo_url="https://codeforces.com/api/user.info?handles=";
    ProgressDialog dialog;
    String userInfo="";
    TextView tvInfo,copyright;

    // collection data structures for all data obtained from json
    ArrayList<String> competedContest=new ArrayList<>();
    HashSet<String> tags=new HashSet<>();
    HashMap<String,Integer> verdict=new HashMap<>();
    TreeMap<String,Integer> levels=new TreeMap<>();
    TreeMap<String,Integer> types=new TreeMap<>();
    TreeMap<String,Integer> lang=new TreeMap<>();
    ArrayList<Integer> ratingList=new ArrayList<>();
    ArrayList<Integer> conIDs=new ArrayList<>();

    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        actionBar=getSupportActionBar();
        actionBar.setTitle("User Stats");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        copyright=findViewById(R.id.copyright);
        copyright.setText("\n\n \t\t\t\t Happy coding !");

        handle=getIntent().getStringExtra("handle");
        assert handle != null;
        tvInfo=(TextView) findViewById(R.id.tvInfo);

        api_url += handle +"&from=1";
        rating_url += handle;
        userInfo_url += handle;

        if(!isNetworkAvailable()) {
            Toast.makeText(getApplicationContext(),"Network Unavailable :(", Toast.LENGTH_SHORT).show();
            Log.d("Network Availability","false");

        } else {
            Log.d("Network Availability","true");
            new JsonTask().execute();
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
                        Uri.parse("https://www.codeforces.com")));
                break;
            case R.id.conList:
                startActivity(new Intent(StatsActivity.this,
                        com.example.cfstats.ContestListActivity.class));
                break;

            case R.id.devSite:
                startActivity(new Intent(StatsActivity.this,
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

    private class JsonTask extends AsyncTask<String,Void,Exception>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(StatsActivity.this);
            dialog.setMessage("Loading Stats...");
            dialog.show();
        }

        @Override
        protected Exception doInBackground(String... links) {

            Exception exception=null;

            try {
                URL url=new URL(api_url);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(in));
                String line="",data="";
                while(line!=null) {
                    line=br.readLine();
                    data += line;
                }

                JSONObject root=new JSONObject(data);
                JSONArray results=root.getJSONArray("result");
                for(int i=0;i<results.length();i++)
                {
                    JSONObject object=results.getJSONObject(i);

                    // to get count of all types of verdicts

                    String vdt=object.getString("verdict");
                    if(!verdict.containsKey(vdt)) verdict.put(vdt,1);
                    else verdict.put(vdt,verdict.get(vdt)+1);

                    // to get all the tags and levels used so far

                    JSONObject problem=object.getJSONObject("problem");
                    String ll=problem.getString("index");
                    String lvl=ll.substring(0,1);
                    if(vdt.equals("OK")) {
                        if (!levels.containsKey(lvl)) levels.put(lvl,1);
                        else levels.put(lvl,levels.get(lvl)+1);
                    }
                    JSONArray tag=problem.getJSONArray("tags");
                    for(int j=0;j<tag.length();j++) {
                        tags.add(tag.getString(j));
                    }

                    // to get the languages used and number of contests with their types

                    String lng=object.getString("programmingLanguage");
                    if (!lang.containsKey(lng)) lang.put(lng,1);
                    else lang.put(lng,lang.get(lng)+1);

                    String type=(object.getJSONObject("author")).getString("participantType");
                    if (!types.containsKey(type)) types.put(type,1);
                    else types.put(type,types.get(type)+1);


                }
                /*
                Log.d("Verdicts ",verdict.toString());
                Log.d("Levels ",levels.toString());
                Log.d("Tags ",tags.toString());
                Log.d("Languages ",lang.toString());
                Log.d("Types ",types.toString());
                */

                // details of user
                url=new URL(userInfo_url);
                urlConnection= (HttpURLConnection) url.openConnection();
                in=urlConnection.getInputStream();
                br=new BufferedReader(new InputStreamReader(in));
                line="";data="";
                while(line!=null) {
                    line=br.readLine();
                    data+=line;
                }
                root=new JSONObject(data);
                results=root.getJSONArray("result");
                JSONObject info=results.getJSONObject(0);
                String Username,con,rat,rank,orgn;
                try{ Username=info.getString("firstName")+" "+info.getString("lastName");}catch (JSONException e) {Username="";}
                try{ con=info.getString("country");} catch (JSONException e) {con="";}
                try{ rat=Integer.toString(info.getInt("rating"));} catch (JSONException e) {rat="";}
                try{ rank=info.getString("rank");} catch (JSONException e) {rank="";}
                try{ orgn=info.getString("organization");} catch (JSONException e) {orgn="";}

                userInfo=" Profile: \n\n Name: \t"+Username+"\n Rating: \t"+rat+"\n Rank: \t"+rank+"\n Country: \t"+con+"\n Organisation: "+orgn+"\n";

                // contest summary of user
                url=new URL(rating_url);
                urlConnection= (HttpURLConnection) url.openConnection();
                in=urlConnection.getInputStream();
                br=new BufferedReader(new InputStreamReader(in));
                line="";data="";
                while(line!=null) {
                    line=br.readLine();
                    data+=line;
                }
                root=new JSONObject(data);
                results=root.getJSONArray("result");
                for(int i=0;i<results.length();i++)
                {
                    JSONObject object=results.getJSONObject(i);
                    int r=object.getInt("newRating");
                    int tt=object.getInt("contestId");
                    ratingList.add(r); conIDs.add(tt);
                    String name=object.getString("contestName");
                    rank=Integer.toString(object.getInt("rank"));
                    String detail=name+" Rank: "+rank;
                    competedContest.add(detail);
                }

                // Log.d("Contest details ",competedContest.toString());

            } catch (Exception e) {
                e.printStackTrace();
                // Toast.makeText(getApplicationContext(),"Invalid data entered!",Toast.LENGTH_LONG).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Invalid handle entered !",Toast.LENGTH_LONG).show();
                    }
                });
                exception=e;
            }
            return exception;
        }

        @Override
        protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            dialog.dismiss();
            actionBar.setTitle("Handle: "+handle);
            tvInfo.setText(userInfo);
            // (new StatsView()).setStatsView(lang);
            // startActivity(new Intent(StatsActivity.this, com.example.cfstats.StatsView.class));

            /*
            * Displaying stats of user
            */

            // Pie Chart for Language Stats

            PieChart pieChart=findViewById(R.id.pie_chart);
            ArrayList<PieEntry> pieEntries=new ArrayList<>();
            ArrayList<_pieData> pieDataArrayList=new ArrayList<>();
            for(String ll:lang.keySet())
            {
                pieDataArrayList.add(new _pieData(ll,lang.get(ll)));
            }
            Log.d("Data added ","true");

            for(int i=0;i<pieDataArrayList.size();i++)
            {
                String ll=pieDataArrayList.get(i).getLang();
                int num=pieDataArrayList.get(i).getNum();
                pieEntries.add(new PieEntry(num,ll));
            }

            /*for(int i=0;i<pieEntries.size()-1;i++)
            {
                pieEntries.get(i).
            }*/

            Log.d("Entry added ","true");
            PieDataSet pieDataSet=new PieDataSet(pieEntries,"");
            pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
            pieDataSet.setValueTextSize(10f);
            pieDataSet.setSelectionShift(5.0f);
            pieDataSet.setValueFormatter(new DefaultValueFormatter(0));

            PieData pieData=new PieData(pieDataSet);
            pieData.setValueTextSize(8.5f);

            pieChart.setData(pieData);
            pieChart.setHoleRadius(0.0f);
            pieChart.setEntryLabelTextSize(0.0f);
            pieChart.setDrawHoleEnabled(false);
            Description desc=new Description(); desc.setText("Language Stats  ");
            desc.setTextSize(20f);
            pieChart.setDescription(desc);

            Legend legend=pieChart.getLegend();
            legend.setTextSize(11f);
            legend.setTextColor(getResources()
                    .getColor(R.color.colorPrimaryDark));
            legend.setWordWrapEnabled(true);
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);

            pieChart.animateXY(500,500);
            pieChart.invalidate();

            pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    PieEntry pe=(PieEntry) e;
                    Toast.makeText(getApplicationContext(),
                            pe.getLabel()+": "+Integer.toString((int)pe.getValue()),Toast.LENGTH_SHORT).show();
                    Log.d("item tapped", "true");
                }

                @Override
                public void onNothingSelected() {
                }
            });

            Log.d("Pie set up ","true");


            // Pie Chart for Verdicts

            TreeMap<String,Integer> verd=new TreeMap<>();
            for(String vdt:verdict.keySet())
            {
                switch(vdt)
                {
                    case "WRONG_ANSWER" : verd.put("WA",verdict.get(vdt));
                        break;
                    case "OK" : verd.put("AC",verdict.get(vdt));
                        break;
                    case "COMPILATION_ERROR" : verd.put("CE",verdict.get(vdt));
                        break;
                    case "RUNTIME_ERROR" : verd.put("RTE",verdict.get(vdt));
                        break;
                    case "TIME_LIMIT_EXCEEDED" : verd.put("TLE",verdict.get(vdt));
                        break;
                    case "MEMORY_LIMIT_EXCEEDED" : verd.put("MLE",verdict.get(vdt));
                        break;
                    case "CHALLENGED" : verd.put("Challenged",verdict.get(vdt));
                        break;
                    case "SKIPPED" : verd.put("Skipped",verdict.get(vdt));
                        break;
                    default: verd.put("Others",verdict.get(vdt));
                }
            }

//            Log.d("New verdict map ",verd.toString());

            PieChart pieChart2=findViewById(R.id.pie_chart2);
             ArrayList<PieEntry> pieEntries2=new ArrayList<>();
             ArrayList<_pieData> pieDataArrayList2=new ArrayList<>();

            for(String ll:verd.keySet())
            {
                pieDataArrayList2.add(new _pieData(ll,verd.get(ll)));
            }
            Log.d("Data added ","true");

            for(_pieData pd:pieDataArrayList2)
            {
                String ll=pd.getLang();
                int num=pd.getNum();
                pieEntries2.add(new PieEntry(num,ll));
            }
            Log.d("Entry added ","true");

            PieDataSet pieDataSet2=new PieDataSet(pieEntries2,"");
            pieDataSet2.setColors(ColorTemplate.VORDIPLOM_COLORS);
            pieDataSet2.setValueTextSize(10f);
            pieDataSet2.setSelectionShift(5.0f);
            pieDataSet2.setValueFormatter(new DefaultValueFormatter(0));

            PieData pieData2=new PieData(pieDataSet2);
            pieData2.setValueTextSize(8.5f);

            pieChart2.setData(pieData2);
            pieChart2.setHoleRadius(0.0f);
            pieChart2.setEntryLabelTextSize(0.0f);
            pieChart2.setDrawHoleEnabled(false);
            desc=new Description(); desc.setText("Verdict Stats  ");
            desc.setTextSize(20f);
            pieChart2.setDescription(desc);

            Legend legend2=pieChart2.getLegend();
            legend2.setTextSize(11f);
            legend2.setTextColor(getResources()
                    .getColor(R.color.colorPrimaryDark));
            legend2.setWordWrapEnabled(true);
            legend2.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);

            pieChart2.animateXY(500,500);
            pieChart2.invalidate();

            pieChart2.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    PieEntry pe=(PieEntry) e;
                    Toast.makeText(getApplicationContext(),
                            pe.getLabel()+": "+ (int) pe.getValue(),Toast.LENGTH_SHORT).show();
                    Log.d("item tapped", "true");
                }

                @Override
                public void onNothingSelected() {
                }
            });

            Log.d("Verdict pie set up ","true");

            // Bar chart for levels

            BarChart barChart=findViewById(R.id.bar_chart1);
            ArrayList<BarEntry> barEntries=new ArrayList<>();
            final ArrayList<String> labels=new ArrayList<>();
            ArrayList<_barData> barDataArrayList=new ArrayList<>();

            for(String lvl:levels.keySet())
            {
                barDataArrayList.add(new _barData(lvl,levels.get(lvl)));
            }
            for(int i=0;i<barDataArrayList.size();i++)
            {
                String m=barDataArrayList.get(i).getLvl();
                int v=barDataArrayList.get(i).getNum();
                barEntries.add(new BarEntry(i,v));
                labels.add(m);
            }
            Log.d("Bar entries ","true");

            BarDataSet barDataSet=new BarDataSet(barEntries,"");
            barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            barDataSet.setValueFormatter(new DefaultValueFormatter(0));

            Description desc3=new Description();
            desc3.setText("Level Stats "); desc3.setTextSize(20f);

            barChart.setDescription(desc3);
            BarData barData=new BarData(barDataSet);
            barChart.setData(barData);

            // setting X axis
            XAxis xAxis=barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
            xAxis.setTextSize(5f);

            // setting position of labels
            xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(false);
            xAxis.setGranularity(1f);
            xAxis.setLabelCount(labels.size());
            xAxis.setLabelRotationAngle(0);
            barChart.animateY(500);
            barChart.invalidate();

            Log.d("Bar set up ","true");


            barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    BarEntry be=(BarEntry) e;
                    Toast.makeText(getApplicationContext(),
                            labels.get((int)be.getX())+": "+(int)be.getY(),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected() {

                }
            });

            // TextView for Tags
            TextView tvTags=findViewById(R.id.tvTags);
            String disp_tags="\n \t TAGS: \n\n"; ArrayList<String> tag=new ArrayList<>();
            for(String t:tags)
            {
                tag.add(t);
            }
            tags.clear();
            for(int i=0;i<tag.size()-1;i+=2)
            {
                disp_tags +=" \t "+tag.get(i)+" , "+tag.get(i+1)+"\n";
            }
            tvTags.setText(disp_tags);
            Log.d("Tags displayed ","true");

            // Line chart for rating changes
//            Log.d("rating list ",ratingList.toString());
//            Log.d("IDs ",conIDs.toString());

            LineChart lineChart=findViewById(R.id.line_chart);

            ArrayList<Entry> entryArrayList=new ArrayList<>();
            for(int i=0;i<ratingList.size();i++)
            {
                entryArrayList.add(new Entry(conIDs.get(i),ratingList.get(i)));
            }

            LineDataSet lineDataSet=new LineDataSet(entryArrayList,"");
            lineDataSet.setCircleColors(ColorTemplate.MATERIAL_COLORS);
            lineDataSet.setHighLightColor(189);
            lineDataSet.setCircleRadius(4.0f);
            lineDataSet.setLineWidth(3.0f);

            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(lineDataSet);

            LineData data=new LineData(dataSets);

            Description desc4=new Description(); desc4.setText("Rating Change Curve");
            desc4.setTextSize(20f); lineChart.setDescription(desc4);

            lineChart.setData(data);
            lineChart.getAxisLeft().setDrawGridLines(false);
            lineChart.getXAxis().setDrawGridLines(false);
            lineChart.animateXY(800,800);
            lineChart.invalidate();

            lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    int idx=(int)e.getX();
                    int val=(int)e.getY();
                    Toast.makeText(getApplicationContext(),
                            "Contest ID: "+idx+"\nEvaluated Rating: "+val,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected() {

                }
            });

            Log.d("Line chart set up ","true");




        }

    }

    class _pieData
    {
        String lang;
        int num;
        public _pieData(String lang, int num) {
            this.lang = lang;
            this.num = num;
        }

        public String getLang() {
            return lang;
        }

        public int getNum() {
            return num;
        }
    }

    class _barData
    {
        String lvl;
        int num;
        public _barData(String lvl, int num) {
            this.lvl = lvl;
            this.num = num;
        }

        public String getLvl() {
            return lvl;
        }

        public int getNum() {
            return num;
        }
    }

}

