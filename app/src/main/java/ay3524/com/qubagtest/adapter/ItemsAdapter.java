package ay3524.com.qubagtest.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashSet;

import ay3524.com.qubagtest.R;
import ay3524.com.qubagtest.model.Items;

/**
 * Created by Ashish on 07-03-2017.
 */

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.WallpaperViewHolder> {

    private ArrayList<Items> itemsList;
    private Context context;
    private ListItemClickListener clickListener;
    private HashSet<Items> itemsHashSet;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
    public ItemsAdapter(ArrayList<Items> items, ListItemClickListener listener) {
        itemsHashSet = new HashSet<>();
        itemsList = items;
        clickListener = listener;
    }
    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.single_item, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WallpaperViewHolder holder, int position) {
        final Items items = itemsList.get(position);
        holder.title.setText(items.getTitle());
        holder.size.setText(items.getSize());
        holder.price.setText("₹".concat(items.getPrice()));

        Glide.with(context)
                .load(items.getImage_url())
                .placeholder(R.mipmap.ic_launcher)
                //.error(R.drawable.sorry_no_image)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    removeThisItemFromHashSet(items);
                }else{
                    addThisItemToHashSet(items);
                }
            }
        });
    }

    private void removeThisItemFromHashSet(Items items) {
        itemsHashSet.add(items);
    }

    private void addThisItemToHashSet(Items items) {
        itemsHashSet.remove(items);
    }
    public HashSet<Items> getHashSet(){
        return itemsHashSet;
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class WallpaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        TextView title,price,size;
        AppCompatCheckBox checkBox;
        public WallpaperViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.item_image);
            title = (TextView) itemView.findViewById(R.id.item_title);
            price = (TextView) itemView.findViewById(R.id.item_price);
            size = (TextView) itemView.findViewById(R.id.item_size);
            checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.checkbox);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            clickListener.onListItemClick(clickedPosition);
        }
    }
}
