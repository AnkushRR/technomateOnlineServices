package nf.co.ankushrodewad.technomateonlinestore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ProductViewHolder> {

    Context context;
    LinearLayout[] layouts;
    View view1;
    ProductViewHolder viewHolder1;
    public RecyclerViewAdapter(Context context1, LinearLayout[] layouts1){
        context = context1;
        layouts = new LinearLayout[1000];
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        view1 = LayoutInflater.from(context).inflate(R.layout.template_product,viewGroup,false);
        viewHolder1 = new ProductViewHolder(view1);

        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i) {

    }


    @Override
    public int getItemCount() {
        return layouts.length;
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder{

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
