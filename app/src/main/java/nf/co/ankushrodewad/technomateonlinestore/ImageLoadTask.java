package nf.co.ankushrodewad.technomateonlinestore;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

    private String url;
    private ImageView imageView;

    public ImageLoadTask(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            Picasso
                    .get()
                    .load(url)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
