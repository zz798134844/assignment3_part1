package mg.studio.weatherappdesign;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void btnClick(View view) {

        new DownloadUpdate().execute();
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "https://api.openweathermap.org/data/2.5/forecast?q=Chongqing,cn&mode=json&units=metric&appid=87900c8dbea46b9af4ee9fb6a6aa3b6d";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    Log.d("TAG", line);
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        public int GetWeather(String wea)
        {
            if(wea.contains("Sunny"))
                return R.drawable.sunny_small;
            else if(wea.contains("Clouds"))
                return   R.drawable.partly_sunny_small;
            else if(wea.contains("Rain"))
                return  R.drawable.rainy_small;
            else
                return  R.drawable.windy_small;
        }


        @Override
        protected void onPostExecute(String JsonData) {
            if(JsonData==null||JsonData=="") {
                Toast.makeText(MainActivity.this, "The weather conditions update failed", Toast.LENGTH_SHORT).show();
                return;
            }
            else
                Toast.makeText(MainActivity.this,"The weather conditions was updated successfully",Toast.LENGTH_SHORT).show();

            //Update the temperature displayed
            ((TextView) findViewById(R.id.tv_location)).setText("Chongqing");
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(JsonData.substring(JsonData.indexOf("temp") + 6, JsonData.indexOf("temp_min") - 2));
            ((TextView) findViewById(R.id.tv_date)).setText(JsonData.substring(JsonData.indexOf("dt_txt") + 9, JsonData.indexOf(":00:00") - 3));

            String update_time=JsonData.substring(JsonData.indexOf("dt_txt") + 20, JsonData.indexOf(":00:00") + 6);
            int startIndex=0;
            int today=JsonData.indexOf(update_time,startIndex);
            startIndex=today+1;
            int next_one_day=JsonData.indexOf(update_time,startIndex);
            startIndex=next_one_day+1;
            int next_two_day=JsonData.indexOf(update_time,startIndex);
            startIndex=next_two_day+1;
            int next_three_day=JsonData.indexOf(update_time,startIndex);
            startIndex=next_three_day+1;
            int next_four_day=JsonData.indexOf(update_time,startIndex);

            String today_weather=JsonData.substring(JsonData.indexOf("main"),today);
            String next_one_day_weather=JsonData.substring(JsonData.indexOf("main"), next_one_day);
            String next_two_day_weather=JsonData.substring(JsonData.indexOf("main"), next_two_day);
            String next_three_day_weather=JsonData.substring(JsonData.indexOf("main"), next_three_day);
            String next_four_day_weather=JsonData.substring(JsonData.indexOf("main"), next_four_day);

            ((ImageView) findViewById(R.id.img_weather_condition)).setImageDrawable(getResources().getDrawable(GetWeather(today_weather)));
            ((ImageView) findViewById(R.id.img_weather_condition_next_one_day)).setImageDrawable(getResources().getDrawable(GetWeather(next_one_day_weather)));
            ((ImageView) findViewById(R.id.img_weather_condition_next_two_day)).setImageDrawable(getResources().getDrawable(GetWeather(next_two_day_weather)));
            ((ImageView) findViewById(R.id.img_weather_condition_next_three_day)).setImageDrawable(getResources().getDrawable(GetWeather(next_three_day_weather)));
            ((ImageView) findViewById(R.id.img_weather_condition_next_four_day)).setImageDrawable(getResources().getDrawable(GetWeather(next_four_day_weather)));

            String[] week = {"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
            Calendar calendar;
            calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int dayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;

            ((TextView) findViewById(R.id.tv_today)).setText(week[dayIndex]);
            ((TextView) findViewById(R.id.tv_next_one_day)).setText(week[dayIndex+1%7]);
            ((TextView) findViewById(R.id.tv_next_two_day)).setText(week[dayIndex+2%7]);
            ((TextView) findViewById(R.id.tv_next_three_day)).setText(week[dayIndex+3%7]);
            ((TextView) findViewById(R.id.tv_next_four_day)).setText(week[dayIndex+4%7]);
        }
    }
}
