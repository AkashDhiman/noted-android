package com.ost.Noted;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.ost.Noted.R;

public class FirestoreAdapter extends FirestoreRecyclerAdapter<JournalModel, FirestoreAdapter.JournalViewHolder> {

    private OnListItemClick onListItemClick;

    public FirestoreAdapter(@NonNull FirestoreRecyclerOptions<JournalModel> options, OnListItemClick onListItemClick) {
        super(options);
        this.onListItemClick = onListItemClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull JournalViewHolder holder, int position, @NonNull JournalModel model) {
        holder.journal_name.setText(model.getName());
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_item_single, parent, false);
        return new JournalViewHolder(view);
    }

    public class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView journal_name;
        private String id;
        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            journal_name = itemView.findViewById(R.id.journal_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onListItemClick.onItemClick(getSnapshots().getSnapshot(getAdapterPosition()).getId(), getAdapterPosition());
        }
    }

    public interface OnListItemClick {
        void onItemClick(String id, int position);
    }
}
