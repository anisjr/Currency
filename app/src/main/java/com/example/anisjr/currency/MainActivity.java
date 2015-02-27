package com.example.anisjr.currency;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        ArrayAdapter<String> mHistoryAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Create some dummy data for the ListView.  Here's a sample history
            String[] data = {
                    " 2014 - EUR-TND - 2.2",
                    " 2013 - EUR-TND - 2.1",
                    " 2012 - EUR-TND - 2.0",
                    " 2011 - EUR-TND - 1.9",
                    " 2010 - EUR-TND - 1.8",
                    " 2009 - EUR-TND - 1.7",
                    " 2008 - EUR-TND - 1.6"
            };
            List<String> yearHistory = new ArrayList<String>(Arrays.asList(data));

            // The ArrayAdapter will take data from a source (like our dummy history)
            //and use it to populate the ListView it's attached to.
            mHistoryAdapter =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_history, // The name of the layout ID.
                            R.id.list_item_history_textview, // The ID of the textview to populate.
                            yearHistory);


            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            // Get a reference to the ListView, and attach this adapter to it.
                       ListView listView = (ListView) rootView.findViewById(R.id.listview_history);
                        listView.setAdapter(mHistoryAdapter);

            return rootView;
        }
    }
}
