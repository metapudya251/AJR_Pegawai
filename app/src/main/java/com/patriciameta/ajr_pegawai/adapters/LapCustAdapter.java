package com.patriciameta.ajr_pegawai.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.patriciameta.ajr_pegawai.R;
import com.patriciameta.ajr_pegawai.models.Transaksi;

import java.util.ArrayList;
import java.util.List;

public class LapCustAdapter extends RecyclerView.Adapter<LapCustAdapter.ViewHolder> implements Filterable {
    private List<Transaksi> transaksiList, filteredTransaksiList;
    private Context context;

    public LapCustAdapter(List<Transaksi> transaksiList, Context context) {
        this.transaksiList= transaksiList;
        filteredTransaksiList = new ArrayList<>(transaksiList);
        this.context = context;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charSequenceString = charSequence.toString();
                ArrayList<Transaksi> filtered = new ArrayList<>();

                if (charSequenceString.isEmpty()) {
                    filtered.addAll(transaksiList);
                } else {
                    for (Transaksi transaksi : transaksiList) {
                        if (transaksi.getNama_cust().toLowerCase()
                                .contains(charSequenceString.toLowerCase()))
                            filtered.add(transaksi);
                        
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults
                    filterResults) {
                ArrayList<Transaksi> transaksis = new ArrayList<>();
                filteredTransaksiList.clear();
                filteredTransaksiList.addAll((List<Transaksi>) filterResults.values);
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public LapCustAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rv_item_lap_cust, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LapCustAdapter.ViewHolder holder, int position) {
        Transaksi transaksi = filteredTransaksiList.get(position);

        holder.tvMobil.setText(transaksi.getNama_aset());
        holder.tvCust.setText(transaksi.getNama_cust());
        holder.tvJenis.setText(transaksi.getJenis_transaksi());
        holder.tvTotal.setText("Rp. " + transaksi.getPendapatan().toString());
        holder.tvJumlah.setText(transaksi.getJumlah().toString());
    }

    @Override
    public int getItemCount() {
        return filteredTransaksiList.size();
    }

    public void setTransaksiList(List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
        filteredTransaksiList = new ArrayList<>(transaksiList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMobil, tvTotal, tvJenis,tvJumlah, tvCust;
        CardView cvTransaksi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMobil = itemView.findViewById(R.id.tv_aset);
            tvCust = itemView.findViewById(R.id.tv_cust);
            tvJenis = itemView.findViewById(R.id.tv_jenis);
            tvJumlah = itemView.findViewById(R.id.tv_jml);
            tvTotal = itemView.findViewById(R.id.tv_total);
            cvTransaksi = itemView.findViewById(R.id.cv_transaksi);
        }
    }

}
