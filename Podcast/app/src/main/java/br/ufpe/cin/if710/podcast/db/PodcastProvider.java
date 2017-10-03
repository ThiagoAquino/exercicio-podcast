package br.ufpe.cin.if710.podcast.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentUris;


public class PodcastProvider extends ContentProvider {
    private PodcastDBHelper podcastDBHelper;

    public PodcastProvider() {
    }


    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        // Feito
        Context context = this.getContext();
        podcastDBHelper = PodcastDBHelper.getInstance(context);
        return true;
    }

    //Remoção
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        //throw new UnsupportedOperationException("Not yet implemented");
        int num;
        num = this.podcastDBHelper.getWritableDatabase().delete(PodcastProviderContract.EPISODE_TABLE, selection,selectionArgs);

        if(num != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return num;
    }


    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //Inserção
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
      final SQLiteDatabase database = podcastDBHelper.getWritableDatabase();
      long id = database.insert(PodcastProviderContract.EPISODE_TABLE, null, values);
      Uri retorno = null;
      if (id > 0) {
            //inserção feita com sucesso
            retorno = ContentUris.withAppendedId(PodcastProviderContract.EPISODE_LIST_URI, id);
            Log.d("PodcastProvider", "inserindo" + retorno.toString());
      } else {
            throw new android.database.SQLException("Falha na inserção em: " +uri);

      }
      return retorno;
    }

    //Consulta
    @Override
    public Cursor query(Uri uri, String [] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
        //throw new UnsupportedOperationException("Not yet implemented");

        //referência ao banco
        final SQLiteDatabase database = podcastDBHelper.getReadableDatabase();
        //Consultando o banco
        Cursor cursor = database.query(PodcastProviderContract.EPISODE_TABLE, projection,selection,selectionArgs,null,null,sortOrder);
        //Alerta de mudanças na URI
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
