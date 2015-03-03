package com.example.anisjr.currency;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.net.Uri;
import android.text.format.Time;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import android.widget.AdapterView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */
public class HistoryFragment extends Fragment {

    ArrayAdapter<String> mHistoryAdapter;

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            FetchCurrencyTask currencyTask = new FetchCurrencyTask();
            //currencyTask.execute("USD");
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String location = prefs.getString(getString(R.string.pref_currencyFrom_key),
                              getString(R.string.pref_currencyFrom_default));
                      currencyTask.execute(location);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create some dummy data for the ListView.  Here's a sample history
        String[] data = {
                "Mon 6/23 - EUR-TND - 2.2",
                "Tue 6/24 - EUR-TND - 2.1",
                "Wed 6/25 - EUR-TND - 2.0",
                "Thurs 6/26 - EUR-TND - 1.9",
                "Fri 6/27 - EUR-TND - 1.8",
                "Sat 6/28 - EUR-TND - 1.7",
                "Sun 6/29 - EUR-TND - 1.6"

        };
        List<String> weekHistory = new ArrayList<String>(Arrays.asList(data));

        // The ArrayAdapter will take data from a source (like our dummy history)
        //and use it to populate the ListView it's attached to.
        mHistoryAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_history, // The name of the layout ID.
                        R.id.list_item_history_textview, // The ID of the textview to populate.
                        weekHistory);


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_history);
        listView.setAdapter(mHistoryAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                              String forecast = mHistoryAdapter.getItem(position);
                              Intent intent = new Intent(getActivity(), DetailActivity.class)
                                                   .putExtra(Intent.EXTRA_TEXT, forecast);
                                startActivity(intent);
                           }
                   });


        return rootView;
    }

    public class FetchCurrencyTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchCurrencyTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         */
        private String[] getCurrencyDataFromJson(String historyJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RATES = "rates";
            final String OWM_utctime = "utctime";
            final String OWM_rate = "rate";


            JSONObject historyJson = new JSONObject(historyJsonStr);
            JSONObject daysHistory = historyJson.getJSONObject("rates");

                 int i=0;
            String[] resultStrs = new String[numDays];

            Iterator<String> keys = daysHistory.keys();
            String date;


             while (keys.hasNext()) {
                date = (String) keys.next();
                JSONObject values = daysHistory.getJSONObject(date);
                Double histrate = values.getDouble("rate");
                resultStrs[i] =  date + " - " + histrate;
                i++;

            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "History entry: " + s);
            }
            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            // If there's no currency code, there's nothing to look up.  Verify size of params.
                       if (params.length == 0) {
                                return null;
                            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String historyJsonStr = null;

            String toCurrency = "TND";
            int numDays = 7;

            try {
                // Construct the URL for the  query
                //URL url = new URL("http://jsonrates.com/historical/?from=USD&to=TND&dateStart=2014-06-17&dateEnd=2014-06-23");

                final String FORECAST_BASE_URL =
                                        "http://jsonrates.com/historical/?";
                                final String fromCurrency_PARAM = "from";
                                final String toCurrency_PARAM = "to";
                                final String dateStart_PARAM = "dateStart";
                                final String dateEnd_PARAM = "dateEnd";

                                        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                                               .appendQueryParameter(fromCurrency_PARAM, params[0])
                                                .appendQueryParameter(toCurrency_PARAM,toCurrency)
                                                .appendQueryParameter(dateStart_PARAM, "2014-06-17")
                                               .appendQueryParameter(dateEnd_PARAM,"2014-06-23" )
                                                .build();

                                      URL url = new URL(builtUri.toString());

                                        Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request , and open the connection
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                historyJsonStr = buffer.toString();

                Log.v(LOG_TAG,"History JSON String: "+historyJsonStr);
            } catch (IOException e) {
                Log.e("HistoryFragment", "Error ", e);
                // If the code didn't successfully get the data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("HistoryFragment", "Error closing stream", e);
                    }
                }
            }

            try {
                               return getCurrencyDataFromJson(historyJsonStr, numDays);
                           } catch (JSONException e) {
                               Log.e(LOG_TAG, e.getMessage(), e);
                              e.printStackTrace();
                           }

                               // This will only happen if there was an error getting or parsing the history.
            return null;
        }

        @Override
               protected void onPostExecute(String[] result) {
                        if (result != null) {
                                mHistoryAdapter.clear();
                                for(String dayHistoryStr : result) {
                                        mHistoryAdapter.add(dayHistoryStr);
                                    }
                                // New data is back from the server.  Hooray!
                                    }
                    }
    }
}