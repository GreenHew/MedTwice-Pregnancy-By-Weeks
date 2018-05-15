package com.example.matthew.pregnancybyweeksapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class WeekVideoAndDescriptionFragment extends android.support.v4.app.Fragment {

    YouTubePlayerSupportFragment youTubePlayerSupportFragment;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    TextView bodyText;
    TextView welcomeText;
    HtmlParseTask parseTask;
    String youTubeSig;
    int weekNum;
    String link;
    boolean isWeekVideo = true;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.week_youtube_description_fragment, container, false);

        Bundle bundle = this.getArguments();
        assert bundle != null;
        weekNum = bundle.getInt("weekNum", -1);
        link = bundle.getString("link", "nope");
        welcomeText = view.findViewById(R.id.welcomeTextView);
        if (weekNum == -1)
            isWeekVideo = false;
        if (isWeekVideo) {
            String welcome = getString(R.string.welcome) + " " + weekNum;
            welcomeText.setText(welcome);
        }

        welcomeText.requestFocus();

        parseTask = new HtmlParseTask(this);
        parseTask.execute();

        bodyText = view.findViewById(R.id.bodyTextView);

        youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, youTubePlayerSupportFragment).commit();

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(youTubeSig);
                youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                    private boolean interceptPlay = true;
                    @Override
                    public void onPlaying() {
                        if (interceptPlay) {
                            youTubePlayer.pause();
                            interceptPlay = false;
                        }
                    }

                    @Override
                    public void onPaused() {

                    }

                    @Override
                    public void onStopped() {

                    }

                    @Override
                    public void onBuffering(boolean b) {

                    }

                    @Override
                    public void onSeekTo(int i) {

                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
        return view;
    }

    static private class HtmlParseTask extends AsyncTask<Integer, Integer, HtmlParser> {
        private WeakReference<WeekVideoAndDescriptionFragment> contextWeakReference;
        HtmlParseTask(WeekVideoAndDescriptionFragment context) {
            contextWeakReference = new WeakReference<>(context);
        }
        @Override
        protected HtmlParser doInBackground(Integer... integers) {
            WeekVideoAndDescriptionFragment fragment = contextWeakReference.get();
            HtmlParser htmlParser = null;
            try {
                if (fragment.isWeekVideo)
                    htmlParser = new HtmlParser(fragment.weekNum);
                else
                    htmlParser = new HtmlParser(fragment.link);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return htmlParser;
        }

        protected void onPostExecute(HtmlParser result) {
            WeekVideoAndDescriptionFragment fragment = contextWeakReference.get();

            StringBuilder body = new StringBuilder();
            for (String p : result.getParagraphs()) {
                body.append(p);
                body.append("\n\n");
            }
            fragment.youTubeSig = result.getYouTubeSignature();
            fragment.bodyText.setText(body.toString());
            fragment.setHelpfulLinks(result.getHelpfulLinks(), result.getHelpfulLinkTitles());
            if (!fragment.isWeekVideo)
                fragment.welcomeText.setText(result.getPageTitle());
            fragment.youTubePlayerSupportFragment.initialize(YouTubeConfig.getApiKey(), fragment.onInitializedListener);
        }
    }

    private void setHelpfulLinks(final ArrayList<String> links, final ArrayList<String> titles) {

        ListView listView = view.findViewById(R.id.helpfulLinks);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),
                android.R.layout.simple_list_item_1, titles);

        TextView helpfulLinksTextView = view.findViewById(R.id.helpfulLinksTextView);
        if (links.size() > 0)
            helpfulLinksTextView.setText(R.string.helpful_links);
        listView.setAdapter(arrayAdapter);
        setListViewHeightBasedOnChildren(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                Intent intent = new Intent(getActivity(), MedTwiceVideoActivity.class);
                intent.putExtra("link", links.get(position));
                startActivityForResult(intent, 1);
            }
        });
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
