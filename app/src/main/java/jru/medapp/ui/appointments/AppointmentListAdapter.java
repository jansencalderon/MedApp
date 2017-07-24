package jru.medapp.ui.appointments;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import jru.medapp.R;
import jru.medapp.databinding.ItemAppointmentBinding;
import jru.medapp.model.data.Appointment;
/**
 * Created by Mark Jansen Calderon on 1/12/2017.
 */

public class AppointmentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private AppointmentView view;
    private List<Appointment> list;
    private static final int VIEW_TYPE_MORE = 1;
    private static final int VIEW_TYPE_DEFAULT = 0;
    private boolean loading;

    public AppointmentListAdapter(AppointmentView view) {
        this.view = view;
        list = new ArrayList<>();

    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAppointmentBinding itemAppointmentBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_appointment, parent, false);
        return new AppointmentListAdapter.ViewHolder(itemAppointmentBinding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AppointmentListAdapter.ViewHolder viewHolder = (AppointmentListAdapter.ViewHolder) holder;
        viewHolder.itemAppointmentBinding.setAppointment(list.get(position));
        viewHolder.itemAppointmentBinding.setView(view);

    }

    public void setList(List<Appointment> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemAppointmentBinding itemAppointmentBinding;

        public ViewHolder(ItemAppointmentBinding itemAppointmentBinding) {
            super(itemAppointmentBinding.getRoot());
            this.itemAppointmentBinding = itemAppointmentBinding;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
