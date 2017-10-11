package br.ufpe.cin.if710.podcast.ui.adapter;

import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.service.DownloadService;
import br.ufpe.cin.if710.podcast.ui.EpisodeDetailActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class XmlFeedAdapter extends ArrayAdapter<ItemFeed> {

    int linkResource;
    public static String TITLE = "Title";
    public static String DESCRIPTION = "Description";
    public static String uri;

    public XmlFeedAdapter(Context context, int resource, List<ItemFeed> objects) {
        super(context, resource, objects);
        linkResource = resource;
    }

    /**
     * public abstract View getView (int position, View convertView, ViewGroup parent)
     * <p>
     * Added in API level 1
     * Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a root view and to prevent attachment to the root.
     * <p>
     * Parameters
     * position	The position of the item within the adapter's data set of the item whose view we want.
     * convertView	The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
     * parent	The parent that this view will eventually be attached to
     * Returns
     * A View corresponding to the data at the specified position.
     */


	/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.itemlista, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.item_title);
		textView.setText(items.get(position).getTitle());
	    return rowView;
	}
	/**/

    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     static class ViewHolder {
        TextView item_title;
        TextView item_date;
        Button baixar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), linkResource, null);
            holder = new ViewHolder();
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
            holder.baixar = (Button) convertView.findViewById(R.id.baixar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ItemFeed item = getItem(position);

        holder.item_title.setText(getItem(position).getTitle());
        holder.item_date.setText(getItem(position).getPubDate());
        uri = getItem(position).getDownloadLink();


        /*Criando o evento de clicar na lista e também já criando o intent para abrir a outra tela
        * passando o titulo e a descrição
        */
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =  new Intent(getContext(), EpisodeDetailActivity.class);
                i.putExtra(TITLE, item.getTitle());
                i.putExtra(DESCRIPTION, item.getDescription());
                i.setFlags(FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);

            }
        });



        holder.baixar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), DownloadService.class);
                i.setData(Uri.parse(uri));
                Toast.makeText(getContext(), "baixando...", Toast.LENGTH_SHORT).show();
                getContext().startService(i);
            }
        });

        return convertView;
    }
}
