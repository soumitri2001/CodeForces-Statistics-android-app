/*
package com.example.cfstats;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class StatsView extends AppCompatActivity
{
    BarChart barChart;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> labels;
    ArrayList<SampleData> dataArrayList=new ArrayList<>();

    PieChart pieChart;
    ArrayList<PieEntry> pieEntries;
    ArrayList<Sample_pieData> pieDataArrayList=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stats_layout);

        barChart=(BarChart)findViewById(R.id.bar_chart1);

        barEntries=new ArrayList<>();
        labels=new ArrayList<>();

        dataArrayList.add(new SampleData("Jan",10));
        dataArrayList.add(new SampleData("Feb",25));
        dataArrayList.add(new SampleData("Mar",14));
        dataArrayList.add(new SampleData("Apr",12));
        dataArrayList.add(new SampleData("May",75));
        dataArrayList.add(new SampleData("Jun",110));
        dataArrayList.add(new SampleData("Jul",156));
        dataArrayList.add(new SampleData("Aug",165));
        dataArrayList.add(new SampleData("Sep",125));
        dataArrayList.add(new SampleData("Oct",52));
        dataArrayList.add(new SampleData("Nov",31));
        dataArrayList.add(new SampleData("Dec",12));


        for(int i=0;i<dataArrayList.size();i++)
        {
            String m=dataArrayList.get(i).getMonth();
            int v=dataArrayList.get(i).getSales();
            barEntries.add(new BarEntry(i,v));
            labels.add(m);
        }

        BarDataSet barDataSet=new BarDataSet(barEntries,"Monthly indices");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        Description desc=new Description();
        desc.setText("");
        barChart.setDescription(desc);
        BarData barData=new BarData(barDataSet);
        barChart.setData(barData);

        // setting X axis
        XAxis xAxis=barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        // setting position of labels
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setLabelRotationAngle(0);
        barChart.animateY(1000);
        barChart.invalidate();

        //setting pie chart
        pieChart=(PieChart) findViewById(R.id.pie_chart);
        pieEntries=new ArrayList<>();

        pieDataArrayList.add(new Sample_pieData("Java 11",255));
        pieDataArrayList.add(new Sample_pieData("C++ 17",150));
        pieDataArrayList.add(new Sample_pieData("C++ 14",15));
        pieDataArrayList.add(new Sample_pieData("Python 3",37));
        pieDataArrayList.add(new Sample_pieData("Kotlin",5));
        pieDataArrayList.add(new Sample_pieData("PyPy 3",24));
        pieDataArrayList.add(new Sample_pieData("Java 8",96));

        for(int i=0;i<pieDataArrayList.size();i++)
        {
            String lang=pieDataArrayList.get(i).getLang();
            int num=pieDataArrayList.get(i).getNum();
            pieEntries.add(new PieEntry(num,lang));
            Log.d("Entry added ","true");
        }
        PieDataSet pieDataSet=new PieDataSet(pieEntries,"Languages");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setSelectionShift(4.0f);

        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(8.5f);

        pieChart.setData(pieData);
        pieChart.setHoleRadius(0.0f);
        pieChart.setEntryLabelTextSize(9.5f);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setDescription(desc);

        Legend legend=pieChart.getLegend();
        legend.setTextSize(13);
        legend.setTextColor(getResources()
                .getColor(R.color.colorPrimaryDark));
        legend.setWordWrapEnabled(true);

        pieChart.animateXY(1000,1000);
        pieChart.invalidate();


        // setting LineChart
        LineChart lineChart=findViewById(R.id.line_chart);

        ArrayList<Entry> entryArrayList=new ArrayList<>();
        entryArrayList.add(new Entry(0,20));
        entryArrayList.add(new Entry(1,24));
        entryArrayList.add(new Entry(2,2));
        entryArrayList.add(new Entry(3,10));

        LineDataSet lineDataSet=new LineDataSet(entryArrayList,"");
        lineDataSet.setCircleColors(ColorTemplate.MATERIAL_COLORS);
        lineDataSet.setHighLightColor(189);
        lineDataSet.setCircleRadius(4.0f);
        lineDataSet.setLineWidth(3.0f);


        ArrayList<ILineDataSet> dataSets=new ArrayList<>();
        dataSets.add(lineDataSet);

        LineData data=new LineData(dataSets);


        lineChart.setData(data);
        lineChart.invalidate();

        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int idx=(int)e.getX();
                int val=(int)e.getY();
                Toast.makeText(getApplicationContext(),
                        "X: "+idx+"\nValue: "+val,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        Log.d("Line chart set up ","true");

    }

    */
/**
    public void setStatsView(TreeMap<String,Integer> lang)
    {
        PieChart pieChart;
        ArrayList<PieEntry> pieEntries;
        ArrayList<Sample_pieData> pieDataArrayList=new ArrayList<>();

        pieChart=findViewById(R.id.pie_chart);
        pieEntries=new ArrayList<>();

        for(String ll:lang.keySet())
        {
            pieDataArrayList.add(new Sample_pieData(ll,lang.get(ll)));
;       }
        Log.d("Data added ","true");

        for(int i=0;i<pieDataArrayList.size();i++)
        {
            String ll=pieDataArrayList.get(i).getLang();
            int num=pieDataArrayList.get(i).getNum();
            pieEntries.add(new PieEntry(num,ll));
        }
        Log.d("Entry added ","true");
        PieDataSet pieDataSet=new PieDataSet(pieEntries,"");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setSelectionShift(4.0f);

        PieData pieData=new PieData(pieDataSet);
        pieData.setValueTextSize(8.5f);

        pieChart.setData(pieData);
        pieChart.setHoleRadius(0.0f);
        pieChart.setEntryLabelTextSize(9.5f);
        pieChart.setDrawHoleEnabled(false);
        Description desc=new Description(); desc.setText("Language Stats");
        pieChart.setDescription(desc);

        Legend legend=pieChart.getLegend();
        legend.setTextSize(13);
        legend.setTextColor(getResources()
                .getColor(R.color.colorPrimaryDark));
        legend.setWordWrapEnabled(true);

        pieChart.animateXY(1000,1000);
        pieChart.invalidate();
    }
     **//*





    class Sample_pieData
    {
        String lang;
        int num;
        public Sample_pieData(String lang, int num) {
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

    class SampleData
    {
        String month;
        int sales;
        public SampleData(String month,int sales)
        {
            this.month=month;
            this.sales=sales;
        }

        public String getMonth() {
            return month;
        }

        public int getSales() {
            return sales;
        }
    }
}
*/
