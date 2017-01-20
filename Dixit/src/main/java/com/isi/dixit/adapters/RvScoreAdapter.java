package com.isi.dixit.adapters;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isi.dixit.R;
import com.isi.dixit.game.Score;

import java.util.List;

public class RvScoreAdapter extends RecyclerView.Adapter<RvScoreAdapter.ScoreViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private List<Score> mScores;

    public RvScoreAdapter(List<Score> scores) {
        this.mScores = scores;
    }

    @Override
    public ScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mItemView = inflater.inflate(R.layout.item_score, parent, false);
        return new ScoreViewHolder(mItemView);
    }

    @Override
    public void onBindViewHolder(ScoreViewHolder holder, int position) {
        holder.configure(mScores.get(position));
    }

    @Override
    public int getItemCount() {
        return mScores.size();
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder {
        private TextView mPlayerTv, mPointsTv;

         ScoreViewHolder(View itemView) {
            super(itemView);
            mPlayerTv = (TextView) itemView.findViewById(R.id.tvPlayer);
            mPointsTv = (TextView) itemView.findViewById(R.id.tvPoints);
        }

        void configure(Score score) {
            mPlayerTv.setText(score.name);
            mPointsTv.setText(score.points + "");
        }
    }

    public void setScores(List<Score> scores) {
        mScores = scores;
        notifyDataSetChanged();
    }
}
