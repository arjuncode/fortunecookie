package com.fortunecookie.fortunecookie2;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by parth on 2/17/2018.
 */

public class PortfolioConstructor {
    public double perHourAmount = 0.1;
    public double totalHours = 0;
    public Hashtable<String, Double> portfolio_investments = new Hashtable<String, Double>();
    public Hashtable<String, Double> portfolio_weights = new Hashtable<String, Double>();
    public String[] names;
    public double[] weights;

    public PortfolioConstructor(List<CustomUsageStat> usageStats) {
        for(int i = 0; i < usageStats.size(); i++){
            CustomUsageStat usageStat = (CustomUsageStat) usageStats.get(i);
            this.totalHours += (usageStat.hours_used);
            if (this.portfolio_investments.containsKey(usageStat.ticker)) {
                this.portfolio_investments.put(usageStat.ticker,
                        ((this.portfolio_investments.get(usageStat.ticker) + usageStat.hours_used)) );
            }
            else {
                this.portfolio_investments.put(usageStat.ticker, (usageStat.hours_used));
            }
        }


        for(String key: this.portfolio_investments.keySet()) {
            this.portfolio_weights.put(key, this.portfolio_investments.get(key)/this.totalHours);
        }

        this.names = this.portfolio_weights.keySet().toArray(new String[this.portfolio_weights.keySet().size()]);
        this.weights = new double[this.names.length];
        for(int i = 0; i < this.names.length; i++){
            this.weights[i] = this.portfolio_weights.get(this.names[i]);
        }
    }

    @Override
    public String toString(){
        return this.portfolio_weights.toString() + "Total Hours: " + this.totalHours +
                this.portfolio_investments.toString() + this.names.toString() + this.weights.toString();
    }

}