package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.ContentValues;
import android.util.Log;
import android.net.Uri;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;
import br.ufpe.cin.if710.podcast.db.*;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    //TODO teste com outros links de podcast

    private ListView items;
    //private PodcastProvider database;
    //private PodcastDBHelper podhelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = (ListView) findViewById(R.id.items);

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

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Cursor c = database.query("title",podhelper.columns,null,null,null)
        if(isOnline(getApplicationContext())){
            new DownloadXmlTask().execute(RSS_FEED);
        } else {
            //Chamada da função para carregar o listview do banco;
           // throw new UnsupportedOperationException("Not yet implemented");
            List<ItemFeed> itemList = new ArrayList<>();
            Cursor c = getApplicationContext().getContentResolver().query(PodcastProviderContract.EPISODE_LIST_URI, null, "", null, null);
            int i = 0;
            while(c.moveToNext()){
                String item_title = c.getString(c.getColumnIndex(PodcastProviderContract.TITLE));
                String item_link = c.getString(c.getColumnIndex(PodcastProviderContract.EPISODE_LINK));
                String item_date = c.getString(c.getColumnIndex(PodcastProviderContract.DATE));
                String item_description = c.getString(c.getColumnIndex(PodcastProviderContract.DESCRIPTION));
                String item_download_link = c.getString(c.getColumnIndex(PodcastProviderContract.DOWNLOAD_LINK));
                String item_uri = c.getString(c.getColumnIndex(PodcastProviderContract.EPISODE_URI));
                i++;
                itemList.add(new ItemFeed(item_title, item_link, item_date, item_description, item_download_link));
            }

            Log.d("count",""+i);
            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, itemList);
            //atualizar o list view
            items.setAdapter(adapter);
            items.setTextFilterEnabled(true);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        XmlFeedAdapter adapter = (XmlFeedAdapter) items.getAdapter();
        adapter.clear();
    }


    private class DownloadXmlTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "iniciando...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();
            try {
                itemList = XmlFeedParser.parse(getRssFeed(params[0]));
                Save(itemList);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return itemList;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

            //Adapter Personalizado
            XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);

            //atualizar o list view
            items.setAdapter(adapter);
            items.setTextFilterEnabled(true);
            /*
            items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    XmlFeedAdapter adapter = (XmlFeedAdapter) parent.getAdapter();
                    ItemFeed item = adapter.getItem(position);
                    String msg = item.getTitle() + " " + item.getLink();
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
            /**/
        }
    }


    private void Save(List<ItemFeed> itemList){
        for(ItemFeed item : itemList){
            ContentValues contentValues = new ContentValues();
            contentValues.put(PodcastDBHelper.EPISODE_TITLE,item.getTitle());
            contentValues.put(PodcastDBHelper.EPISODE_DATE,item.getPubDate());
            contentValues.put(PodcastDBHelper.EPISODE_LINK,item.getLink());
            contentValues.put(PodcastDBHelper.EPISODE_DESC, item.getDescription());
            contentValues.put(PodcastDBHelper.EPISODE_DOWNLOAD_LINK, item.getDownloadLink());
            contentValues.put(PodcastDBHelper.EPISODE_FILE_URI,"");

            Uri uri = getContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI, contentValues);
            if(uri != null){
                Log.d("AddItem", "Item adicionado!");
            } else {
                Log.e("AddItem", "Falha ao adicionar o titulo " +item.getTitle());
            }
        }
    }

    //Verificação se o dispositivo está conectado a internet;
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected())
            return true;
        else
            return false;
    }


    //TODO Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }
}


