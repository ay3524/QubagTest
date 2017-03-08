package ay3524.com.qubagtest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import ay3524.com.qubagtest.model.Categories;
import ay3524.com.qubagtest.R;

/**
 * Created by Ashish on 06-03-2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.WallpaperViewHolder> {

    private ArrayList<Categories> categories;
    private Context context;
    private ListItemClickListener clickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public CategoryAdapter(ArrayList<Categories> categoryList, ListItemClickListener listener) {
        categories = categoryList;
        clickListener = listener;
    }

    @Override
    public WallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.single_category_item, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WallpaperViewHolder holder, final int position) {

        holder.textView.setText(categories.get(position).getTitle());

        Glide.with(context)
                .load(categories.get(position).getImage_url())
                .placeholder(R.mipmap.ic_launcher)
                //.error(R.drawable.sorry_no_image)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ImageView imageView;
        TextView textView;
        WallpaperViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.category_image);
            textView = (TextView) itemView.findViewById(R.id.category_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            clickListener.onListItemClick(clickedPosition);
        }
    }
}
