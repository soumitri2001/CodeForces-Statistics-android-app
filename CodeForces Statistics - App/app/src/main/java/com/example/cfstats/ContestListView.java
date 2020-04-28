package com.example.cfstats;

public class ContestListView
{
    private String name,duration,start;

    public ContestListView(String name,String duration,String start)
    {
        this.duration=duration;
        this.name=name;
        this.start=start;
    }

    public String getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public String getStart() {
        return start;
    }
}
