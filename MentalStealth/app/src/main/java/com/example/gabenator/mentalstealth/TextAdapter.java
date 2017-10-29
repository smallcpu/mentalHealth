package com.example.gabenator.mentalstealth;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.Features;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.SentimentOptions;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Gabenator on 10/28/2017.
 */

public class TextAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Message> messageList;
    private ViewTexts viewTextsAct;

    private class AskWatsonTask extends AsyncTask<String, Void, Double> {

        @Override
        protected Double doInBackground(String... textsToAnalyse) {
            /*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("In thread", "what is happening inside a thread - we are running Watson AlchemyAPI");
                }
            });*/

            NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                    NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                    "cbf3ee71-755c-4c7c-a77f-7013f725f3f7",
                    "5JIDp7wGZ5jO"
            );



            String text = textsToAnalyse[0];
            SentimentOptions sentiment = new SentimentOptions.Builder().build();
            Features features = new Features.Builder().sentiment(sentiment).build();
            AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(text).language("en").features(features).build();
            AnalysisResults response = service.analyze(parameters).execute();
            double val = parseJson(response.toString());

            return val;
        }

    }

    public TextAdapter(List<Message> sList, ViewTexts ma) {
        this.messageList = sList;
        viewTextsAct = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "MAKING NEW");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_text_row, parent, false);

        itemView.setOnClickListener(viewTextsAct);
        itemView.setOnLongClickListener(viewTextsAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.textNumber.setText(message.getNumber());
        holder.textContent.setText(message.getContent());
        Date d = new Date(message.getDate_Sent() * 1000);
        holder.textDate.setText(d.toString());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        double val = getVal(message.getContent());
        holder.rating.setText(Double.toString(val));
    }

    public double getVal(String textsToAnalyse) {
        NaturalLanguageUnderstanding service = new NaturalLanguageUnderstanding(
                NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27,
                "{key}",
                "{username}"
        );

        String text = textsToAnalyse;
        SentimentOptions sentiment = new SentimentOptions.Builder().build();
        Features features = new Features.Builder().sentiment(sentiment).build();
        AnalyzeOptions parameters = new AnalyzeOptions.Builder().text(text).language("en").features(features).build();
        AnalysisResults response = service.analyze(parameters).execute();
        double val = parseJson(response.toString());

        return val;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static double parseJson(String s) {
        int size;
        double rating = 0.0;

        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONObject sent = jObjMain.getJSONObject("sentiment");
            JSONObject doc = sent.getJSONObject("document");
            rating = doc.getDouble("score");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rating;
    }


}
