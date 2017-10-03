package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

public class EpisodeDetailActivity extends Activity {
    private TextView title;
    private TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        this.title = (TextView) findViewById(R.id.title);
        this.desc = (TextView) findViewById(R.id.desc);
        //TODO preencher com informações do episódio clicado na lista...

        this.title.setText(getIntent().getExtras().getString(XmlFeedAdapter.TITLE));
        this.desc.setText(getIntent().getExtras().getString(XmlFeedAdapter.DESCRIPTION));
    }
}
