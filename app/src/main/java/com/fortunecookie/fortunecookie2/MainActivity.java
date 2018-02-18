package com.fortunecookie.fortunecookie2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;


import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

import static com.fortunecookie.fortunecookie2.R.id.weightChart;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String[][] listOfApps= {{"GOOG", "YouTube","com.google.android.youtube"},
            {"AMZN", "Amazon","com.amazon.mShop.android.shopping"},
            {"FB", "Facebook","com.facebook.katana"},{"FB","Messenger", "com.facebook.orca"},
            {"FB","WhatsApp","com.whatsapp"},
            {"FB","Instagram","com.instagram.android"}, {"SNAP","Snapchat","com.snapchat.android"},
            {"NFLX","Netflix","com.netflix.mediaclient"},
            {"PCLN","Priceline","com.priceline.android.negotiator"},
            {"EBAY","Ebay","com.ebay.mobile"}, {"EXPE","Expedia","com.expedia.bookings"},
            {"PYPL","PayPal","com.paypal.android.p2pmobile"},
            {"TWTR","Twitter","com.twitter.android"}, {"GRPN","Groupon","com.groupon"},
            {"GRUB","Seamless","com.seamlessweb.android.view"},
            {"GRUB","Grubhub","com.grubhub.android"}, {"YELP","Yelp","com.yelp.android"},
            {"MTCH","Tinder","com.tinder"}, {"MTCH","OkCupid","com.okcupid.okcupid"},
            {"MTCH","PlentyOfFish","com.pof.android"}, {"MTCH","Match","com.match.android.matchmobile"},
            {"P","Pandora","com.pandora.android"}, {"W","Wayfair","com.wayfair.wayfair"},
            {"TRIP","TripAdvisor","com.tripadvisor.tripadvisor"},
            {"TRIP","TripAdvisor Vacation Rentals","com.tripadvisor.android.vr.owner"},
            {"TRIP","SeatGuru","com.seatguru"}};

    private List<CustomUsageStat> randomUsageGenerator(String dateToStart){
        Random rand = new Random();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime beginDate = formatter.parseDateTime(dateToStart);
        DateTime endDate = beginDate.dayOfMonth().withMaximumValue();
        DateTime today = new DateTime();
        DateTimeComparator comparator = DateTimeComparator.getInstance();
        if (endDate.isAfter(today)) {
            endDate = today;
        }
        List<CustomUsageStat> resultList = new ArrayList<CustomUsageStat>();
        int numApps = listOfApps.length;
        int days = Days.daysBetween(beginDate.toLocalDate(), endDate.toLocalDate()).getDays();
        System.out.println(days);
        for(int i = 0; i <= days; i++){
            int numAppsInDay = rand.nextInt((12 - 5) + 1) + 5;
            for(int j = 0; j < numAppsInDay; j ++){
                String cur_date = formatter.print(beginDate);
                int indexOfApp = rand.nextInt(numApps);
                String ticker = listOfApps[indexOfApp][0];
                String name = listOfApps[indexOfApp][1];
                String package_name = listOfApps[indexOfApp][2];
                double  hours_used = rand.nextInt(2) + ((rand.nextInt(99)+ 10)/100.00);
                CustomUsageStat usageStat = new CustomUsageStat(package_name,
                        name, ticker, cur_date, hours_used);
                resultList.add(usageStat);
            }
            beginDate = beginDate.plusDays(1);
        }
        System.out.println(Arrays.toString(resultList.toArray()));
        return resultList;
    }

    public double getShareCount(String ticker, Date date, double dollarValue){
        Calendar from = Calendar.getInstance();
        from.setTime(date);
        Calendar to = Calendar.getInstance();
        to.setTime(date);
        to.add(Calendar.DATE, 30);

        Stock stock;
        try{
            System.out.println(ticker);
            stock = YahooFinance.get(ticker, from, to, Interval.WEEKLY);
            List<HistoricalQuote> quotes = stock.getHistory();
            System.out.println(quotes);
            HistoricalQuote quote = quotes.get(quotes.size() - 1);
            double price = quote.getClose().doubleValue();
            System.out.println("Price Found" + Double.toString(price));
            return dollarValue/price;
        }
        catch (IOException exception) {
            System.out.println("Unable to find price");
            return 1.0;
        }
    }

    public double getMarketValue(String ticker, double shareCount){
        Stock stock;
        try {
            stock = YahooFinance.get(ticker);
            double price = stock.getQuote().getPrice().doubleValue();
            return shareCount * price;
        } catch (IOException exception){
            return 1.0;
        }
    }

    float weights[] = {5, 2, 1, 2, 3, 2, 1, 3};
    String appNames[] = {"Facebook", "Tinder", "Twitter", "Netflix", "Instagram", "Whatsapp", "Grubhub", "PayPal"};

    float weights2 [] = {3, 5, 2, 1, 5, 3, 2, 2};
    String appNames2 [] = {"Facebook", "Tinder", "Twitter", "Netflix", "Instagram", "Whatsapp", "Grubhub", "PayPal"};

    Description description = new Description();

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NumberFormat format = NumberFormat.getCurrencyInstance();
        TextView portfolioValue = (TextView) findViewById(R.id.portfolioBalance);


        // Pie Chart Setup //
        List<CustomUsageStat> usage1 = randomUsageGenerator("2018-01-01");
        List<CustomUsageStat> usage2 = randomUsageGenerator("2018-02-01");
        PortfolioConstructor portfolio1 = new PortfolioConstructor(usage1);
        PortfolioConstructor portfolio2 = new PortfolioConstructor(usage2);

        double totalInitialInvestment = portfolio1.totalHours * portfolio1.perHourAmount;
        portfolioValue.setText(format.format(totalInitialInvestment));


        setupPieChart(portfolio1.names, portfolio1.weights);
//        setupPieChart2(portfolio2.names, portfolio2.weights);


        new portfolioGainSetUpActivity(portfolio1).execute();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Action Bar Format //

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
    }

    class portfolioGainSetUpActivity extends AsyncTask<String, Integer, Double>{

        private PortfolioConstructor portfolio;

        public portfolioGainSetUpActivity(PortfolioConstructor portfolio){
            this.portfolio = portfolio;
        }

        @Override
        protected Double doInBackground(String... params){
            Date initialDate = new Date(2018, 01, 31);

            double gain = 0.0;
            for(String key: this.portfolio.portfolio_investments.keySet()){
                double investmentValue = this.portfolio.portfolio_investments.get(key) * this.portfolio.perHourAmount;
                System.out.println(Double.toString(investmentValue) + " Invested in " + key);
                double shareCount = getShareCount(key, initialDate, investmentValue);
                System.out.println(Double.toString(shareCount) + " shares of " + key);
                double currMarketValue = getMarketValue(key, shareCount);
                System.out.println(Double.toString(currMarketValue) + " current market value of " + key);
                gain += (currMarketValue - investmentValue);
                System.out.println(Double.toString(currMarketValue - investmentValue) + " gain in " + key);
            }
            return gain;

        }

        protected void onPostExecute(Double gain){
            NumberFormat format = NumberFormat.getCurrencyInstance();
            TextView portfolioGain = (TextView) findViewById(R.id.portfolioGainLoss);
            portfolioGain.setText(format.format(1.2));

        }
    }

    private void setupPieChart2(String[] names, double[] weights) {
        //Colors: FB, Tinder, Twitter, Netflix, IG, WhatsApp, Grub, PayPal
        final int[] myColors2 = {
                ColorTemplate.rgb("#68b6b8"), ColorTemplate.rgb("#516887"),
                ColorTemplate.rgb("#abfdde"), ColorTemplate.rgb("#9b96d6"),
                ColorTemplate.rgb("#17475a"), ColorTemplate.rgb("#93beea"),
                ColorTemplate.rgb("#8d8cd7"), ColorTemplate.rgb("#2d4093"),
                ColorTemplate.rgb("#18415d"), ColorTemplate.rgb("#000518"),
                ColorTemplate.rgb("#50a99d"), ColorTemplate.rgb("#49a2ac"),
                ColorTemplate.rgb("#a8c687"), ColorTemplate.rgb("#7ba995"),
                ColorTemplate.rgb("#2894a0"), ColorTemplate.rgb("#cbd4c2"),
                ColorTemplate.rgb("#dbebc0"), ColorTemplate.rgb("#c3b299"),
                ColorTemplate.rgb("#815355"), ColorTemplate.rgb("#523249")
        };

        // Populating a list of pie entries //

        List<PieEntry> pieEntries2 = new ArrayList<>();
        for (int i = 0; i < weights.length; i++) {
            pieEntries2.add(new PieEntry((float)weights[i], names[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries2,"");
        dataSet.setColors(myColors2);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueLineColor(Color.BLACK);
        PieData data = new PieData(dataSet);

        // Get Chart //
        PieChart c2 = findViewById(R.id.weightChart2);
        c2.setData(data);
        c2.animateY(1000);
        c2.notifyDataSetChanged();
        c2.invalidate();
        c2.setDrawSliceText(false);
        c2.setUsePercentValues(false);

        // Description 'Unsophisticatedly' Removed //

        description.setText("");
        c2.setDescription(description);

        // Legend Styling//

        Legend l2 = c2.getLegend();
        l2.setEnabled(true);
        l2.setWordWrapEnabled(true);
        l2.setForm(Legend.LegendForm.CIRCLE);
        l2.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        // Interaction //

    }

    private void setupPieChart(String[] names, double[] weights) {
        //Colors: FB, Tinder, Twitter, Netflix, IG, WhatsApp, Grub, PayPal
        final int[] myColors = {
                ColorTemplate.rgb("#68b6b8"), ColorTemplate.rgb("#516887"),
                ColorTemplate.rgb("#abfdde"), ColorTemplate.rgb("#9b96d6"),
                ColorTemplate.rgb("#17475a"), ColorTemplate.rgb("#93beea"),
                ColorTemplate.rgb("#8d8cd7"), ColorTemplate.rgb("#2d4093"),
                ColorTemplate.rgb("#18415d"), ColorTemplate.rgb("#000518"),
                ColorTemplate.rgb("#50a99d"), ColorTemplate.rgb("#49a2ac"),
                ColorTemplate.rgb("#a8c687"), ColorTemplate.rgb("#7ba995"),
                ColorTemplate.rgb("#2894a0"), ColorTemplate.rgb("#cbd4c2"),
                ColorTemplate.rgb("#dbebc0"), ColorTemplate.rgb("#c3b299"),
                ColorTemplate.rgb("#815355"), ColorTemplate.rgb("#523249")
        };

        // Populating a list of pie entries //

        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < weights.length; i++) {
            pieEntries.add(new PieEntry((float) weights[i], names[i]));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries,"");
        dataSet.setColors(myColors);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueLineColor(Color.BLACK);
        PieData data = new PieData(dataSet);


        // Get Chart //
        PieChart c = findViewById(weightChart);
        c.setData(data);
        c.animateY(1000);
        c.notifyDataSetChanged();
        c.invalidate();
        c.setDrawSliceText(false);
        c.setUsePercentValues(false);

        // Description 'Unsophisticatedly' Removed //

        description.setText("");
        c.setDescription(description);

        // Legend Styling//

        Legend l = c.getLegend();
        l.setEnabled(true);
        l.setWordWrapEnabled(true);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        // Interaction //
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Navigation here:
        int id = item.getItemId();

        if (id == R.id.nav_portfolio) {
            Intent intent= new Intent(this, portfolio.class);
            startActivity(intent);

        } else if (id == R.id.nav_account) {
            //Intent intent= new Intent(this, Main8Activity.class);
            //startActivity(intent);

        } else if (id == R.id.nav_settings) {
            //Intent intent= new Intent(this, MainActivity7.class);
            //startActivity(intent);

        } else if (id == R.id.nav_logout) {
            Intent intent= new Intent(this, Main3Activity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
