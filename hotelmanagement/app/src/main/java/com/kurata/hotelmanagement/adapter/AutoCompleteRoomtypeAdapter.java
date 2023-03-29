package com.kurata.hotelmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kurata.hotelmanagement.R;
import com.kurata.hotelmanagement.data.model.Roomtype;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteRoomtypeAdapter extends ArrayAdapter<Roomtype> {
    private List<Roomtype> roomtypeListFull;

    public AutoCompleteRoomtypeAdapter(@NonNull Context context, @NonNull List<Roomtype> roomtype) {
        super(context, 0, roomtype);
        roomtypeListFull = new ArrayList<>(roomtype);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return hoteltypeFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.autocomplete, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.txt_name);

        Roomtype RoomItem = getItem(position);

        if (RoomItem != null) {
            textViewName.setText(RoomItem.getName());
        }

        return convertView;
    }

    private Filter hoteltypeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Roomtype> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(roomtypeListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Roomtype item : roomtypeListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Roomtype) resultValue).getName();
        }
    };
}
