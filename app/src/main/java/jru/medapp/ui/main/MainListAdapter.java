package jru.medapp.ui.main;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jru.medapp.R;
import jru.medapp.app.Constants;
import jru.medapp.databinding.ItemClinicBinding;
import jru.medapp.model.data.Clinic;

/**
 * Created by Mark Jansen Calderon on 1/12/2017.
 */

public class MainListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private MainView mainView;
    private List<Clinic> list;
    private static final int VIEW_TYPE_MORE = 1;
    private static final int VIEW_TYPE_DEFAULT = 0;
    private boolean loading;

    public MainListAdapter(MainView mainView) {
        this.mainView = mainView;
        list = new ArrayList<>();

    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemClinicBinding itemClinicBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_clinic, parent, false);
        return new MainListAdapter.ViewHolder(itemClinicBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainListAdapter.ViewHolder viewHolder = (MainListAdapter.ViewHolder) holder;
        viewHolder.itemClinicBinding.setClinic(list.get(position));
        viewHolder.itemClinicBinding.setView(mainView);

    }

    public void setList(List<Clinic> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }


    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemClinicBinding itemClinicBinding;

        public ViewHolder(ItemClinicBinding itemClinicBinding) {
            super(itemClinicBinding.getRoot());
            this.itemClinicBinding = itemClinicBinding;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
