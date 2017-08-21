package jru.medapp.ui.clinic.form;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import jru.medapp.R;
import jru.medapp.databinding.ItemSlotBinding;
import jru.medapp.model.data.AppointmentSlot;
import jru.medapp.model.data.Slot;

/**
 * Created by Mark Jansen Calderon on 1/12/2017.
 */

public class ClinicSlotListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private ClinicAppointmentView view;
    private List<Slot> list;
    private static final int VIEW_TYPE_MORE = 1;
    private static final int VIEW_TYPE_DEFAULT = 0;
    private boolean loading;

    public ClinicSlotListAdapter(ClinicAppointmentView view) {
        this.view = view;
        list = new ArrayList<>();

    }


    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_DEFAULT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemSlotBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_slot, parent, false);
        return new ClinicSlotListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ClinicSlotListAdapter.ViewHolder viewHolder = (ClinicSlotListAdapter.ViewHolder) holder;
        viewHolder.binding.setSlot(list.get(position));
        viewHolder.binding.setView(view);

        final Realm realm = Realm.getDefaultInstance();
        AppointmentSlot slot = realm.where(AppointmentSlot.class).equalTo("transTimeSlot", list.get(position).getSlotTime().trim()).findFirst();
        realm.close();
        if(slot!=null){
            viewHolder.binding.slotText.setEnabled(false);
            viewHolder.binding.slotText.setClickable(false);
            viewHolder.binding.slotText.setBackground(ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.rounded_slight_gray));
        }

    }

    public void setList(List<Slot> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemSlotBinding binding;

        public ViewHolder(ItemSlotBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
